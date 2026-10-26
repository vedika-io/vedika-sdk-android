package io.vedika.sdk

import io.vedika.sdk.exceptions.VedikaApiError
import io.vedika.sdk.exceptions.VedikaAuthError
import io.vedika.sdk.exceptions.VedikaIdempotencyNotSupportedError
import io.vedika.sdk.exceptions.VedikaInsufficientCredits
import io.vedika.sdk.exceptions.VedikaRateLimitError
import io.vedika.sdk.exceptions.VedikaServerError
import io.vedika.sdk.internal.OriginPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Main client for the Vedika Intelligence API — Vastu surface.
 *
 * Instantiate with your API key and call into [vastu]:
 *
 * ```kotlin
 * val client = VedikaClient(apiKey = "vk_live_...")
 * val score = client.vastu.vastuScore("overall", mapOf("rooms" to rooms))
 * ```
 *
 * First deliverable scope: the Vastu
 * domain only, not the full 23-domain surface `sdks/flutter` covers. New
 * domains get their own `*Service` class alongside [VastuService], exposed
 * as a new `val` here — this class's own shape (construction, origin policy,
 * `get`/`post` primitives, response handling) does not need to change to
 * add one.
 */
class VedikaClient @JvmOverloads constructor(
    apiKey: String,
    baseUrl: String = VedikaConfig.DEFAULT_BASE_URL,
    timeoutMs: Long = VedikaConfig.DEFAULT_TIMEOUT_MS,
    allowInsecureHttp: Boolean = false,
) {
    companion object {
        private const val SDK_VERSION = "vedika-android/2.0.0"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

        /**
         * Two extra attempts for network failures and 5xx responses. Either
         * can follow a completed charge, so billed requests must retain their
         * idempotency key. A rejected 4xx request is never retried.
         */
        private const val MAX_RETRIES = 2

        private val BILLED_VASTU_GET_PATHS = listOf("/v2/vastu/", "/v2/astrology/vastu/")
            .flatMap { prefix ->
                listOf(
                    "reference/directions/8",
                    "reference/directions/16",
                    "reference/directions/32",
                    "reference/mandala/9-zone",
                    "reference/mandala/45-devatas",
                    "reference/mandala/64-pada",
                    "reference/defects/catalog",
                    "reference/remedies/catalog",
                    "reference/colors-by-zone",
                    "reference/materials-by-zone",
                    "reference/gate-obstructions",
                    "direction/declination",
                ).map { prefix + it }
            }.toSet()

        private val IDEMPOTENCY_HEADER_NAMES = listOf("Idempotency-Key", "X-Idempotency-Key")

        /**
         * Header spelling per operation. The live spec names `X-Idempotency-Key` on
         * `/api/v1/astrology/query` and `Idempotency-Key` on the Vastu operations.
         */
        internal fun idempotencyHeaderName(path: String): String =
            if (path.substringBefore("?") == "/api/v1/astrology/query") "X-Idempotency-Key" else "Idempotency-Key"

        /**
         * True for the paths where the client may attach a key the caller did not
         * supply: the Vastu operations, which accept one, and the report-chat query.
         * Anything else (the free `/sandbox` routes, unknown paths) gets a key only
         * when the caller passes one, because a key sent to an operation that does
         * not take it is answered 422 `IDEMPOTENCY_NOT_SUPPORTED`.
         */
        internal fun acceptsAutomaticKey(path: String): Boolean {
            val bare = path.substringBefore("?")
            return bare.startsWith("/v2/vastu/") || bare.startsWith("/v2/astrology/vastu/") ||
                bare == "/api/v1/astrology/query"
        }

        /** Linear backoff base; attempt 0 waits this long, attempt 1 waits 2x, etc. */
        private const val RETRY_BACKOFF_BASE_MS = 250L
    }

    /** SDK configuration (API key, base URL, timeout). */
    val config: VedikaConfig = VedikaConfig(
        apiKey = apiKey,
        baseUrl = baseUrl.trimEnd('/'),
        timeoutMs = timeoutMs,
        allowInsecureHttp = allowInsecureHttp,
    )

    init {
        // Validate before building the transport that will hold credential headers.
        OriginPolicy.assertSafeBaseUrl(config.baseUrl, config.allowInsecureHttp)
    }

    /**
     * The underlying OkHttp client. Redirect-following is disabled on both
     * axes on purpose (credential-routing hardening): OkHttp's default
     * `followRedirects = true` would re-send the `Authorization` header to
     * whatever origin a 3xx response names, leaking the API key to a
     * different host. With both flags false, a 3xx comes back from
     * `Call.execute()` as an ordinary [Response] (`response.code` in
     * `300..399`) instead of being chased — [handleResponse] below turns
     * that into a thrown [VedikaApiError] rather than a second request. The
     * key is provably never forwarded because there is no second request.
     */
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .connectTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
        .readTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
        .writeTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
        .build()

    /**
     * Vastu Shastra: plot geometry, mandala projection, entrance/room/
     * placement rules, compliance audits, scoring, and floor-plan
     * generation.
     */
    val vastu: VastuService = VastuService(this)

    private fun headers(idempotencyKey: String? = null, path: String = ""): Headers {
        val builder = Headers.Builder()
            .add("Authorization", "Bearer ${config.apiKey}")
            .add("Content-Type", "application/json")
            .add("Accept", "application/json")
            .add("X-SDK", SDK_VERSION)
        if (idempotencyKey != null) {
            builder.add(idempotencyHeaderName(path), idempotencyKey)
        }
        return builder.build()
    }

    /** Sends GET with one key per billed Vastu call, shared by its retries. */
    internal suspend fun get(
        path: String,
        queryParams: Map<String, String> = emptyMap(),
        idempotencyKey: String? = null,
    ): JSONObject = withContext(Dispatchers.IO) {
        val urlBuilder = (config.baseUrl + path).toHttpUrl().newBuilder()
        queryParams.forEach { (k, v) -> urlBuilder.addQueryParameter(k, v) }
        val automatic = idempotencyKey == null && path.substringBefore("?") in BILLED_VASTU_GET_PATHS
        val key = idempotencyKey ?: if (automatic) UUID.randomUUID().toString() else null
        val request = Request.Builder()
            .url(urlBuilder.build())
            .headers(headers(key, path))
            .get()
            .build()
        execute(request, keyIsAutomatic = automatic)
    }

    /**
     * Sends a POST request to the Vedika API.
     *
     * [idempotencyKey]: every paid POST needs one so a client-side
     * retry after a LOST response (the request reached the server and was
     * billed, but the reply never made it back to the phone) cannot create a
     * duplicate charge — the server dedupes on this key and returns the
     * original result instead of billing again. If the caller does not
     * supply one, a fresh random UUID is generated per logical call except for
     * assessment batches, which require a retained caller key. This
     * client's own automatic retries (see [execute]) are always safe by
     * default; callers doing their OWN outer retry (e.g. after a process
     * restart) should pass the same key explicitly to get that same safety
     * across calls this client can't see.
     */
    internal suspend fun post(
        path: String,
        body: Map<String, Any?>,
        idempotencyKey: String? = null,
    ): JSONObject = withContext(Dispatchers.IO) {
        if (path.substringBefore("?") in RETAINED_KEY_PATHS) {
            require(!idempotencyKey.isNullOrBlank()) { "A nonblank caller-retained Idempotency-Key is required" }
        }
        // Scan save/retrieve/list/timelapse identify a retry by scanId or the
        // body's requestId and answer 422 to any retry header, so none is sent.
        val bodyIdentity = usesBodyIdentity(path)
        require(!bodyIdentity || idempotencyKey == null) {
            "Scan operations use scanId or requestId in the body; do not pass an Idempotency-Key"
        }
        val automatic = !bodyIdentity && idempotencyKey == null && acceptsAutomaticKey(path)
        val key = if (bodyIdentity) null else idempotencyKey ?: if (automatic) UUID.randomUUID().toString() else null
        val request = Request.Builder()
            .url((config.baseUrl + path).toHttpUrl())
            .headers(headers(key, path))
            .post(mapToJson(body).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()
        execute(request, keyIsAutomatic = automatic)
    }

    /**
     * True when resending [request] cannot be billed twice: a read, a request
     * carrying an idempotency key, or a scan operation whose identity is in the
     * body. A keyless billed POST is never retried after a network error or a
     * 5xx, because either can follow a completed charge.
     */
    private fun isReplaySafe(request: Request): Boolean =
        request.method == "GET" ||
            IDEMPOTENCY_HEADER_NAMES.any { request.header(it) != null } ||
            usesBodyIdentity(request.url.encodedPath)

    /**
     * Runs [request], retrying transient failures (see [MAX_RETRIES]).
     * Billed Vastu calls carry an `Idempotency-Key`, so a retry of the exact same
     * [Request] (same body, same key) cannot double-charge even if the first
     * attempt reached the server before the network dropped the response.
     *
     * [keyIsAutomatic]: the key was generated here, not supplied by the caller.
     * If the endpoint answers 422 `IDEMPOTENCY_NOT_SUPPORTED` (no charge was
     * attempted), the request is sent once more without the key and without any
     * automatic retry, since it is no longer replay-safe. A caller's own key is
     * never dropped: that case throws [VedikaIdempotencyNotSupportedError].
     */
    private fun execute(request: Request, attempt: Int = 0, keyIsAutomatic: Boolean = false): JSONObject {
        val replaySafe = isReplaySafe(request)
        val response: Response = try {
            httpClient.newCall(request).execute()
        } catch (e: IOException) {
            if (attempt < MAX_RETRIES && replaySafe) {
                Thread.sleep(RETRY_BACKOFF_BASE_MS * (attempt + 1))
                return execute(request, attempt + 1, keyIsAutomatic)
            }
            throw VedikaApiError("Network error: ${e.message}")
        }
        val status = response.code
        val retryAfterHeader = response.header("Retry-After")
        val bodyText = response.use { it.body?.string().orEmpty() }
        if (status == 422 && keyIsAutomatic && parseBody(bodyText).optString("code") == "IDEMPOTENCY_NOT_SUPPORTED") {
            val keyless = request.newBuilder().apply { IDEMPOTENCY_HEADER_NAMES.forEach { removeHeader(it) } }.build()
            return execute(keyless, MAX_RETRIES, keyIsAutomatic = false)
        }
        if (status >= 500 && attempt < MAX_RETRIES && replaySafe) {
            Thread.sleep(RETRY_BACKOFF_BASE_MS * (attempt + 1))
            return execute(request, attempt + 1, keyIsAutomatic)
        }
        return handleResponse(status, retryAfterHeader, bodyText)
    }

    private fun parseBody(bodyText: String): JSONObject = try {
        JSONObject(bodyText)
    } catch (e: Exception) {
        JSONObject().put("error", bodyText)
    }

    private fun handleResponse(status: Int, retryAfterHeader: String?, bodyText: String): JSONObject {
        val body: JSONObject = parseBody(bodyText)

        return when (val code = status) {
            in 200..299 -> body // job submit answers 202
            401 -> throw VedikaAuthError(
                body.optString("error", "Invalid API key"),
                body = body,
            )
            402 -> throw VedikaInsufficientCredits(
                body.optString("error", "Insufficient wallet balance"),
                body = body,
            )
            422 -> if (body.optString("code") == "IDEMPOTENCY_NOT_SUPPORTED") {
                throw VedikaIdempotencyNotSupportedError(
                    body.optString("message", "This endpoint does not accept an idempotency key; call again without one"),
                    body = body,
                )
            } else {
                throw VedikaApiError(
                    body.optString("error", "API error: $code"),
                    statusCode = code,
                    body = body,
                )
            }
            429 -> {
                // The body `code` names the limiter that refused the call; the
                // rate-limit headers describe the per-minute limiter only, so
                // nothing is decided from them. This client never retries a 429.
                val bodyRetry = if (body.has("retryAfter") && !body.isNull("retryAfter")) {
                    (body.opt("retryAfter") as? Number)?.toInt()
                } else null
                throw VedikaRateLimitError(
                    body.optString("message", "Rate limit exceeded"),
                    body = body,
                    retryAfterSeconds = bodyRetry ?: retryAfterHeader?.toIntOrNull(),
                )
            }
            else -> when {
                code in 300..399 -> {
                    // Credential-routing: redirects are not followed (see
                    // `httpClient` above), so the API key is never forwarded to the
                    // redirect destination. A 3xx from the API is unexpected and
                    // surfaced as an error rather than chased.
                    throw VedikaApiError(
                        "Unexpected redirect (HTTP $code) not followed; credentials were " +
                            "not forwarded. Check baseUrl.",
                        statusCode = code,
                        body = body,
                    )
                }
                code >= 500 -> throw VedikaServerError(
                    body.optString("error", "Server error: $code"),
                    statusCode = code,
                    body = body,
                )
                else -> throw VedikaApiError(
                    body.optString("error", "API error: $code"),
                    statusCode = code,
                    body = body,
                )
            }
        }
    }

    /** Recursively converts a `Map<String, Any?>` request body into a [JSONObject]. */
    private fun mapToJson(map: Map<String, Any?>): JSONObject {
        val obj = JSONObject()
        map.forEach { (k, v) -> obj.put(k, toJsonValue(v)) }
        return obj
    }

    private fun toJsonValue(value: Any?): Any = when (value) {
        null -> JSONObject.NULL
        is Map<*, *> -> {
            val obj = JSONObject()
            value.forEach { (k, v) -> obj.put(k.toString(), toJsonValue(v)) }
            obj
        }
        is List<*> -> {
            val arr = JSONArray()
            value.forEach { arr.put(toJsonValue(it)) }
            arr
        }
        else -> value
    }
}

/** Paths whose submit must carry a caller-retained key: a batch and an async job each charge per item. */
private val RETAINED_KEY_PATHS = setOf(
    "/v2/vastu/assessments/batch", "/v2/astrology/vastu/assessments/batch",
    "/v2/vastu/jobs", "/v2/astrology/vastu/jobs",
)

private val SCAN_PREFIXES = listOf("/v2/vastu/scans/", "/v2/astrology/vastu/scans/")
private val BODY_IDENTITY_SCAN_OPS = setOf("save", "retrieve", "list", "delete", "timelapse")

/** True for the scan operations whose retry identity lives in the JSON body. */
internal fun usesBodyIdentity(path: String): Boolean {
    val bare = path.substringBefore("?")
    return SCAN_PREFIXES.any { bare.startsWith(it) && bare.removePrefix(it) in BODY_IDENTITY_SCAN_OPS }
}
