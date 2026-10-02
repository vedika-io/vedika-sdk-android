package io.vedika.sdk

import java.net.InetAddress
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class VastuJobsTest {
    private val job = "vjob_0123456789abcdef0123456789abcdef"

    private fun request() = VastuJobsRequest(
        listOf(VastuJobsRequestItem("p1", VastuAssessmentsRequest("plan-derived"))),
        webhookId = "wh_12345678",
    )

    private val submitted = """{"success":true,"data":{"jobId":"$job","status":"queued","itemCount":1,"maxCharge":0.1,"replayed":false}}"""
    private val status = """{"success":true,"data":{"jobId":"$job","status":"running","operation":"assessments","itemCount":1,
        "counts":{"succeeded":0,"failed":0,"pending":1,"cancelled":0},
        "billing":{"currency":"USD","pricePerItem":0.1,"maxCharge":0.1,"charged":0,"basis":"per item"},
        "cancelRequested":false,"createdAt":1,"updatedAt":1,"expiresAt":9,"resultsUrl":"https://api.vedika.io/v2/vastu/jobs/$job/results"}}"""

    private fun json(body: String, code: Int = 200) =
        MockResponse().setResponseCode(code).setHeader("Content-Type", "application/json").setBody(body)

    private fun server(): MockWebServer = MockWebServer().also { it.start(InetAddress.getByName("127.0.0.1"), 0) }
    private fun client(server: MockWebServer) = VedikaClient(apiKey = "vk_test", baseUrl = server.url("/").toString())

    @Test fun `the inventory declares all 98 paths and refuses templated job paths`() {
        val paths = VastuOperation.values().map { it.path }
        assertEquals(98, paths.size)
        assertEquals(98, paths.toSet().size)
        assertTrue(paths.containsAll(listOf("jobs", "jobs/{id}", "jobs/{id}/results", "jobs/{id}/cancel")))
        val server = server()
        try {
            val client = client(server)
            for (templated in listOf(VastuOperation.JobsId, VastuOperation.JobsIdResults, VastuOperation.JobsIdCancel)) {
                assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastuOperation(templated) } }
            }
            assertEquals(0, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun `request body names the operation and carries items under input`() {
        val body = request().toMap()
        assertEquals("assessments", body["operation"])
        assertEquals("wh_12345678", body["webhookId"])
        val item = (body["items"] as List<*>).single() as Map<*, *>
        assertEquals("p1", item["id"])
        assertEquals("plan-derived", (item["input"] as Map<*, *>)["inputSource"])
        assertFalse(VastuJobsRequest(request().items).toMap().containsKey("webhookId"))
    }

    @Test fun `submit needs a retained key before any network request`() {
        val server = server()
        try {
            val client = client(server)
            for (key in listOf("", "   ")) {
                assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastuJobSubmit(request(), key) } }
                assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastu("jobs", request().toMap(), idempotencyKey = key) } }
            }
            assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastu("jobs", request().toMap()) } }
            assertEquals(0, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun `submit accepts the 202 answer and keeps its key across a retry and a new client`() = runBlocking {
        val server = server()
        try {
            server.enqueue(MockResponse().setResponseCode(503).setBody("{}"))
            repeat(2) { server.enqueue(json(submitted, 202)) }
            repeat(2) {
                val result = client(server).vastu.vastuJobSubmit(request(), "saved-job-7")
                assertTrue(result.success)
                assertEquals(job, result.data.jobId)
                assertEquals("queued", result.data.status)
                assertFalse(result.data.replayed)
            }
            repeat(3) {
                val wire = server.takeRequest()
                assertEquals("POST", wire.method)
                assertEquals("/v2/astrology/vastu/jobs", wire.path)
                assertEquals("saved-job-7", wire.getHeader("Idempotency-Key"))
                val body = JSONObject(wire.body.readUtf8())
                assertEquals("assessments", body.getString("operation"))
                assertEquals("p1", body.getJSONArray("items").getJSONObject(0).getString("id"))
            }
        } finally { server.shutdown() }
    }

    @Test fun `status results and cancel use the right verbs and paths`() = runBlocking {
        val server = server()
        try {
            server.enqueue(json(status))
            server.enqueue(json("""{"success":true,"data":{"jobId":"$job","jobStatus":"running","results":[],"nextCursor":null}}"""))
            server.enqueue(json(status.replace("\"cancelRequested\":false", "\"cancelRequested\":true")))
            val client = client(server)
            assertEquals(1, client.vastu.vastuJobStatus(job).data.counts.pending)
            assertNull(client.vastu.vastuJobResults(job, "abc").data.nextCursor)
            assertTrue(client.vastu.vastuJobCancel(job).data.cancelRequested)
            val seen = List(3) { server.takeRequest() }
            assertEquals(listOf("GET", "GET", "POST"), seen.map { it.method })
            assertEquals("/v2/astrology/vastu/jobs/$job", seen[0].path)
            assertEquals("/v2/astrology/vastu/jobs/$job/results?cursor=abc", seen[1].path)
            assertEquals("/v2/astrology/vastu/jobs/$job/cancel", seen[2].path)
        } finally { server.shutdown() }
    }

    @Test fun `generic escape hatch sends GET for status and results and POST for cancel`() = runBlocking {
        val server = server()
        try {
            repeat(3) { server.enqueue(json(status)) }
            val client = client(server)
            client.vastu.vastu("jobs/$job")
            client.vastu.vastu("jobs/$job/results", mapOf("cursor" to "c1"))
            client.vastu.vastu("jobs/$job/cancel")
            assertEquals(listOf("GET", "GET", "POST"), List(3) { server.takeRequest().method })
            assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastu("jobs/../../x") } }
            assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastu("jobs/vjob_x/results") } }
            assertEquals(3, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun `job ids and cursors are checked before any request`() {
        val server = server()
        try {
            val client = client(server)
            for (bad in listOf("", "vjob_x", "../keys", "$job/../x")) {
                assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastuJobStatus(bad) } }
                assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastuJobCancel(bad) } }
            }
            assertThrows(IllegalArgumentException::class.java) { runBlocking { client.vastu.vastuJobResults(job, "x".repeat(33)) } }
            assertEquals(0, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun `result items follow nextCursor to the end`() = runBlocking {
        val server = server()
        try {
            fun item(i: Int) = """{"id":"p$i","index":$i,"status":200,"response":{"success":true}}"""
            server.enqueue(json("""{"success":true,"data":{"jobId":"$job","jobStatus":"running","results":[${item(0)},${item(1)}],"nextCursor":"c2"}}"""))
            server.enqueue(json("""{"success":true,"data":{"jobId":"$job","jobStatus":"completed","results":[${item(2)}],"nextCursor":null}}"""))
            val items = client(server).vastu.vastuJobResultItems(job).toList()
            assertEquals(listOf(0, 1, 2), items.map { it.index })
            assertEquals(200, items[0].status)
            assertNull(items[0].code)
            assertEquals("/v2/astrology/vastu/jobs/$job/results", server.takeRequest().path)
            assertEquals("/v2/astrology/vastu/jobs/$job/results?cursor=c2", server.takeRequest().path)
        } finally { server.shutdown() }
    }
}
