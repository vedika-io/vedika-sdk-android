package io.vedika.sdk.exceptions

import org.json.JSONObject

/**
 * Base exception for all Vedika API errors. Mirrors
 * `sdks/flutter/lib/src/exceptions.dart`'s `VedikaApiError`.
 *
 * Only the 5 classes the Flutter SDK proved sufficient are ported here
 * (docs/ops/2026-08-11-vastu-native-adapters-plan.md section 3.4: "do not
 * build error-type parity beyond the 5 classes sdks/flutter already
 * defines"). Dart's 6th class, `VedikaSubscriptionError` (HTTP 403), is
 * intentionally NOT ported — a 403 falls through to the generic
 * [VedikaApiError] via [io.vedika.sdk.VedikaClient]'s response-status
 * `when` (its `else` branch), same as any other unmapped 4xx status.
 */
open class VedikaApiError(
    message: String,
    val statusCode: Int? = null,
    val body: JSONObject? = null,
) : Exception(message) {
    override fun toString(): String = "VedikaApiError($statusCode): $message"
}

/** Thrown when the API key is invalid or missing (HTTP 401). */
class VedikaAuthError(
    message: String,
    statusCode: Int = 401,
    body: JSONObject? = null,
) : VedikaApiError(message, statusCode, body)

/** Thrown when the wallet balance is insufficient (HTTP 402). */
class VedikaInsufficientCredits(
    message: String,
    statusCode: Int = 402,
    body: JSONObject? = null,
) : VedikaApiError(message, statusCode, body)

/** Thrown when the rate limit is exceeded (HTTP 429). */
class VedikaRateLimitError(
    message: String,
    statusCode: Int = 429,
    body: JSONObject? = null,
    /** Seconds until the rate limit resets, parsed from the `Retry-After` header. */
    val retryAfterSeconds: Int? = null,
) : VedikaApiError(message, statusCode, body)

/** Thrown on server errors (HTTP 5xx). */
class VedikaServerError(
    message: String,
    statusCode: Int? = null,
    body: JSONObject? = null,
) : VedikaApiError(message, statusCode, body)
