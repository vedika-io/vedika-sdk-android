package io.vedika.sdk

import org.junit.Assert.*
import org.junit.Test

class VastuCalibrationTest {
    @Test fun `sensor quality preserves explicit zero and omitted unknown values`() {
        val missing = VastuArTrueNorthCalibrateRequest(28.6, 77.2, "2026-06-21T03:30:00Z", 70.0).toMap()
        assertFalse(missing.containsKey("deviceHeadingAccuracyDeg"))
        assertFalse(missing.containsKey("headingSampleAgeMs"))
        val known = VastuArTrueNorthCalibrateRequest(28.6, 77.2, "2026-06-21T03:30:00Z", 70.0,
            deviceHeadingAccuracyDeg = 3.0, headingSampleAgeMs = 0.0).toMap()
        assertEquals(3.0, known["deviceHeadingAccuracyDeg"])
        assertEquals(0.0, known["headingSampleAgeMs"])
    }
}
