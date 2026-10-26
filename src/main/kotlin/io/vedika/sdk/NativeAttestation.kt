package io.vedika.sdk

import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.security.MessageDigest
import java.util.Base64

/** UTF-16 member order, minimal escapes and ECMAScript number formatting. */
object AttestationCanonicalJson {
    fun encode(value: Any?): String = when (value) {
        null, JSONObject.NULL -> "null"
        is Boolean -> if (value) "true" else "false"
        is String -> quote(value)
        is Number -> number(value)
        is JSONObject -> value.keys().asSequence().toList().sorted().joinToString(",", "{", "}") { quote(it) + ":" + encode(value.get(it)) }
        is JSONArray -> (0 until value.length()).joinToString(",", "[", "]") { encode(value.get(it)) }
        is Map<*, *> -> {
            require(value.keys.all { it is String }) { "JSON member keys must be strings" }
            value.keys.map { it as String }.sorted().joinToString(",", "{", "}") { quote(it) + ":" + encode(value[it]) }
        }
        is List<*> -> value.joinToString(",", "[", "]") { encode(it) }
        else -> throw IllegalArgumentException("Unsupported JSON value")
    }
    private fun quote(value: String): String = buildString {
        append('"')
        var index = 0
        while (index < value.length) {
            val c = value[index]
            if (c.isHighSurrogate()) {
                require(index + 1 < value.length && value[index + 1].isLowSurrogate()) { "Invalid Unicode surrogate" }
                append(c); append(value[++index])
            } else {
                require(!c.isLowSurrogate()) { "Invalid Unicode surrogate" }
                when (c) {
                    '"' -> append("\\\""); '\\' -> append("\\\\")
                    '\b' -> append("\\b"); '\t' -> append("\\t"); '\n' -> append("\\n")
                    '\u000c' -> append("\\f"); '\r' -> append("\\r")
                    else -> if (c.code < 32) append("\\u%04x".format(c.code)) else append(c)
                }
            }
            index++
        }
        append('"')
    }
    private fun number(value: Number): String {
        val x = value.toDouble(); require(x.isFinite()) { "Nonfinite JSON number" }
        if (value !is Float && value !is Double && value !is BigDecimal) {
            require(BigDecimal(value.toString()).abs() <= BigDecimal("9007199254740991")) { "Unsafe JSON integer" }
        }
        if (x == 0.0) return "0"
        // Java 17 Double.toString(1e23) and subnormals differ from ECMAScript.
        // Find the shortest rounded decimal that round-trips to the same bits.
        val exact = BigDecimal(x)
        val decimal = (1..17).asSequence().map {
            exact.round(java.math.MathContext(it, java.math.RoundingMode.HALF_EVEN)).stripTrailingZeros()
        }.first { it.toDouble().toBits() == x.toBits() }
        if (kotlin.math.abs(x) >= 1e-6 && kotlin.math.abs(x) < 1e21) return decimal.toPlainString()
        val parts = decimal.toString().lowercase().split('e')
        if (parts.size == 1) return parts[0]
        val exponent = parts[1].toInt()
        return parts[0] + "e" + (if (exponent >= 0) "+" else "-") + kotlin.math.abs(exponent)
    }
}

data class NativeAttestationChallenge(val value: String, val expiresAtEpoch: Long, val platform: String) {
    init { require(platform in setOf("ios", "android") && value.matches(Regex("[A-Za-z0-9_-]+"))) { "Invalid challenge" } }
    fun check(nowEpoch: Long = System.currentTimeMillis() / 1000) { require(nowEpoch < expiresAtEpoch) { "Attestation challenge expired" } }
}

object NativeAttestation {
    fun digest(operation: String, challenge: NativeAttestationChallenge, body: JSONObject,
               nowEpoch: Long = System.currentTimeMillis() / 1000): ByteArray {
        challenge.check(nowEpoch)
        require(operation in setOf("ar/room-capture", "ar/scan-quality", "scans/save")) { "Unsupported operation" }
        val unsigned = JSONObject(body.toString()); unsigned.remove("deviceAttestation")
        val text = "vedika-attest-v1\n$operation\n${challenge.value}\n${AttestationCanonicalJson.encode(unsigned)}"
        return MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
    }
    fun playRequestHash(digest: ByteArray): String {
        require(digest.size == 32) { "Expected SHA-256 digest" }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }
    suspend fun challenge(service: VastuService): NativeAttestationChallenge {
        val response = service.vastuOperation(VastuContracts.arAttestationChallenge, VastuArAttestationChallengeRequest("android"))
        val data = response.data
        require(data.singleUse && data.challenge != null && data.expiresAtEpoch != null) { "Attestation is not configured for this buyer app" }
        return NativeAttestationChallenge(data.challenge!!, data.expiresAtEpoch!!.toLong(), "android").also { it.check() }
    }
    /** The buyer supplies their warmed Play Integrity token provider. Invoke
     * StandardIntegrityTokenRequest.builder().setRequestHash(hash) with the
     * supplied hash. No Google credentials, app identity or new dependency is
     * embedded. The provider is suspendable and platform failures propagate. */
    suspend fun playProof(operation: String, challenge: NativeAttestationChallenge, body: JSONObject,
                          requestToken: suspend (requestHash: String) -> String): VastuDeviceAttestation {
        require(challenge.platform == "android")
        val hash = playRequestHash(digest(operation, challenge, body))
        val token = requestToken(hash); require(token.isNotBlank()) { "Empty Play Integrity proof" }
        challenge.check()
        return VastuDeviceAttestation(platform = "android", challenge = challenge.value, integrityToken = token)
    }
}
