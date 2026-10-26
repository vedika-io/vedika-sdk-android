package io.vedika.sdk
import org.junit.Assert.*
import org.junit.Test
import org.json.JSONObject

class VastuPortfolioTest {
    @Test fun attributionSurvivesAssessmentSerialization() {
        val request = VastuAssessmentsRequest("plan-derived", propertyId = "p1", tenantRef = "t1")
        val wire = JSONObject(request.toMap())
        assertEquals("p1", wire.getString("propertyId"))
        assertEquals("t1", wire.getString("tenantRef"))
        assertFalse(VastuAssessmentsRequest("plan-derived").toMap().containsKey("tenantRef"))
    }
    @Test fun nullCapRemovalAndTypedContracts() {
        val request = VastuPortfolioBudgetsSetRequest(tenantRef = "t1", capUsd = null)
        assertTrue(request.toMap().containsKey("capUsd"))
        assertEquals(JSONObject.NULL, request.toMap()["capUsd"])
        assertEquals(VastuOperation.PortfolioBudgetsSet, VastuContracts.portfolioBudgetsSet.operation)
        assertEquals(listOf("p1", "p2"), VastuPortfolioCompareRequest(listOf("p1", "p2")).toMap()["propertyIds"])
    }
}
