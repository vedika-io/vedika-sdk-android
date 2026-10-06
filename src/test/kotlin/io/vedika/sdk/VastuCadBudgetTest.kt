package io.vedika.sdk
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
class VastuCadBudgetTest {
    @Test fun exactBudgetForEveryCADRequest() {
        val requests = listOf(
            VastuPlanImportDxfRequest("synthetic", maxChargeUsd = "0.01"),
            VastuPlanExportDxfRequest(JSONObject(), maxChargeUsd = "0.01"),
            VastuPlanImportIfcRequest("synthetic", maxChargeUsd = "0.01")
        )
        requests.forEach { assertEquals("0.01", it.toMap()["maxChargeUsd"]) }
        assertFalse(VastuPlanImportDxfRequest("synthetic").toMap().containsKey("maxChargeUsd"))
    }
}
