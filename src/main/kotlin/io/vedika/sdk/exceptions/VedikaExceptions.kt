package io.vedika.sdk.exceptions

import org.json.JSONObject

/**
 * Base exception for all Vedika API errors. Mirrors
 * `sdks/flutter/lib/src/exceptions.dart`'s `VedikaApiError`.
 *
 * Only the 5 classes the Flutter SDK proved sufficient are ported here
 * (design note: "do not
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
    /**
     * Machine-readable error `code` from the response body, for example
     * `INSUFFICIENT_BALANCE` or `DAILY_LIMIT_EXCEEDED`, when the API sent one.
     */
    val code: String?
        get() = body?.optString("code", "")?.takeIf { it.isNotEmpty() }

    override fun toString(): String = "VedikaApiError($statusCode): $message"
}

/** Thrown when the API key is invalid or missing (HTTP 401). */
class VedikaAuthError(
    message: String,
    statusCode: Int = 401,
    body: JSONObject? = null,
) : VedikaApiError(message, statusCode, body)

/**
 * Thrown when the wallet balance is insufficient (HTTP 402).
 *
 * A 402 is never retried: the call was refused before any charge. Add funds
 * (see [purchaseUrl]) and call again.
 */
class VedikaInsufficientCredits(
    message: String,
    statusCode: Int = 402,
    body: JSONObject? = null,
) : VedikaApiError(message, statusCode, body) {
    private fun walletValue(key: String): Double? {
        val wallet = body?.optJSONObject("wallet")
        val raw = if (wallet != null && wallet.has(key) && !wallet.isNull(key)) wallet.opt(key)
        else if (body != null && body.has(key) && !body.isNull(key)) body.opt(key)
        else null
        return when (raw) {
            is Number -> raw.toDouble()
            is String -> raw.toDoubleOrNull()
            else -> null
        }
    }

    /** Amount this call needed, in USD (`wallet.required`). */
    val required: Double? get() = walletValue("required")

    /** Wallet balance at the time of the call, in USD (`wallet.available`). */
    val available: Double? get() = walletValue("available")

    /** How much more is needed, in USD (`wallet.deficit`). */
    val deficit: Double? get() = walletValue("deficit")

    /** Where to add funds, when the API sent one. */
    val purchaseUrl: String? get() = body?.optString("purchaseUrl", "")?.takeIf { it.isNotEmpty() }
}

/**
 * Thrown when a request is refused with HTTP 429.
 *
 * A 429 has several causes, so decide from [code], never from the rate-limit
 * response headers: they describe the per-minute limiter, which may not be the
 * one that refused the call.
 */
class VedikaRateLimitError(
    message: String,
    statusCode: Int = 429,
    body: JSONObject? = null,
    /**
     * Seconds to wait, from the body's `retryAfter`, falling back to the
     * `Retry-After` header. Only meaningful when [isRetryable] is true.
     */
    val retryAfterSeconds: Int? = null,
) : VedikaApiError(message, statusCode, body) {
    /**
     * True for `DAILY_LIMIT_EXCEEDED`: the daily allowance is spent and a retry
     * will keep failing until it resets or the plan is upgraded.
     */
    val isDailyLimit: Boolean get() = code == "DAILY_LIMIT_EXCEEDED"

    /**
     * True for `MONTHLY_SPEND_LIMIT_REACHED`: the account's monthly spend limit
     * is reached. It clears only in the new month, or when the owner raises or
     * removes the limit under Billing, so a retry will keep failing.
     */
    val isMonthlySpendLimit: Boolean get() = code == "MONTHLY_SPEND_LIMIT_REACHED"

    /** True when waiting [retryAfterSeconds] and calling again can succeed. False for the daily, plan and monthly spend limits. */
    val isRetryable: Boolean get() =
        code != "DAILY_LIMIT_EXCEEDED" && code != "PLAN_LIMIT_EXCEEDED" && code != "MONTHLY_SPEND_LIMIT_REACHED"

    /** Where to upgrade the plan, when the API sent one. */
    val upgradeUrl: String? get() = body?.optString("upgradeUrl", "")?.takeIf { it.isNotEmpty() }
}

/**
 * Thrown when a call carried an idempotency key that the endpoint does not
 * accept (HTTP 422, `IDEMPOTENCY_NOT_SUPPORTED`) and the key was the caller's
 * own. No charge was attempted. Call again without `idempotencyKey`.
 */
class VedikaIdempotencyNotSupportedError(
    message: String,
    statusCode: Int = 422,
    body: JSONObject? = null,
) : VedikaApiError(message, statusCode, body)

/** Thrown on server errors (HTTP 5xx). */
class VedikaServerError(
    message: String,
    statusCode: Int? = null,
    body: JSONObject? = null,
) : VedikaApiError(message, statusCode, body)
