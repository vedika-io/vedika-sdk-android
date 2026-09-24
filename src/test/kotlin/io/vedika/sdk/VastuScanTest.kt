package io.vedika.sdk

import java.net.InetAddress
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class VastuScanTest {
    @Test fun `counted scan quality preserves zero and large declared counts`() {
        val request = VastuArCountedScanQualityRequest(roomsTagged = 0, roomCount = 0, expectedRoomCount = 1, coveragePercent = 0.0)
        assertEquals(0L, request.toMap()["roomsTagged"])
        assertEquals(0L, request.toMap()["roomCount"])
        assertEquals(1L, request.toMap()["expectedRoomCount"])
        assertEquals(0.0, request.toMap()["coveragePercent"])
        val coverage = VastuArScanQualityDataRoomCoverage(JSONObject().put("expectedRoomCount", 9007199254740991L))
        assertEquals(9007199254740991L, coverage.expectedRoomCount)
    }

    @Test fun `scan input retains zero bearing and nested room data`() {
        val request = VastuScansSaveRequest("scan-000000000001", "property-00000001", "Kitchen", 1,
            VastuScanSnapshot("self-reported", listOf(VastuScanSnapshotRoomsItem("kitchen", "SE")),
                plotPolygon = listOf(listOf(0.0, 0.0), listOf(10.0, 0.0), listOf(0.0, 10.0)), bearingDeg = 0.0))
        val snapshot = JSONObject(request.toMap()).getJSONObject("snapshot")
        assertEquals(0.0, snapshot.getDouble("bearingDeg"), 0.0)
        assertEquals("SE", snapshot.getJSONArray("rooms").getJSONObject(0).getString("zone"))
        assertFalse(snapshot.has("telemetry"))
        assertEquals(mapOf("requestId" to "request-00000001", "limit" to 1), VastuScansListRequest("request-00000001", 1).toMap())
    }

    @Test fun `scan list keeps caller identity and permits an unbilled response`() = runBlocking {
        val server = MockWebServer()
        server.start(InetAddress.getByName("127.0.0.1"), 0)
        try {
            server.enqueue(MockResponse().setHeader("Content-Type", "application/json").setBody("""{"success":true,"data":{"scans":[],"nextCursor":null}}"""))
            val client = VedikaClient(apiKey = "vk_test", baseUrl = server.url("/").toString())
            val result = client.vastu.vastuScansList(VastuScansListRequest("request-00000001", 1))
            assertTrue(result.success)
            assertNull(result.billing)
            assertNull(result.meta)
            assertTrue(result.data.scans.isEmpty())
            val wire = server.takeRequest()
            assertEquals("POST", wire.method)
            assertEquals("/v2/astrology/vastu/scans/list", wire.path)
            // The server answers 422 to any retry header on scan operations.
            assertNull(wire.getHeader("Idempotency-Key"))
            assertTrue(JSONObject("""{"requestId":"request-00000001","limit":1}""").similar(JSONObject(wire.body.readUtf8())))
        } finally { server.shutdown() }
    }

    @Test fun `scan operations refuse a caller Idempotency-Key before sending`() = runBlocking {
        val client = VedikaClient(apiKey = "vk_test", baseUrl = "https://api.vedika.io")
        val error = runCatching { client.vastu.vastuScansList(VastuScansListRequest("request-00000001", 1), "retained-scan-list") }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
        assertTrue(usesBodyIdentity("/v2/vastu/scans/timelapse"))
        assertFalse(usesBodyIdentity("/v2/vastu/assessments"))
    }
}
