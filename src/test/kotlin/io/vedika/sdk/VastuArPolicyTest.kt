package io.vedika.sdk

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs
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

    // ── resolveHeading (2026-09-24 upright/camera-heading fix) ───────────
    //
    // Root cause (Firebase Test Lab, Pixel 8 `shiba` / Pixel 8 Pro `husky`,
    // Android 14): held upright for AR, the old code always reported the
    // DISPLAYED TOP EDGE's azimuth (remapCoordinateSystem + getOrientation),
    // which only agrees with the back camera's own facing direction when the
    // phone lies flat. Every test below is written against
    // `VastuArPolicy.resolveHeading` -- had that function simply returned
    // `topEdgeHeadingDeg` unconditionally (the old behavior, inlined), the
    // upright-mode and camera-azimuth assertions below would all fail,
    // exactly reproducing the on-device symptom (heading off by ~90/~180
    // degrees from the camera direction). That equivalence is asserted
    // directly in the last test in this section.

    /** East/North components of a camera pointed at compass bearing [azimuthDeg]. */
    private fun cameraVector(azimuthDeg: Double): Pair<Double, Double> {
        val r = Math.toRadians(azimuthDeg)
        return sin(r) to cos(r) // (east, north): atan2(east, north) == azimuthDeg
    }

    @Test
    fun `upright portrait reports the camera azimuth for every display rotation`() {
        // "Upright portrait": screenNormalUp near 0 (screen-normal horizontal).
        // The camera azimuth must win regardless of what the top-edge remap
        // says for the currently-displayed rotation (0/1/2/3) -- the camera
        // does not care which way the OS rotated the UI.
        for (cameraAzimuth in listOf(0.0, 90.0, 180.0, 270.0)) {
            val (east, north) = cameraVector(cameraAzimuth)
            for (displayRotationTopEdgeHeading in listOf(0.0, 90.0, 180.0, 270.0)) {
                val (heading, mode) = VastuArPolicy.resolveHeading(
                    screenNormalUp = 0.0,
                    cameraEast = east,
                    cameraNorth = north,
                    topEdgeHeadingDeg = displayRotationTopEdgeHeading,
                    previousMode = VastuArPolicy.HeadingMode.TOP_EDGE,
                )
                assertEquals(VastuArPolicy.HeadingMode.CAMERA, mode)
                assertEquals("cameraAzimuth=$cameraAzimuth topEdge=$displayRotationTopEdgeHeading", cameraAzimuth, heading!!, 1e-6)
            }
        }
    }

    @Test
    fun `upright sideways test-lab pose reports the camera azimuth regardless of display rotation`() {
        // Reproduces the exact evidence: shiba camera azimuth 107.3, heading
        // at rotation natural/left/right 16.8/285.5/104.1 (topEdge values
        // below, all wrong pre-fix); husky camera azimuth 224.9, heading
        // 133.4/11.0/190.9. screenNormalUp ~ -0.2 (device mounted sideways).
        for ((screenNormalUp, cameraAzimuth, topEdgeHeadings) in listOf(
            Triple(-0.2, 107.3, listOf(16.8, 285.5, 104.1)),
            Triple(-0.2, 224.9, listOf(133.4, 11.0, 190.9)),
        )) {
            val (east, north) = cameraVector(cameraAzimuth)
            val mismatchesFromOldTopEdgeBehavior = topEdgeHeadings.count { topEdge ->
                abs(((topEdge - cameraAzimuth) % 360 + 540) % 360 - 180) > 45
            }
            for (topEdge in topEdgeHeadings) {
                val (heading, mode) = VastuArPolicy.resolveHeading(
                    screenNormalUp = screenNormalUp,
                    cameraEast = east,
                    cameraNorth = north,
                    topEdgeHeadingDeg = topEdge,
                    previousMode = VastuArPolicy.HeadingMode.TOP_EDGE,
                )
                assertEquals(VastuArPolicy.HeadingMode.CAMERA, mode)
                assertEquals(cameraAzimuth, heading!!, 1e-6)
            }
            // The old always-top-edge behavior reported `topEdge` verbatim
            // regardless of pose. Per the task evidence, only the ONE
            // display rotation that happens to match the physical pose
            // agrees with the camera by coincidence -- the other two are
            // off by roughly 90/180 degrees. Confirm this fixture reproduces
            // that shape (a majority mismatch), not a coincidental
            // pre-fix/post-fix agreement across the board.
            assertTrue(
                "expected most of $topEdgeHeadings to disagree with camera=$cameraAzimuth under the old top-edge-only formula",
                mismatchesFromOldTopEdgeBehavior >= 2,
            )
        }
    }

    @Test
    fun `flat screen-up keeps the unchanged top-edge behaviour including a quarter turn moving heading 90`() {
        // screenNormalUp near +1 ONLY: flat, screen facing UP. Camera vector
        // is irrelevant here (near-vertical, degenerate) but must not matter
        // -- TOP_EDGE mode uses topEdgeHeadingDeg verbatim. (Screen-down is
        // covered separately below -- since the 2026-09-24 P2 fix it stays
        // in CAMERA mode, not TOP_EDGE; see the signed-threshold doc on
        // resolveHeading.)
        for (screenNormalUp in listOf(1.0, 0.95, 0.81)) {
            val natural = 40.0
            for (quarterTurns in 0..3) {
                val topEdge = (natural + 90.0 * quarterTurns) % 360.0
                val (heading, mode) = VastuArPolicy.resolveHeading(
                    screenNormalUp = screenNormalUp,
                    cameraEast = 0.01, cameraNorth = 0.01, // near-degenerate, must be ignored
                    topEdgeHeadingDeg = topEdge,
                    previousMode = VastuArPolicy.HeadingMode.CAMERA, // even starting from CAMERA mode
                )
                assertEquals(VastuArPolicy.HeadingMode.TOP_EDGE, mode)
                assertEquals(topEdge, heading!!, 1e-9)
            }
        }
        // The quarter turn itself moved the reported heading by exactly 90.
        val h0 = VastuArPolicy.resolveHeading(1.0, 0.0, 0.0, 40.0, VastuArPolicy.HeadingMode.TOP_EDGE).first!!
        val h1 = VastuArPolicy.resolveHeading(1.0, 0.0, 0.0, 130.0, VastuArPolicy.HeadingMode.TOP_EDGE).first!!
        assertEquals(90.0, ((h1 - h0) % 360 + 360) % 360, 1e-9)
    }

    @Test
    fun `flat screen-down stays in camera mode (2026-09-24 P2 fix, not the mirror of screen-up)`() {
        // Before the fix, |screenNormalUp| > 0.80 re-entered TOP_EDGE for a
        // face-down phone too, which is the exact P2 the review reproduced.
        // A face-down phone is just as "upright" (camera horizontal, top
        // edge near-vertical) as a sideways one -- it must stay CAMERA.
        val (east, north) = cameraVector(310.0)
        for (screenNormalUp in listOf(-1.0, -0.99, -0.95, -0.81, -0.5, 0.0)) {
            val (heading, mode) = VastuArPolicy.resolveHeading(
                screenNormalUp = screenNormalUp,
                cameraEast = east, cameraNorth = north,
                topEdgeHeadingDeg = 12.0, // must be ignored -- CAMERA mode
                previousMode = VastuArPolicy.HeadingMode.TOP_EDGE, // even starting from TOP_EDGE
            )
            assertEquals("screenNormalUp=$screenNormalUp", VastuArPolicy.HeadingMode.CAMERA, mode)
            assertEquals("screenNormalUp=$screenNormalUp", 310.0, heading!!, 1e-6)
        }
    }

    @Test
    fun `hysteresis holds the previous mode inside the band and requires the full gap to flip back`() {
        val (east, north) = cameraVector(200.0)
        // Inside the band (0.70 < |up| < 0.80): previous mode wins.
        for (up in listOf(0.70001, 0.75, 0.79999)) {
            val fromTopEdge = VastuArPolicy.resolveHeading(up, east, north, 55.0, VastuArPolicy.HeadingMode.TOP_EDGE)
            assertEquals("up=$up from TOP_EDGE", VastuArPolicy.HeadingMode.TOP_EDGE, fromTopEdge.second)
            assertEquals(55.0, fromTopEdge.first!!, 1e-9)

            val fromCamera = VastuArPolicy.resolveHeading(up, east, north, 55.0, VastuArPolicy.HeadingMode.CAMERA)
            assertEquals("up=$up from CAMERA", VastuArPolicy.HeadingMode.CAMERA, fromCamera.second)
            assertEquals(200.0, fromCamera.first!!, 1e-6)
        }
        // Past either edge of the band, mode is forced regardless of previousMode.
        val belowBand = VastuArPolicy.resolveHeading(0.65, east, north, 55.0, VastuArPolicy.HeadingMode.TOP_EDGE)
        assertEquals(VastuArPolicy.HeadingMode.CAMERA, belowBand.second)
        val aboveBand = VastuArPolicy.resolveHeading(0.85, east, north, 55.0, VastuArPolicy.HeadingMode.CAMERA)
        assertEquals(VastuArPolicy.HeadingMode.TOP_EDGE, aboveBand.second)
        // A single tick sitting exactly at the boundary constants keeps the
        // previous mode (strict inequalities on both sides).
        val atCameraThreshold = VastuArPolicy.resolveHeading(0.70, east, north, 55.0, VastuArPolicy.HeadingMode.TOP_EDGE)
        assertEquals(VastuArPolicy.HeadingMode.TOP_EDGE, atCameraThreshold.second)
        val atTopEdgeThreshold = VastuArPolicy.resolveHeading(0.80, east, north, 55.0, VastuArPolicy.HeadingMode.CAMERA)
        assertEquals(VastuArPolicy.HeadingMode.CAMERA, atTopEdgeThreshold.second)
    }

    @Test
    fun `degenerate camera vector withholds the sample (null) but stays in camera mode`() {
        // Camera pointed nearly straight up/down: horizontal length under 0.2.
        // 2026-09-24 P2 fix: withhold (null), do NOT fall back to
        // topEdgeHeadingDeg here -- that fallback is not continuous with
        // neighboring camera-mode samples in general (only equal to it in
        // the flat limit), so it reintroduces the same jump class the
        // signed-threshold fix removed. See resolveHeading's @return doc.
        val (heading, mode) = VastuArPolicy.resolveHeading(
            screenNormalUp = 0.1, // clearly upright, CAMERA-eligible
            cameraEast = 0.05, cameraNorth = 0.05, // length ~0.0707 < 0.2
            topEdgeHeadingDeg = 77.0,
            previousMode = VastuArPolicy.HeadingMode.CAMERA,
        )
        assertEquals(VastuArPolicy.HeadingMode.CAMERA, mode) // stays, doesn't re-cross hysteresis
        assertNull(heading) // withheld, not 77.0
    }

    @Test
    fun `pre-fix behaviour (always top-edge) would misreport a synthetic 90-degree-off upright sample`() {
        // Direct equivalence check: the pre-fix code was exactly
        // `heading = topEdgeHeadingDeg` for every pose, with no notion of
        // "upright" at all. Construct a clean, unambiguous case (camera and
        // top-edge deliberately 90 degrees apart, the exact failure shape
        // the Test Lab evidence showed) to prove the fix changes real
        // output rather than being a no-op refactor -- not reusing the
        // evidence fixture above, which happens to include one rotation
        // that nearly matches the camera by coincidence (the task's own
        // "only the matching rotation agrees" note).
        val cameraAzimuth = 50.0
        val topEdge = 140.0 // 90 degrees off, matching the on-device magnitude
        val (east, north) = cameraVector(cameraAzimuth)
        val (heading, mode) = VastuArPolicy.resolveHeading(
            screenNormalUp = 0.0, // bolt upright
            cameraEast = east,
            cameraNorth = north,
            topEdgeHeadingDeg = topEdge,
            previousMode = VastuArPolicy.HeadingMode.TOP_EDGE,
        )
        val preFixHeading = topEdge // the entire old function body was this
        assertEquals(VastuArPolicy.HeadingMode.CAMERA, mode)
        assertEquals(cameraAzimuth, heading!!, 1e-6) // post-fix: correct
        assertEquals(140.0, preFixHeading, 1e-9) // pre-fix: would have been wrong by 90
        assertTrue(abs(preFixHeading - heading) >= 89.0)
    }

    // ── continuous-rotation sweeps (2026-09-24 fix) ────────────
    //
    // The P2 finding: rotating continuously about device X from face-up
    // through upright toward face-down, the OLD abs()-based hysteresis
    // re-armed TOP_EDGE mode once the screen faced far enough down, even
    // though the camera's own azimuth never moved -- a spurious ~180-degree
    // jump. These tests build real rotation matrices via the SAME
    // quaternion-to-matrix formula `SensorManager.getRotationMatrixFromVector`
    // uses (not a hand-picked/re-derived shortcut), sweep a full 0..180
    // degree physical rotation in 1-degree steps, and assert the resolved
    // heading never jumps more than ~5 degrees step-to-step except where a
    // sample is legitimately withheld (null) for a degenerate camera vector.

    /**
     * Row-major 3x3 device->world rotation matrix for a pure rotation of
     * [angleDeg] about the device's own [axis] ("x", "y", or "z"), built via
     * the exact quaternion formula `SensorManager.getRotationMatrixFromVector`
     * uses (AOSP `SensorManager.java`, `getRotationMatrixFromVector`): given
     * a unit quaternion (q1,q2,q3,q0) = (x,y,z,w),
     *   R[0]=1-2q2^2-2q3^2  R[1]=2(q1q2-q3q0)  R[2]=2(q1q3+q2q0)
     *   R[3]=2(q1q2+q3q0)   R[4]=1-2q1^2-2q3^2 R[5]=2(q2q3-q1q0)
     *   R[6]=2(q1q3-q2q0)   R[7]=2(q2q3+q1q0)  R[8]=1-2q1^2-2q2^2
     * A rotation of [angleDeg] about a single unit axis is the quaternion
     * (axis * sin(angle/2), cos(angle/2)) -- this is the real sensor-stack
     * math, not a bespoke re-derivation of a remap algorithm.
     */
    private fun rotationMatrixAboutAxis(axis: Char, angleDeg: Double): DoubleArray {
        val half = Math.toRadians(angleDeg) / 2.0
        val s = sin(half)
        val q0 = cos(half)
        val q1 = if (axis == 'x') s else 0.0
        val q2 = if (axis == 'y') s else 0.0
        val q3 = if (axis == 'z') s else 0.0
        return doubleArrayOf(
            1 - 2 * q2 * q2 - 2 * q3 * q3, 2 * (q1 * q2 - q3 * q0), 2 * (q1 * q3 + q2 * q0),
            2 * (q1 * q2 + q3 * q0), 1 - 2 * q1 * q1 - 2 * q3 * q3, 2 * (q2 * q3 - q1 * q0),
            2 * (q1 * q3 - q2 * q0), 2 * (q2 * q3 + q1 * q0), 1 - 2 * q1 * q1 - 2 * q2 * q2,
        )
    }

    /**
     * The azimuth `SensorManager.getOrientation` would report after
     * `remapCoordinateSystem` selects [displayAxes]'s "Y" mapping for
     * [rotation]: `atan2` of the (East, North) world components of whichever
     * signed device axis that mapping designates (row 0 / row 1 of [r] at
     * the mapped column), matching `getOrientation`'s own
     * `atan2(outR[1], outR[4])`. This reads [displayAxes]'s existing,
     * already-tested bit encoding (axis index in bits 0-1, sign in bit
     * 0x80) against a REAL 3x3 matrix, rather than re-deriving
     * `remapCoordinateSystem`'s full row-copy algorithm.
     */
    private fun topEdgeAzimuthDeg(r: DoubleArray, rotation: Int): Double {
        val code = VastuArPolicy.displayAxes(rotation)!!.second
        val axisIndex = (code and 0x7f) - 1
        val sign = if ((code and 0x80) != 0) -1.0 else 1.0
        val east = sign * r[axisIndex]
        val north = sign * r[3 + axisIndex]
        return (Math.toDegrees(atan2(east, north)) + 360.0) % 360.0
    }

    private fun circularDiff(a: Double, b: Double): Double {
        var d = (b - a) % 360.0
        if (d > 180.0) d -= 360.0
        if (d <= -180.0) d += 360.0
        return d
    }

    /**
     * Sweeps a continuous physical rotation and asserts the resolved
     * heading never jumps by more than [maxStepDeg] between consecutive
     * 1-degree samples, except when either sample was withheld (null) for a
     * degenerate camera vector -- an explicitly documented, acceptable gap,
     * not a silent pass: [minWithheldSamples] requires the sweep to actually
     * pass through such a band (proving the assertion isn't vacuously true),
     * and [maxWithheldSamples] bounds how wide that band may be so a
     * regression that withholds far more than the genuinely-degenerate
     * region near the two flat extremes would also fail this test.
     */
    private fun assertContinuousSweep(
        axis: Char,
        rotation: Int,
        maxStepDeg: Double = 5.0,
        minWithheldSamples: Int = 1,
        maxWithheldSamples: Int = 40,
    ) {
        var mode = VastuArPolicy.HeadingMode.TOP_EDGE
        var previousHeading: Double? = null
        var withheldCount = 0
        for (angleInt in 0..180) { // 0 to 180 degrees in 1-degree steps
            val angle = angleInt.toDouble()
            val r = rotationMatrixAboutAxis(axis, angle)
            val screenNormalUp = r[8]
            val cameraEast = -r[2]
            val cameraNorth = -r[5]
            val topEdge = topEdgeAzimuthDeg(r, rotation)
            val (heading, newMode) = VastuArPolicy.resolveHeading(screenNormalUp, cameraEast, cameraNorth, topEdge, mode)
            mode = newMode
            if (heading == null) {
                withheldCount++
            } else if (previousHeading != null) {
                val jump = abs(circularDiff(previousHeading!!, heading))
                assertTrue("axis=$axis rotation=$rotation angle=$angle jumped $jump from $previousHeading to $heading (mode=$mode)", jump <= maxStepDeg)
            }
            previousHeading = heading
        }
        assertTrue("axis=$axis rotation=$rotation expected at least $minWithheldSamples withheld (degenerate-camera) samples, got $withheldCount", withheldCount >= minWithheldSamples)
        assertTrue("axis=$axis rotation=$rotation withheld $withheldCount samples, wider than the expected degenerate band ($maxWithheldSamples)", withheldCount <= maxWithheldSamples)
    }

    @Test
    fun `continuous sweep about device X in portrait never jumps outside the withheld band`() {
        // Reproduces the exact review repro: pitching about device X from
        // face-up (0) through upright (90) to face-down (180), read as
        // rotation=0 (natural/portrait).
        assertContinuousSweep(axis = 'x', rotation = 0)
    }

    @Test
    fun `continuous sweep about device Y in landscape never jumps outside the withheld band`() {
        // The "portrait/landscape variant": a device held in landscape has
        // its long axis (device Y) horizontal, so the physically analogous
        // pitch motion rotates about device Y, read with rotation=1
        // (landscape) -- displayAxes(1) selects device X (sign-flipped) as
        // the top-edge reference, which is the axis that STAYS fixed under
        // this rotation, exactly mirroring the portrait/X case above.
        assertContinuousSweep(axis = 'y', rotation = 1)
    }
}
