package io.vedika.sdk

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class VastuArPolicyTest {
    @Test fun `AR trust includes scheme host port and refuses user information`() {
        val target = "https://vedika.io/vastu-ar/"
        assertTrue(VastuArPolicy.trustedPage("https://vedika.io:443/other", target))
        for (url in listOf(null, "http://vedika.io/", "https://vedika.io:8443/", "https://evil.example/", "https://user@vedika.io/", "file:///index.html")) {
            assertFalse(url, VastuArPolicy.trustedPage(url, target))
        }
    }

    @Test
    fun `display axes preserve the compass direction across all screen rotations`() {
        // Android SensorManager uses X=1, Y=2 and bit 0x80 for a negative axis.
        // Apply the selected axis basis to a known flat compass pose. A screen
        // quarter-turn must move its top edge exactly one quarter-turn too.
        for (heading in listOf(0.0, 37.0, 180.0, 359.0)) {
            val radians = Math.toRadians(heading)
            val east = doubleArrayOf(cos(radians), sin(radians))
            val north = doubleArrayOf(-sin(radians), cos(radians))
            for (rotation in 0..3) {
                val axes = VastuArPolicy.displayAxes(rotation)!!
                val originalAxis = if ((axes.first and 0x7f) == 2) 0 else 1
                val axis = if (originalAxis == 0) axes.first else axes.second
                val sign = if ((axis and 0x80) != 0) -1 else 1
                val actual = (Math.toDegrees(atan2(sign * east[originalAxis], sign * north[originalAxis])) + 360) % 360
                assertEquals("heading=$heading rotation=$rotation", (heading + 90 * rotation) % 360, actual, 0.00001)
            }
        }
        assertNull(VastuArPolicy.displayAxes(-1))
        assertNull(VastuArPolicy.displayAxes(4))
    }

    @Test
    fun `sensor quality stays a class and unreliable or unknown status is refused`() {
        assertEquals("low", VastuArPolicy.accuracyClass(1))
        assertEquals("medium", VastuArPolicy.accuracyClass(2))
        assertEquals("high", VastuArPolicy.accuracyClass(3))
        for (status in listOf(-1, 0, 4, 999)) assertNull(VastuArPolicy.accuracyClass(status))
    }

    @Test
    fun `malformed rotation vectors cannot enter heading math`() {
        assertTrue(VastuArPolicy.validRotationVector(floatArrayOf(0f, 0f, 0f)))
        assertTrue(VastuArPolicy.validRotationVector(floatArrayOf(0f, 0f, 0f, 1f, 0.1f)))
        for (values in listOf(floatArrayOf(), floatArrayOf(0f, 0f), floatArrayOf(Float.NaN, 0f, 0f), floatArrayOf(0f, 0f, Float.POSITIVE_INFINITY))) {
            assertFalse(VastuArPolicy.validRotationVector(values))
        }
    }
    @Test fun `location quality rejects malformed fields and preserves zero coordinates`() {
        fun usable(lat: Double = 0.0, lon: Double = 0.0, alt: Double? = null, accuracy: Float? = 0f) =
            VastuArPolicy.usableLocation(lat, lon, alt, accuracy, 1L, 1000L, 1L, 1000L)
        assertTrue(usable())
        assertTrue(usable(90.0, -180.0, -100.0, 500f))
        for (accuracy in listOf(null, -1f, Float.NaN, Float.POSITIVE_INFINITY, 500.1f)) assertFalse(usable(accuracy = accuracy))
        for (lat in listOf(Double.NaN, Double.POSITIVE_INFINITY, 90.1, -90.1)) assertFalse(usable(lat = lat))
        for (lon in listOf(Double.NaN, 180.1, -180.1)) assertFalse(usable(lon = lon))
        for (alt in listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.MAX_VALUE)) assertFalse(usable(alt = alt))
    }

    @Test fun `location age refuses future missing and stale fixes at exact boundaries`() {
        fun usable(elapsed: Long, time: Long, nowNanos: Long, nowMillis: Long) =
            VastuArPolicy.usableLocation(0.0, 0.0, null, 1f, elapsed, time, nowNanos, nowMillis)
        assertTrue(usable(1L, 1000L, 1_800_000_000_001L, 1000L))
        assertFalse(usable(1L, 1000L, 1_800_000_000_002L, 1000L))
        assertFalse(usable(2L, 1000L, 1L, 1000L))
        assertFalse(usable(-1L, 1000L, 1L, 1000L))
        assertFalse(usable(0L, 0L, 1L, 0L))
        assertFalse(usable(0L, 1001L, 1L, 1000L))
        assertTrue(usable(0L, 1L, 1L, 1_800_001L))
        assertFalse(usable(0L, 1L, 1L, 1_800_002L))
    }
}
