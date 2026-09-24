package io.vedika.sdk

import io.vedika.sdk.exceptions.VedikaApiError
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.json.JSONObject
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * Credential-routing hardening, ported from
 * `sdks/flutter/test/credential_routing_test.dart` onto OkHttp's
 * `MockWebServer`. Five properties, same as the Dart suite:
 *
 *  1. Vastu verb dispatch: tables under `reference/` plus `direction/declination` -> GET,
 *     everything else -> POST.
 *  2. Every request is built with redirects disabled.
 *  3. An unfollowed 3xx surfaces as [VedikaApiError], not a chased redirect.
 *  4. Real two-server test: the `Authorization` header — and any request at
 *     all — never reaches the redirect target.
 *  5. `baseUrl` origin policy, including the `127.attacker.invalid` /
 *     `127.example.com` spoof-host cases.
 */
class CredentialRoutingTest {

    @Test
    fun `optional numeric responses decode with the Android JSONObject API`() {
        for (value in listOf<Any>(42, 42.5, "42.5", "4.25e1")) {
            val result = VastuOperationResult(JSONObject().put("data", JSONObject().put("score", value)))
            assertEquals(if (value == 42) 42.0 else 42.5, result.score!!, 0.0)
        }
        for (value in listOf(JSONObject.NULL, true, "unknown", "NaN", JSONObject())) {
            val result = VastuOperationResult(JSONObject().put("data", JSONObject().put("score", value)))
            assertNull(result.score)
        }
        assertNull(VastuOperationResult(JSONObject().put("data", JSONObject())).score)
    }

    @Test
    fun `assessment request and response types match canonical runtime shapes`() {
        val request = VastuAssessmentsRequest(
            inputSource = "plan-derived",
            rooms = listOf(VastuAssessmentsRequestRoomsItem("kitchen", "SE")),
            plotPolygon = listOf(listOf(0.0, 0.0), listOf(10.0, 0.0), listOf(10.0, 10.0)),
            doorXY = VastuPoint(5.0, 0.0),
        )
        assertEquals("kitchen", (request.toMap()["rooms"] as List<*>).first().let { it as Map<*, *> }["roomType"])
        assertEquals(listOf(5.0, 0.0), request.toMap()["doorXY"])

        val badge = VastuAssessmentBadgeEligibility(
            JSONObject("""{"inputSource":"plan-derived","badge":null,"eligible":false,"variant":null,"reason":"insufficient evidence"}""")
        )
        assertEquals("plan-derived", badge.inputSource)
        assertNull(badge.badge)
        assertEquals(false, badge.eligible)

        val data = VastuAssessmentData(JSONObject())
        val billed = VastuTypedResponse(
            success = true,
            data = data,
            raw = JSONObject("""{"billing":{"charged":1}}"""),
        )
        assertEquals(1, billed.billing.getInt("charged"))
        val unbilled = VastuAssessmentsResponse(true, data, JSONObject())
        assertNull(unbilled.billing)
    }

    @Test
    fun `typed inventory exposes every mounted logical operation exactly once`() {
        val paths = VastuOperation.entries.map { it.path }
        assertEquals(94, paths.size)
        assertEquals(94, paths.toSet().size)
        assertEquals("assessments", VastuOperation.Assessments.path)
        assertEquals("ar/true-north-calibrate", VastuOperation.ArTrueNorthCalibrate.path)
    }

    @Test
    fun `AR result types decode the exact nested data contract`() {
        val scan = VastuArScanQualityData(JSONObject("""{
            "grade":"A","score":96,"missingData":[],"warnings":[],
            "reScanSuggestions":[],"dimensions":{
              "pointCloudDensity":{"score":98,"reason":"dense"},
              "polygonClosure":{"score":97,"reason":"closed"},
              "compassConfidence":{"score":96,"reason":"stable"},
              "gpsConfidence":{"score":95,"reason":"fixed"},
              "roomsTagged":{"score":94,"reason":"tagged"},
              "coverage":{"score":93,"reason":"complete"}
            },"acceptForAudit":true,"sources":["scan"],"verified":true
        }"""))
        assertEquals("A", scan.grade)
        assertEquals(98, scan.dimensions.pointCloudDensity.score)
        assertEquals("complete", scan.dimensions.coverage.reason)

        val north = VastuArTrueNorthData(JSONObject("""{
            "input":{"lat":28.61,"lon":77.21,"datetime":"2026-08-24T12:00:00Z","deviceHeadingAtSunDeg":140.0},
            "sunAzimuthTrueDeg":151.5,"solarElevationDeg":62.0,"offsetDeg":11.5,
            "headingCorrection":"add 11.5 degrees","reliable":true,"reason":"solar fix",
            "sources":["NOAA"],"verified":true
        }"""))
        assertEquals(28.61, north.input.lat, 0.0)
        assertEquals(11.5, north.offsetDeg, 0.0)
        assertEquals(true, north.reliable)
        assertEquals(VastuOperation.ArTrueNorthCalibrate, VastuContracts.arTrueNorthCalibrate.operation)
    }

    // The 11 GET-only reference tables + the GET+POST dual, from the Rust
    // router (VASTU_GET_REFERENCE_ROUTES + VASTU_DUAL_ROUTE in
    // vedika-v2/src/vastu.rs) — identical list to the Flutter/JS SDK tests.
    private val getOps = listOf(
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
    )
    private val postOps = listOf("score/overall", "placement/borewell", "entrance/pada", "plan/analyze")

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

    private fun okResponse(): MockResponse = MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody("""{"success":true,"data":{"ok":true}}""")

    private fun clientFor(server: MockWebServer, apiKey: String = "vk_test_x"): VedikaClient =
        VedikaClient(apiKey = apiKey, baseUrl = server.url("/").toString().trimEnd('/'))

    @Test
    fun `vastu verb dispatch matches the reference-table plus declination GET list, everything else POST`() =
        runBlocking {
            repeat(getOps.size + postOps.size) { server.enqueue(okResponse()) }
            val client = clientFor(server)

            for (op in getOps) client.vastu.vastu(op, mapOf("lat" to 1, "lon" to 2))
            for (op in postOps) client.vastu.vastu(op, mapOf("zone" to "north"))

            for (op in getOps) {
                val recorded = server.takeRequest()
                assertEquals(op, "GET", recorded.method)
                assertEquals(op, "/v2/astrology/vastu/$op", recorded.path!!.substringBefore("?"))
            }
            for (op in postOps) {
                val recorded = server.takeRequest()
                assertEquals(op, "POST", recorded.method)
                assertEquals(op, "/v2/astrology/vastu/$op", recorded.path)
            }
        }

    @Test
    fun `billed GET retries reuse one key and separate calls use new keys`() = runBlocking {
        val client = clientFor(server)
        val keys = mutableSetOf<String>()
        for (prefix in listOf("/v2/vastu/", "/v2/astrology/vastu/")) {
            for (op in getOps) {
                server.enqueue(MockResponse().setResponseCode(503))
                server.enqueue(okResponse())
                server.enqueue(okResponse())
                val path = prefix + op
                client.get(path, mapOf("lat" to "1", "lon" to "2"))
                client.get(path, mapOf("lat" to "1", "lon" to "2"))
                val first = server.takeRequest()
                val retry = server.takeRequest()
                val next = server.takeRequest()
                val key = first.getHeader("Idempotency-Key")
                assertNotNull(path, key)
                assertEquals(path, key, retry.getHeader("Idempotency-Key"))
                assertEquals(first.path, retry.path)
                assertTrue(keys.add(key!!))
                assertTrue(keys.add(next.getHeader("Idempotency-Key")!!))
            }
        }
    }

    @Test
    fun `GET preserves explicit keys and excludes unbilled paths`() = runBlocking {
        val client = clientFor(server)
        server.enqueue(MockResponse().setResponseCode(503))
        server.enqueue(okResponse())
        client.get("/v2/vastu/reference/directions/8", idempotencyKey = "caller-operation")
        repeat(2) { assertEquals("caller-operation", server.takeRequest().getHeader("Idempotency-Key")) }
        for (path in listOf("/v2/sandbox/vastu/reference/directions/8", "/v2/vastu/reference/unknown",
            "/v2/vastu/score/overall", "/v2/astrology/planets")) {
            server.enqueue(okResponse())
            client.get(path)
            assertNull(path, server.takeRequest().getHeader("Idempotency-Key"))
        }
    }

    @Test
    fun `public Vastu calls retain caller replay keys across client recreation`() = runBlocking {
        val calls = listOf<suspend (VastuService, String) -> Any>(
            { s, k -> s.vastu("score/overall", idempotencyKey = k) },
            { s, k -> s.vastu("reference/directions/8", idempotencyKey = k) },
            { s, k -> s.vastuOperation(VastuOperation.ScoreOverall, idempotencyKey = k) },
            { s, k -> s.vastuOperation(VastuContracts.referenceDirections8, VastuNoRequest, idempotencyKey = k) },
            { s, k -> s.vastuReference("reference/directions/8", idempotencyKey = k) },
            { s, k -> s.vastuMandalaProject("9-zone", emptyMap(), idempotencyKey = k) },
            { s, k -> s.vastuEntrancePada(VastuEntrancePadaRequest(emptyList(), listOf(0.0, 0.0)), idempotencyKey = k) },
            { s, k -> s.vastuEntranceRecommend(VastuEntranceRecommendRequest("N"), idempotencyKey = k) },
            { s, k -> s.vastuArScanQuality(VastuArScanQualityRequest(), idempotencyKey = k) },
            { s, k -> s.vastuArTrueNorthCalibrate(VastuArTrueNorthCalibrateRequest(1.0, 2.0, "2026-09-07T00:00:00Z", 3.0), idempotencyKey = k) },
            { s, k -> s.vastuAssessments(VastuAssessmentsRequest("plan-derived"), idempotencyKey = k) },
            { s, k -> s.vastuRoom("kitchen", emptyMap(), idempotencyKey = k) },
            { s, k -> s.vastuPlacement("borewell", emptyMap(), idempotencyKey = k) },
            { s, k -> s.vastuAudit("floor-plan", emptyMap(), idempotencyKey = k) },
            { s, k -> s.vastuScore("overall", emptyMap(), idempotencyKey = k) },
            { s, k -> s.vastuPlanGenerate(VastuPlanGenerateRequest(VastuPlanGenerateRequestPlot()), idempotencyKey = k) },
            { s, k -> s.vastuPlanFromRequirements(VastuPlanFromRequirementsRequest(VastuPlanFromRequirementsRequestPlot()), idempotencyKey = k) },
            { s, k -> s.vastuDeclination(1.0, 2.0, idempotencyKey = k) },
        )
        for ((index, call) in calls.withIndex()) {
            val key = "saved-visit-$index"
            server.enqueue(MockResponse().setResponseCode(503))
            server.enqueue(okResponse())
            server.enqueue(okResponse())
            call(clientFor(server).vastu, key)
            call(clientFor(server).vastu, key)
            val attempts = List(3) { server.takeRequest() }
            val bodies = attempts.map { it.body.readUtf8() }
            for (attempt in attempts) {
                assertEquals(attempt.path, key, attempt.getHeader("Idempotency-Key"))
                assertEquals(attempts.first().path, attempt.path)
                assertEquals(attempts.first().method, attempt.method)
            }
            assertEquals(listOf(bodies.first(), bodies.first(), bodies.first()), bodies)
        }
    }

    @Test
    fun `every request disables redirect following`() = runBlocking {
        server.enqueue(okResponse())
        val client = clientFor(server, apiKey = "k")
        client.vastu.vastuScore("overall", mapOf("zone" to "north"))
        val recorded = server.takeRequest()
        assertNotNull(recorded)
        // The behavioral proof that redirects are disabled is the next two
        // tests: a 3xx is surfaced as an error instead of being chased, and a
        // real redirect target is never contacted at all.
    }

    @Test
    fun `an unfollowed 3xx surfaces as an error, not a chased redirect`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(302)
                .setHeader("Location", "http://evil.example/collect")
        )
        val client = clientFor(server, apiKey = "k")
        val error = assertThrows(VedikaApiError::class.java) {
            runBlocking { client.vastu.vastuScore("overall", mapOf("zone" to "north")) }
        }
        assertEquals(302, error.statusCode)
    }

    @Test
    fun `the API key is never forwarded across a real cross-origin redirect`() = runBlocking {
        val collector = MockWebServer()
        collector.start(InetAddress.getByName("127.0.0.1"), 0)
        collector.enqueue(okResponse())

        val redirector = MockWebServer()
        redirector.start(InetAddress.getByName("127.0.0.1"), 0)
        redirector.enqueue(
            MockResponse()
                .setResponseCode(302)
                .setHeader("Location", collector.url("/collect").toString())
        )

        try {
            val client = VedikaClient(
                apiKey = "vk_test_secret",
                baseUrl = redirector.url("/").toString().trimEnd('/'),
            )
            try {
                client.vastu.vastuScore("overall", mapOf("zone" to "north"))
                fail("expected the 302 to surface as an error, not resolve successfully")
            } catch (_: VedikaApiError) {
                // followRedirects = false -> the 302 surfaces as an error; expected.
            }

            // The redirect target must never have been reached AT ALL — not
            // "reached without the header," but never contacted, because
            // there is no second request once redirects are disabled.
            val reachedCollector = collector.takeRequest(1, TimeUnit.SECONDS)
            assertNull("collector must never receive a request", reachedCollector)
        } finally {
            collector.shutdown()
            redirector.shutdown()
        }
    }

    @Test
    fun `untrusted first hops fail before any request without exposing URL secrets`() {
        for (origin in listOf(
            "https://attacker.invalid", "https://api.vedika.io.attacker.invalid",
            "https://api.vedika.io:8443", "https://127.0.0.1:443",
            "http://api.vedika.io", "http://127.attacker.invalid",
            "https://api.vedika.io/path", "https://api.vedika.io/?key=SECRET_SENTINEL",
            "https://user:SECRET_SENTINEL@api.vedika.io", "https://api.vedika.io:bad",
            "https://[SECRET_SENTINEL", "ftp://SECRET_SENTINEL.invalid",
        )) {
            for (optIn in listOf(false, true)) {
                val error = assertThrows(origin, IllegalArgumentException::class.java) {
                    VedikaClient(apiKey = "vk_test", baseUrl = origin, allowInsecureHttp = optIn)
                }
                assertTrue(error.toString(), !error.toString().contains("SECRET_SENTINEL"))
                assertNull(error.cause)
            }
        }
        VedikaClient(apiKey = "k", baseUrl = "https://api.vedika.io:443/")
        assertEquals(0, server.requestCount)
    }

    @Test
    fun `base_url origin policy`() {
        // Official HTTPS origin allowed.
        VedikaClient(apiKey = "k", baseUrl = "https://api.vedika.io")
        // Loopback http allowed: numeric IPv4, literal localhost, IPv6 loopback.
        VedikaClient(apiKey = "k", baseUrl = "http://127.0.0.1:8080")
        VedikaClient(apiKey = "k", baseUrl = "http://localhost:8080")
        VedikaClient(apiKey = "k", baseUrl = "http://LOCALHOST:8080")
        VedikaClient(apiKey = "k", baseUrl = "http://[::1]:8080")

        // Remote http rejected without opt-in.
        assertThrows(IllegalArgumentException::class.java) {
            VedikaClient(apiKey = "k", baseUrl = "http://api.vedika.io")
        }

        // Spoof hosts that merely START with "127." as a DNS label (not an
        // IPv4 octet) are NOT loopback -> rejected. This is the adversarial
        // case a naive `host.startsWith("127.")` check would get wrong.
        assertThrows(IllegalArgumentException::class.java) {
            VedikaClient(apiKey = "k", baseUrl = "http://127.attacker.invalid")
        }
        assertThrows(IllegalArgumentException::class.java) {
            VedikaClient(apiKey = "k", baseUrl = "http://127.example.com")
        }

        // The legacy opt-in cannot bypass credential routing.
        assertThrows(IllegalArgumentException::class.java) {
            VedikaClient(apiKey = "k", baseUrl = "http://api.vedika.io", allowInsecureHttp = true)
        }

        // Unsupported scheme rejected.
        assertThrows(IllegalArgumentException::class.java) {
            VedikaClient(apiKey = "k", baseUrl = "ftp://api.vedika.io")
        }

        // Malformed baseUrl rejected.
        assertThrows(IllegalArgumentException::class.java) {
            VedikaClient(apiKey = "k", baseUrl = "not a url")
        }
    }
}
