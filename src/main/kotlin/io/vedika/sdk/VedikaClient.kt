package io.vedika.sdk

import io.vedika.sdk.exceptions.VedikaApiError
import io.vedika.sdk.exceptions.VedikaAuthError
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
        private const val SDK_VERSION = "vedika-android/1.0.0"
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

    private fun headers(idempotencyKey: String? = null): Headers {
        val builder = Headers.Builder()
            .add("Authorization", "Bearer ${config.apiKey}")
            .add("Content-Type", "application/json")
            .add("Accept", "application/json")
            .add("X-SDK", SDK_VERSION)
        if (idempotencyKey != null) {
            builder.add("Idempotency-Key", idempotencyKey)
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
        val key = idempotencyKey ?: if (path.substringBefore("?") in BILLED_VASTU_GET_PATHS) {
            UUID.randomUUID().toString()
        } else null
        val request = Request.Builder()
            .url(urlBuilder.build())
            .headers(headers(key))
            .get()
            .build()
        execute(request)
    }

    /**
     * Sends a POST request to the Vedika API.
     *
     * [idempotencyKey] (b2c#32): every paid POST needs one so a client-side
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
        if (path.substringBefore("?") in setOf("/v2/vastu/assessments/batch", "/v2/astrology/vastu/assessments/batch")) {
            require(!idempotencyKey.isNullOrBlank()) { "A nonblank caller-retained Idempotency-Key is required" }
        }
        val key = idempotencyKey ?: UUID.randomUUID().toString()
        val request = Request.Builder()
            .url((config.baseUrl + path).toHttpUrl())
            .headers(headers(key))
            .post(mapToJson(body).toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()
        execute(request)
    }

    /**
     * Runs [request], retrying transient failures (see [MAX_RETRIES]).
     * Every POST and mounted billed Vastu GET carries an
     * `Idempotency-Key` (see [post] and [get]), so a retry of the exact same [Request]
     * object (same body, same key) cannot double-charge even if the first
     * attempt's request actually reached the server before the network
     * dropped the response.
     */
    private fun execute(request: Request, attempt: Int = 0): JSONObject {
        val response: Response = try {
            httpClient.newCall(request).execute()
        } catch (e: IOException) {
            if (attempt < MAX_RETRIES) {
                Thread.sleep(RETRY_BACKOFF_BASE_MS * (attempt + 1))
                return execute(request, attempt + 1)
            }
            throw VedikaApiError("Network error: ${e.message}")
        }
        if (response.code >= 500 && attempt < MAX_RETRIES) {
            response.close()
            Thread.sleep(RETRY_BACKOFF_BASE_MS * (attempt + 1))
            return execute(request, attempt + 1)
        }
        return response.use { handleResponse(it) }
    }

    private fun handleResponse(response: Response): JSONObject {
        val bodyText = response.body?.string().orEmpty()
        val body: JSONObject = try {
            JSONObject(bodyText)
        } catch (e: Exception) {
            JSONObject().put("error", bodyText)
        }

        return when (val code = response.code) {
            200 -> body
            401 -> throw VedikaAuthError(
                body.optString("error", "Invalid API key"),
                body = body,
            )
            402 -> throw VedikaInsufficientCredits(
                body.optString("error", "Insufficient wallet balance"),
                body = body,
            )
            429 -> throw VedikaRateLimitError(
                body.optString("message", "Rate limit exceeded"),
                body = body,
                retryAfterSeconds = response.header("Retry-After")?.toIntOrNull(),
            )
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
