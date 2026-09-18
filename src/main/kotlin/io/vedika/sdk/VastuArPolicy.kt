package io.vedika.sdk

/** Platform-free decisions shared by the Android sensor adapter and JVM tests. */
internal object VastuArPolicy {
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
