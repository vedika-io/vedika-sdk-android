package io.vedika.sdk

import kotlin.math.atan2
import kotlin.math.sqrt

/** Platform-free decisions shared by the Android sensor adapter and JVM tests. */
internal object VastuArPolicy {

    /**
     * Which device-local vector [resolveHeading] is currently reporting.
     * [TOP_EDGE] is the displayed top edge (via [displayAxes] +
     * `SensorManager.getOrientation`) -- correct only when the device lies
     * flat, screen up. [CAMERA] is the back camera's facing direction --
     * correct when the device is held upright, which is how Vastu AR is
     * actually used (see [resolveHeading]'s header for the root cause).
     */
    enum class HeadingMode { TOP_EDGE, CAMERA }

    /**
     * See [resolveHeading]: below this SIGNED `screenNormalUp` (not its
     * absolute value), prefer [HeadingMode.CAMERA]. Signed on purpose
     * (2026-09-24 fix): this threshold, and
     * [TOP_EDGE_MODE_ENTER_THRESHOLD], must NOT be symmetric around zero.
     * The top-edge formula is only trustworthy when the SCREEN FACES UP
     * (`screenNormalUp` strongly positive); using `abs(screenNormalUp)`
     * also entered [HeadingMode.TOP_EDGE] when the screen faced strongly
     * DOWN, producing a spurious ~180-degree jump at the exact moment the
     * device tipped past that negative threshold while the camera's own
     * azimuth stayed continuous (root-caused and reproduced by
     * `VastuArPolicyTest`'s continuous-sweep tests). This value covers
     * everything below "clearly screen-up," including the entire face-down
     * range down to `-1.0`.
     */
    private const val CAMERA_MODE_ENTER_THRESHOLD = 0.70

    /**
     * See [resolveHeading] and [CAMERA_MODE_ENTER_THRESHOLD]: above this
     * SIGNED `screenNormalUp`, prefer [HeadingMode.TOP_EDGE] -- entered ONLY
     * when the screen faces up (flat on a table, screen visible from
     * above). Never entered for a face-down phone, however flat.
     */
    private const val TOP_EDGE_MODE_ENTER_THRESHOLD = 0.80

    /**
     * See [resolveHeading]: below this horizontal length, the camera-facing
     * vector is too close to vertical (device tipped to look at the
     * ceiling/floor while "upright") for its azimuth to be trustworthy.
     * Matches the device-lab acceptance test's own degenerate-camera guard
     * (`VastuArDeviceTest.devicePose`'s `sqrt(camE*camE+camN*camN) < 0.2`).
     */
    private const val CAMERA_DEGENERATE_HORIZONTAL_LENGTH = 0.2

    /**
     * Resolves which device-local vector to report as the compass heading
     * for one sensor sample.
     *
     * ## Root cause (Firebase Test Lab, Pixel 8 `shiba` / Pixel 8 Pro
     * `husky`, Android 14, 2026-09-24)
     * The pre-fix code always reported the azimuth of the DISPLAYED TOP
     * EDGE: `SensorManager.remapCoordinateSystem` by [displayAxes], then
     * `SensorManager.getOrientation`. That is the compass bearing of a
     * SPECIFIC device-local axis (whichever one [displayAxes] selects for
     * the current display rotation) -- correct only when that axis is
     * roughly horizontal, which is true when the phone lies flat on a
     * table. Vastu AR is used with the phone held UPRIGHT, pointed at a
     * room through the back camera -- the top-edge axis and the back-camera
     * axis are ORTHOGONAL in the device's own frame, so they coincide only
     * when flat and diverge (by roughly 90 degrees, sometimes 180,
     * depending on which of the four [displayAxes] mappings applied) as
     * soon as the phone is held vertically. Measured on-device:
     * `shiba` camera azimuth 107.3 degrees vs. top-edge heading 16.8/285.5/104.1
     * across the three tested display rotations; `husky` camera azimuth
     * 224.9 vs. 133.4/11.0/190.9. Only the rotation that happened to match
     * this device's actual (sideways, rotation-locked) physical pose agreed
     * with the camera by coincidence -- the formula itself has no concept
     * of "held upright," so every other rotation, and every upright pose in
     * general, was wrong by construction, not merely imprecise.
     *
     * ## The fix
     * When the phone is upright (the device Z axis -- the screen's outward
     * normal -- is far from vertical in world space, i.e. [screenNormalUp]
     * is far from +-1), report the back camera's own facing direction
     * instead: world-frame camera direction is `-(R[2], R[5], R[8])` (the
     * back camera looks out of device -Z), so heading is
     * `atan2(-R[2], -R[5])`, normalized to [0, 360). This does not depend on
     * display rotation at all -- the camera points the same real-world
     * direction no matter which way the OS has rotated the displayed UI,
     * which is exactly the property the Test Lab evidence needed (the
     * camera azimuth stayed fixed near 107.3/224.9 across all three tested
     * rotations while the old top-edge heading swung by ~90-180 degrees).
     *
     * When flat, this keeps reporting [topEdgeHeadingDeg] -- the caller's
     * own, already-correct [displayAxes] + `SensorManager.getOrientation`
     * result for the CURRENT display rotation. Recomputing that remap
     * purely in this Android-framework-free module would mean re-deriving
     * `SensorManager.remapCoordinateSystem`'s own axis-permutation algorithm
     * outside the platform -- a second, independently-fallible
     * implementation of platform math this module has no way to verify
     * against real hardware. Accepting the platform's own already-correct
     * result as a parameter is safer and keeps this function's only new
     * responsibility (choosing which vector to trust, and computing the
     * camera one) pure and JVM-testable.
     *
     * ## Hysteresis, and why it is SIGNED (2026-09-24 fix)
     * [CAMERA_MODE_ENTER_THRESHOLD] (0.70) / [TOP_EDGE_MODE_ENTER_THRESHOLD]
     * (0.80) straddle the midpoint of the acceptance test's own two
     * unambiguous-pose bands (flat: `|screenNormalUp| > 0.87`, upright:
     * `< 0.5` -- see `VastuArDeviceTest.screenRotationMovesTheHeadingByTheRotation`),
     * so any pose the device lab itself certifies as flat or upright lands
     * solidly on one side of BOTH switch points, never in the dead zone
     * between them.
     *
     * The comparison is against SIGNED `screenNormalUp`, not `abs(screenNormalUp)`.
     * An earlier version of this function used the absolute value, which
     * re-entered [HeadingMode.TOP_EDGE] whenever the screen faced STRONGLY
     * DOWN (`screenNormalUp` near `-1.0`), not only strongly up. A device
     * lab review reproduced the resulting bug with a continuous rotation
     * about the device's own X axis, starting face-up and pitching through
     * upright toward face-down: at 143 degrees (`screenNormalUp = -0.7987`)
     * the resolved heading was a continuous 0 degrees in [HeadingMode.CAMERA];
     * one degree later, at 144 degrees (`screenNormalUp = -0.8090`), the old
     * `abs()` comparison re-armed [HeadingMode.TOP_EDGE] and the heading
     * JUMPED to 180 degrees -- even though the physical back camera had not
     * moved and was still pointed due north the entire time. The top-edge
     * formula is a fair proxy for "which way is the camera facing" only
     * when the screen faces up (the classic flat-compass-on-a-table pose);
     * a screen facing down is just as "upright" (camera horizontal, top
     * edge near-vertical) as one facing up sideways, and must stay in
     * [HeadingMode.CAMERA] for the same reason. With the signed comparison,
     * [HeadingMode.TOP_EDGE] is reachable only through `screenNormalUp`
     * strongly positive, so the whole face-down half of the rotation stays
     * in [HeadingMode.CAMERA] and the heading remains continuous (see
     * `VastuArPolicyTest`'s continuous-sweep tests, which build real
     * rotation-vector-style matrices and assert no step-to-step jump above
     * 5 degrees anywhere the camera vector is non-degenerate).
     *
     * The 0.10 gap between the two (signed) thresholds still means a device
     * hovering exactly at the screen-up boundary (e.g. propped at a shallow
     * angle) cannot flicker mode on sensor noise alone -- it must cross the
     * whole gap, not just recross one point, to switch again.
     *
     * @param screenNormalUp `R[8]` from `SensorManager.getRotationMatrixFromVector`:
     *   the world-up component of the device's Z axis (the screen's outward
     *   normal). `1.0` lying flat screen-up, `0.0` held vertical, `-1.0`
     *   flat screen-down.
     * @param cameraEast `-R[2]`, @param cameraNorth `-R[5]`: the horizontal
     *   (East, North) components of the back camera's facing direction.
     * @param topEdgeHeadingDeg the caller's own top-edge-remap heading for
     *   the current display rotation (used verbatim in [HeadingMode.TOP_EDGE]
     *   mode only).
     * @param previousMode the mode chosen for the previous accepted sample;
     *   `HeadingMode.TOP_EDGE` is a reasonable initial value (most devices
     *   start face-up on a table before a user picks them up).
     * @return the heading in degrees, or `null` if this sample should be
     *   WITHHELD -- the device is in [HeadingMode.CAMERA] but the camera
     *   vector is too close to vertical (pointed at the ceiling/floor) to
     *   trust. Falling back to [topEdgeHeadingDeg] there (an earlier version
     *   of this function did) reintroduces the same class of jump this fix
     *   removes: [topEdgeHeadingDeg] is not continuous with the camera
     *   azimuth in general, only equal to it in the narrow limit where the
     *   device is truly flat. Withholding is honest; the caller already has
     *   a "skip this sample" path (see [VastuArView.onSensorChanged]'s
     *   existing `if (!magneticHeadingDeg.isFinite()) return`).
     */
    fun resolveHeading(
        screenNormalUp: Double,
        cameraEast: Double,
        cameraNorth: Double,
        topEdgeHeadingDeg: Double,
        previousMode: HeadingMode,
    ): Pair<Double?, HeadingMode> {
        val mode = when {
            screenNormalUp > TOP_EDGE_MODE_ENTER_THRESHOLD -> HeadingMode.TOP_EDGE
            screenNormalUp < CAMERA_MODE_ENTER_THRESHOLD -> HeadingMode.CAMERA
            else -> previousMode
        }
        if (mode == HeadingMode.CAMERA) {
            val horizontal = sqrt(cameraEast * cameraEast + cameraNorth * cameraNorth)
            if (horizontal >= CAMERA_DEGENERATE_HORIZONTAL_LENGTH) {
                val deg = (Math.toDegrees(atan2(cameraEast, cameraNorth)) + 360.0) % 360.0
                return deg to HeadingMode.CAMERA
            }
            // Degenerate: the camera points nearly straight up/down (device
            // tipped past vertical while "upright", or truly flat screen-down
            // -- e.g. pointed at the ceiling/floor). Withhold this sample
            // (see the @return doc above for why NOT to fall back to
            // topEdgeHeadingDeg here) but stay in CAMERA mode so one
            // transient degenerate sample doesn't force re-crossing the
            // whole hysteresis gap to recover.
            return null to HeadingMode.CAMERA
        }
        return topEdgeHeadingDeg to HeadingMode.TOP_EDGE
    }
    fun secureOrigin(value: String?): String? = try {
        val uri = java.net.URI(value ?: "")
        if (uri.scheme != "https" || uri.host.isNullOrBlank() || uri.rawUserInfo != null) null
        else "https://${uri.host.lowercase()}:${if (uri.port == -1) 443 else uri.port}"
    } catch (_: Exception) { null }

    fun trustedPage(value: String?, expected: String): Boolean =
        secureOrigin(value)?.let { it == secureOrigin(expected) } == true

    // SensorManager axis constants: X=1, Y=2; bit 0x80 reverses an axis.
    // Surface rotation maps the natural sensor frame into the displayed frame.
    fun displayAxes(rotation: Int): Pair<Int, Int>? = when (rotation) {
        0 -> 1 to 2
        1 -> 2 to 0x81
        2 -> 0x81 to 0x82
        3 -> 0x82 to 1
        else -> null
    }

    fun accuracyClass(status: Int): String? = when (status) {
        1 -> "low"
        2 -> "medium"
        3 -> "high"
        else -> null
    }

    fun usableLocation(latitude: Double, longitude: Double, altitude: Double?, accuracy: Float?,
                       elapsedNanos: Long, timeMillis: Long, nowNanos: Long, nowMillis: Long): Boolean {
        if (!latitude.isFinite() || latitude !in -90.0..90.0 ||
            !longitude.isFinite() || longitude !in -180.0..180.0 ||
            (altitude != null && !altitude.toFloat().isFinite()) ||
            accuracy == null || !accuracy.isFinite() || accuracy !in 0f..500f) return false
        // Compare before subtracting; retain nanosecond precision at both bounds.
        if (elapsedNanos < 0L) return false
        return if (elapsedNanos > 0L) {
            nowNanos >= elapsedNanos && nowNanos - elapsedNanos <= 1_800_000_000_000L
        } else {
            timeMillis > 0L && nowMillis >= timeMillis && nowMillis - timeMillis <= 1_800_000L
        }
    }

    fun validRotationVector(values: FloatArray): Boolean =
        values.size >= 3 && values.all { it.isFinite() }
}
