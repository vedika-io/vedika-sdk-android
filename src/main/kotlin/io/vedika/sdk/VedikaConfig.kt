package io.vedika.sdk

/**
 * Configuration for the Vedika Intelligence API client. Mirrors
 * `sdks/flutter/lib/src/config.dart`.
 */
data class VedikaConfig(
    /** Your Vedika API key (format: `vk_live_*` or `vk_ent_*`). */
    val apiKey: String,
    /** Base URL for the API. Defaults to `https://api.vedika.io`. */
    val baseUrl: String = DEFAULT_BASE_URL,
    /** Request timeout, in milliseconds. Defaults to 30,000 (30s). */
    val timeoutMs: Long = DEFAULT_TIMEOUT_MS,
    /** Legacy compatibility option; cannot enable remote HTTP or custom origins. */
    val allowInsecureHttp: Boolean = false,
) {
    companion object {
        const val DEFAULT_BASE_URL = "https://api.vedika.io"
        const val DEFAULT_TIMEOUT_MS = 30_000L
    }
}
