package io.vedika.sdk.internal

import java.net.URI
import java.net.URISyntaxException

/** Validate credential origins before constructing the transport, without DNS. */
internal object OriginPolicy {

    // First octet is fixed to the literal "127" on purpose (not `\d{1,3}`):
    // 127.0.0.0/8 is loopback, so only that leading label counts.
    private val IPV4_LOOPBACK = Regex("^127\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")

    /**
     * True only for genuine loopback: the literal `localhost`, the IPv6
     * loopback `::1` (optionally bracketed, e.g. from a `[::1]:8080` host
     * component), or a numeric IPv4 literal in `127.0.0.0/8` — decided by
     * **string parsing only**, never DNS resolution.
     *
     * `InetAddress.getByName(host).isLoopbackAddress` is deliberately NOT
     * used here: for a non-numeric hostname it performs a real DNS lookup
     * (a network call at client-construction time, and one whose result
     * varies by network/resolver), and it would wrongly treat an
     * attacker-controlled name that merely *resolves* to 127.0.0.1 as
     * loopback — the opposite-direction version of the exact bug this
     * function exists to prevent. The adversarial case that must be
     * rejected without any network lookup: `127.attacker.invalid` and
     * `127.example.com` both start with the string `"127."`, but that's a
     * DNS label, not an IPv4 octet — neither is loopback.
     */
    fun isLoopbackHost(host: String): Boolean {
        val h = host.trim().removePrefix("[").removeSuffix("]").lowercase()
        if (h == "localhost" || h == "::1") return true
        val match = IPV4_LOOPBACK.matchEntire(h) ?: return false
        return match.groupValues.drop(1).all { octet -> isValidOctet(octet) }
    }

    private fun isValidOctet(octet: String): Boolean {
        if (octet.length > 1 && octet[0] == '0') return false // no leading zeros (e.g. "0.0.0.001")
        val n = octet.toIntOrNull() ?: return false
        return n in 0..255
    }

    /** The legacy opt-in cannot bypass the official-origin or loopback policy. */
    @Suppress("UNUSED_PARAMETER")
    fun assertSafeBaseUrl(baseUrl: String, allowInsecureHttp: Boolean) {
        val uri = try {
            URI(baseUrl)
        } catch (_: URISyntaxException) {
            throw IllegalArgumentException("baseUrl is not a valid URL")
        }
        val scheme = uri.scheme?.lowercase()
        val host = uri.host
        require(scheme != null && !host.isNullOrEmpty() && uri.rawUserInfo == null &&
            (uri.rawPath.isNullOrEmpty() || uri.rawPath == "/") &&
            uri.rawQuery == null && uri.rawFragment == null &&
            (uri.port == -1 || uri.port in 1..65535)) {
            "baseUrl must be a valid bare origin without credentials, path, query, or fragment"
        }
        if (scheme == "https" && host.lowercase() == "api.vedika.io" &&
            (uri.port == -1 || uri.port == 443)) return
        if (scheme == "http" && isLoopbackHost(host)) return
        throw IllegalArgumentException(
            "baseUrl must use https://api.vedika.io or loopback http://; custom origins are not allowed"
        )
    }
}
