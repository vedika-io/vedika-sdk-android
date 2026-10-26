package io.vedika.sdk

import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class NativeAttestationTest {
    @Test fun everySharedVector() {
        // The monorepo keeps the shared vectors in sdks/fixtures; the standalone mirror ships them as a test resource.
        val shared = File("../fixtures/vastu-attestation-binding/vectors.json")
        val text = if (shared.isFile) shared.readText()
        else NativeAttestationTest::class.java.getResource("/vastu-attestation-binding/vectors.json")!!.readText()
        val cases = JSONObject(text).getJSONArray("cases")
        assertEquals(5, cases.length())
        for (i in 0 until cases.length()) {
            val row = cases.getJSONObject(i); val body = row.getJSONObject("body")
            assertEquals(row.getString("name"), row.getString("canonical"), AttestationCanonicalJson.encode(body))
            val challenge = NativeAttestationChallenge(row.getString("challenge"), Long.MAX_VALUE, "android")
            val digest = NativeAttestation.digest(row.getString("operation"), challenge, body)
            assertEquals(row.getString("digestHex"), digest.joinToString("") { "%02x".format(it.toInt() and 255) })
            assertEquals(row.getString("playRequestHash"), NativeAttestation.playRequestHash(digest))
        }
    }
    @Test fun bindingExpiryAndProofAdapter() = runBlocking {
        val challenge = NativeAttestationChallenge("challenge", Long.MAX_VALUE, "android")
        val body = JSONObject("{\"capture\":{\"room\":\"kitchen\"}}")
        val digest = NativeAttestation.digest("ar/room-capture", challenge, body)
        val proven = JSONObject(body.toString()).put("deviceAttestation", JSONObject().put("integrityToken", "previous"))
        assertArrayEquals(digest, NativeAttestation.digest("ar/room-capture", challenge, proven))
        assertFalse(digest.contentEquals(NativeAttestation.digest("ar/scan-quality", challenge, body)))
        val proof = NativeAttestation.playProof("ar/room-capture", challenge, body) { hash ->
            assertEquals(NativeAttestation.playRequestHash(digest), hash); "platform-token"
        }
        assertEquals("platform-token", proof.integrityToken)
        try { NativeAttestation.digest("ar/room-capture", NativeAttestationChallenge("challenge", 10, "android"), body, 10); fail("Expired challenge accepted") }
        catch (_: IllegalArgumentException) {}
        try { NativeAttestation.playProof("ar/room-capture", challenge, body) { "" }; fail("Empty proof accepted") }
        catch (_: IllegalArgumentException) {}
    }
    @Test fun unicodeAndNumberEdges() {
        assertEquals("[1,0,0.000001,1e-7,100000000000000000000,1e+21]", AttestationCanonicalJson.encode(listOf(1.0, -0.0, 1e-6, 1e-7, 1e20, 1e21)))
        assertEquals("{\"\ud83d\ude00\":1,\"\ue000\":2}", AttestationCanonicalJson.encode(mapOf("\ue000" to 2, "\ud83d\ude00" to 1)))
        assertEquals("[1e+23,5e-324]", AttestationCanonicalJson.encode(listOf(1e23, Double.MIN_VALUE)))
        for (value in listOf(Double.NaN, Double.POSITIVE_INFINITY, 9007199254740992L, "\ud800")) {
            try { AttestationCanonicalJson.encode(value); fail("Invalid value accepted") } catch (_: IllegalArgumentException) {}
        }
    }
}
