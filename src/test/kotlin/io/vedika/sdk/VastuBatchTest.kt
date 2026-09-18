package io.vedika.sdk

import java.net.InetAddress
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class VastuBatchTest {
    private fun request() = VastuAssessmentsBatchRequest(listOf(
        VastuAssessmentsBatchRequestItemsItem("property-1", VastuAssessmentsRequest("plan-derived")),
        VastuAssessmentsBatchRequestItemsItem("property-2", VastuAssessmentsRequest("seller-supplied")),
    ))

    private val response = """{"success":true,"data":{"results":[
        {"id":"property-1","status":200,"response":{"success":true,"data":{"status":"assessed","score":100}}},
        {"id":"property-2","status":400,"response":{"success":false,"error":"invalid assessment","code":"INVALID_INPUT"}}
    ],"summary":{"total":2,"succeeded":1,"failed":1},"billingBasis":"existing assessment price per item","execution":"synchronous"}}"""

    @Test fun `report and drawing options preserve explicit values and omitted defaults`() {
        assertEquals(false, VastuPlanGenerateRequest(VastuPlanGenerateRequestPlot(), includeSvg = false).toMap()["includeSvg"])
        assertEquals(false, VastuPlanFromRequirementsRequest(VastuPlanFromRequirementsRequestPlot(), includeSvg = false).toMap()["includeSvg"])
        assertEquals(false, VastuPlanOptimizeRequest(emptyList(), includeSvg = false).toMap()["includeSvg"])
        assertFalse(VastuPlanGenerateRequest(VastuPlanGenerateRequestPlot()).toMap().containsKey("includeSvg"))
        val report = VastuPlanReportRequest(emptyList(), format = "html", brand = VastuPlanReportRequestBrand("Title", "Customer"), reportTitle = "Top title", generatedFor = "Buyer", tenantName = "Tenant")
        val body = report.toMap()
        assertEquals("html", body["format"])
        assertEquals(mapOf("reportTitle" to "Title", "generatedFor" to "Customer"), body["brand"])
        assertEquals("Top title", body["reportTitle"])
        assertEquals("Buyer", body["generatedFor"])
        assertEquals("Tenant", body["tenantName"])
        assertEquals(setOf("rooms"), VastuPlanReportRequest(emptyList()).toMap().keys)
    }

    @Test fun `batch rejects missing or blank identity before a network request`() {
        val server = MockWebServer()
        server.start(InetAddress.getByName("127.0.0.1"), 0)
        try {
            val client = VedikaClient(apiKey = "vk_test", baseUrl = server.url("/").toString())
            for (key in listOf(null, "", "   ")) {
                assertThrows(IllegalArgumentException::class.java) {
                    runBlocking { client.vastu.vastu("assessments/batch", request().toMap(), idempotencyKey = key) }
                }
                assertThrows(IllegalArgumentException::class.java) {
                    runBlocking { client.vastu.vastuOperation(VastuOperation.AssessmentsBatch, idempotencyKey = key) }
                }
                if (key != null) {
                    assertThrows(IllegalArgumentException::class.java) {
                        runBlocking { client.vastu.vastuAssessmentsBatch(request(), idempotencyKey = key) }
                    }
                }
            }
            assertEquals(0, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test fun `typed batch preserves wire body and saved identity across retry and client recreation`() = runBlocking {
        val server = MockWebServer()
        server.start(InetAddress.getByName("127.0.0.1"), 0)
        try {
            server.enqueue(MockResponse().setResponseCode(503).setBody("{}"))
            repeat(2) { server.enqueue(MockResponse().setHeader("Content-Type", "application/json").setBody(response)) }
            repeat(2) {
                val client = VedikaClient(apiKey = "vk_test", baseUrl = server.url("/").toString())
                val result = client.vastu.vastuAssessmentsBatch(request(), idempotencyKey = "saved-batch-42")
                assertTrue(result.success)
                assertEquals(2, result.data.summary.total)
                assertEquals(1, result.data.summary.failed)
                assertEquals("property-1", result.data.results[0].id)
                assertEquals(100.0, result.data.results[0].response.data!!.score!!, 0.0)
                assertEquals(400, result.data.results[1].status)
                assertFalse(result.data.results[1].response.success)
                assertNull(result.data.results[1].response.data)
                assertEquals("INVALID_INPUT", result.data.results[1].response.code)
            }
            repeat(3) {
                val wire = server.takeRequest()
                assertEquals("POST", wire.method)
                assertEquals("/v2/astrology/vastu/assessments/batch", wire.path)
                assertEquals("saved-batch-42", wire.getHeader("Idempotency-Key"))
                assertTrue(JSONObject(request().toMap()).similar(JSONObject(wire.body.readUtf8())))
            }
            assertEquals(3, server.requestCount)
        } finally { server.shutdown() }
    }
}
