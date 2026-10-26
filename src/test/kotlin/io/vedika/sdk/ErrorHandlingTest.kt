package io.vedika.sdk

import io.vedika.sdk.exceptions.VedikaApiError
import io.vedika.sdk.exceptions.VedikaAuthError
import io.vedika.sdk.exceptions.VedikaIdempotencyNotSupportedError
import io.vedika.sdk.exceptions.VedikaInsufficientCredits
import io.vedika.sdk.exceptions.VedikaRateLimitError
import io.vedika.sdk.exceptions.VedikaServerError
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.InetAddress

/**
 * Error classification and idempotency-key policy, against a real loopback
 * socket. Mirrors `sdks/swift/Tests/VedikaSDKTests/ErrorHandlingTests.swift`.
 */
class ErrorHandlingTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start(InetAddress.getByName("127.0.0.1"), 0)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun client() = VedikaClient(apiKey = "vk_test_x", baseUrl = server.url("/").toString().trimEnd('/'))

    private fun json(status: Int, body: String, vararg headers: Pair<String, String>) =
        MockResponse().setResponseCode(status).setHeader("Content-Type", "application/json").setBody(body).also { r ->
            headers.forEach { (k, v) -> r.setHeader(k, v) }
        }

    private fun score(c: VedikaClient = client(), key: String? = null) =
        runBlocking { c.vastu.vastuScore("overall", emptyMap(), idempotencyKey = key) }

    @Test
    fun `402 is typed with required available and deficit and is never retried`() {
        server.enqueue(json(402, """{"success":false,"error":"Insufficient balance","code":"INSUFFICIENT_BALANCE","wallet":{"required":0.5,"available":0.12,"deficit":0.38},"purchaseUrl":"https://vedika.io/dashboard/billing"}"""))
        server.enqueue(json(200, """{"success":true}"""))
        val e = assertThrows(VedikaInsufficientCredits::class.java) { score() }
        assertEquals("INSUFFICIENT_BALANCE", e.code)
        assertEquals(0.5, e.required!!, 0.0)
        assertEquals(0.12, e.available!!, 0.0)
        assertEquals(0.38, e.deficit!!, 0.0)
        assertEquals("https://vedika.io/dashboard/billing", e.purchaseUrl)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `402 without a wallet block leaves the amounts null`() {
        server.enqueue(json(402, """{"error":"no funds"}"""))
        val e = assertThrows(VedikaInsufficientCredits::class.java) { score() }
        assertNull(e.required)
        assertNull(e.deficit)
    }

    @Test
    fun `daily limit is not retryable and rate-limit headers are ignored`() {
        server.enqueue(json(
            429,
            """{"success":false,"code":"DAILY_LIMIT_EXCEEDED","message":"Daily limit reached","retryAfter":3600,"upgradeUrl":"https://vedika.io/pricing"}""",
            "x-ratelimit-remaining" to "59", "Retry-After" to "1",
        ))
        server.enqueue(json(200, """{"success":true}"""))
        val e = assertThrows(VedikaRateLimitError::class.java) { score() }
        assertTrue(e.isDailyLimit)
        assertFalse(e.isRetryable)
        assertEquals("https://vedika.io/pricing", e.upgradeUrl)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `monthly spend limit is not retryable and is not a daily limit`() {
        server.enqueue(json(
            429,
            """{"success":false,"code":"MONTHLY_SPEND_LIMIT_REACHED","message":"Monthly spend limit reached","retryAfter":1}""",
            "x-ratelimit-remaining" to "59", "Retry-After" to "1",
        ))
        server.enqueue(json(200, """{"success":true}"""))
        val e = assertThrows(VedikaRateLimitError::class.java) { score() }
        assertEquals("MONTHLY_SPEND_LIMIT_REACHED", e.code)
        assertTrue(e.isMonthlySpendLimit)
        assertFalse(e.isDailyLimit)
        assertFalse(e.isRetryable)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `per-minute limit carries retryAfter from the body before the header`() {
        server.enqueue(json(429, """{"code":"RATE_LIMIT_EXCEEDED","retryAfter":7}""", "Retry-After" to "99"))
        val first = assertThrows(VedikaRateLimitError::class.java) { score() }
        assertTrue(first.isRetryable)
        assertEquals(7, first.retryAfterSeconds)
        server.enqueue(json(429, """{"code":"RATE_LIMITED"}""", "Retry-After" to "12"))
        val second = assertThrows(VedikaRateLimitError::class.java) { score() }
        assertEquals(12, second.retryAfterSeconds)
    }

    @Test
    fun `401 is not retried`() {
        server.enqueue(json(401, """{"error":"Invalid API key"}""", "WWW-Authenticate" to "Bearer error=\"invalid_token\""))
        server.enqueue(json(200, """{"success":true}"""))
        assertThrows(VedikaAuthError::class.java) { score() }
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `every 2xx is success`() {
        for (status in listOf(201, 202)) {
            server.enqueue(json(status, """{"success":true,"data":{"jobId":"x"}}"""))
            val body = runBlocking { client().vastu.vastu("jobs/vjob_0123456789abcdef0123456789abcdef") }
            assertTrue("$status", body.getBoolean("success"))
        }
    }

    @Test
    fun `an automatic key refused as 422 is resent once without it`() {
        server.enqueue(json(422, """{"success":false,"code":"IDEMPOTENCY_NOT_SUPPORTED","message":"Remove the idempotency header and retry; no charge was attempted."}"""))
        server.enqueue(json(200, """{"success":true,"data":{"ok":true}}"""))
        assertTrue(score().getBoolean("success"))
        val first = server.takeRequest()
        val second = server.takeRequest()
        assertNotNull(first.getHeader("Idempotency-Key"))
        assertNull(second.getHeader("Idempotency-Key"))
        assertNull(second.getHeader("X-Idempotency-Key"))
    }

    @Test
    fun `a keyless resend is not retried after a server error`() {
        server.enqueue(json(422, """{"code":"IDEMPOTENCY_NOT_SUPPORTED"}"""))
        server.enqueue(json(503, """{"error":"down"}"""))
        server.enqueue(json(200, """{"success":true}"""))
        assertThrows(VedikaServerError::class.java) { score() }
        assertEquals("a keyless billed call must not be retried", 2, server.requestCount)
    }

    @Test
    fun `a callers own key is never dropped`() {
        server.enqueue(json(422, """{"code":"IDEMPOTENCY_NOT_SUPPORTED","message":"no key here"}"""))
        server.enqueue(json(200, """{"success":true}"""))
        assertThrows(VedikaIdempotencyNotSupportedError::class.java) { score(key = "mine-1") }
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `other idempotency errors stay plain 422s`() {
        server.enqueue(json(422, """{"code":"IDEMPOTENCY_KEY_REUSED","error":"reused"}"""))
        val e = assertThrows(VedikaApiError::class.java) { score() }
        assertFalse(e is VedikaIdempotencyNotSupportedError)
        assertEquals("IDEMPOTENCY_KEY_REUSED", e.code)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `sandbox calls get no automatic key and are not retried on a 5xx`() {
        server.enqueue(json(503, """{"error":"down"}"""))
        server.enqueue(json(200, """{"success":true}"""))
        assertThrows(VedikaServerError::class.java) {
            runBlocking { client().post("/sandbox/v2/vastu/workspace/get", emptyMap()) }
        }
        assertEquals(1, server.requestCount)
        val wire = server.takeRequest()
        assertNull(wire.getHeader("Idempotency-Key"))
        assertNull(wire.getHeader("X-Idempotency-Key"))
        assertNull(wire.getHeader("X-Request-Id"))
    }

    @Test
    fun `report-chat query uses the spec header spelling`() {
        server.enqueue(json(200, """{"success":true}"""))
        runBlocking { client().post("/api/v1/astrology/query", mapOf("question" to "q")) }
        val wire = server.takeRequest()
        assertNotNull(wire.getHeader("X-Idempotency-Key"))
        assertNull(wire.getHeader("Idempotency-Key"))
    }

    @Test
    fun `vastu calls keep the spec spelling and never send X-Request-Id`() {
        server.enqueue(json(200, """{"success":true}"""))
        score()
        val wire = server.takeRequest()
        assertNotNull(wire.getHeader("Idempotency-Key"))
        assertNull(wire.getHeader("X-Idempotency-Key"))
        assertNull(wire.getHeader("X-Request-Id"))
        assertEquals("Bearer vk_test_x", wire.getHeader("Authorization"))
    }

    @Test
    fun `SDK version header matches the Gradle version`() {
        server.enqueue(json(200, """{"success":true}"""))
        score()
        val gradle = java.io.File("build.gradle.kts").takeIf { it.isFile }?.readText()
        val version = gradle?.let { Regex("""(?m)^version\s*=\s*"([^"]+)"""").find(it)?.groupValues?.get(1) }
        val sent = server.takeRequest().getHeader("X-SDK")
        if (version != null) assertEquals("vedika-android/$version", sent) else assertEquals("vedika-android/2.0.0", sent)
    }
}
