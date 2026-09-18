package io.vedika.sdk

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.net.Uri
import android.os.SystemClock
import android.view.WindowManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceResponse
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Vastu AR Compass — Android native sensor bridge (2026-08-13).
 *
 * ## Why this file is different from every other file in this module
 * Every OTHER file under `io.vedika.sdk` (`VedikaClient`, `VastuService`,
 * `VedikaConfig`, `internal/OriginPolicy`) is deliberately a plain
 * `kotlin("jvm")` module with ZERO Android-framework dependency — see this
 * module's `build.gradle.kts` header: "this code has zero Android-framework
 * dependency (no Context, no View, no Compose) ... it builds and unit-tests
 * on a bare JDK with no Android SDK / AGP / ANDROID_HOME installed."
 *
 * This file breaks that on purpose and cannot follow it: an in-app AR
 * compass needs a real `WebView` (`android.webkit`), a real `SensorManager`
 * (`android.hardware`), and a real `Context` — there is no JVM-only way to
 * host a camera-backed WebView or read a rotation-vector sensor. It is
 * compiled into the SDK jar using the Android SDK's compile-only
 * `android.jar`. Consumers use the packaged class directly. Only bare-JDK
 * client tests exclude this file; `gradle jar` requires a supported Android
 * platform and fails instead of shipping an artifact without the AR class.
 *
 * Compile-verified in isolation for this pass via:
 *   `kotlinc -classpath <sdk>/platforms/android-34/android.jar VastuArView.kt`
 * — this catches every real API-shape error (wrong `SensorManager` method
 * name, wrong `GeomagneticField` constructor, etc.) even though it can't
 * link/run without a device. Deliberately zero `androidx.*` dependency —
 * permission checks below use the plain framework
 * `Context.checkSelfPermission(String)` (API 23+), not
 * `androidx.core.content.ContextCompat` — so this file has nothing beyond
 * `android.jar` to resolve, keeping the isolated `kotlinc` check above a
 * complete, honest compile of this file's real API surface, not a partial
 * one that silently skipped an AndroidX-only path.
 *
 * ## What this file does NOT do (single source of truth, Rule 22 n/a)
 * Zero Vastu compute, zero zone tables, zero deity/element/room content —
 * all of that stays in the shared web engine (`ar-overlay.js` +
 * `heading-provider.js`), loaded inside the `WebView` this class hosts. This
 * class's only job is: (1) host the WebView with camera+location permission
 * plumbing, (2) read Android's own compass sensors, resolve TRUE heading,
 * and push it into the page's JS bridge. It never reads or interprets a
 * heading itself.
 *
 * ## The native -> web contract (see `js/vastu/native-bridge.js`)
 * 1. The bundled runtime installs `window.__VEDIKA_NATIVE__ = true` before
 *    mounting. Custom pages receive an origin-guarded flag at navigation start.
 * 2. On every accepted sensor sample, this class calls exactly:
 *    `window.__vedikaNativeFusion.push(headingDeg, null, frame, {accuracyKind:"quality-class", accuracyClass:"high"})`
 *    — matching `heading-provider.js`'s native-fusion contract exactly (see
 *    that module's "`sample.frame` — frame-aware labeling" header).
 *    `frame` is honest per-sample, NOT a blanket "this class always resolves
 *    true heading" claim (2026-08-13 fix — see FIX 1 below):
 *      - `"true"` — [lastLocation] is set, so [GeomagneticField.getDeclination]
 *        could be computed and applied to the raw magnetic azimuth from
 *        [SensorManager.getOrientation]. This class DOES resolve true heading
 *        itself in this case, same as the pre-fix behavior for every sample.
 *      - `"magnetic"` — [lastLocation] is `null` (the host app has not yet
 *        called [setLocation]), so [GeomagneticField] declination CANNOT be
 *        computed. The pre-fix version of this class withheld the sample
 *        entirely in this case; this version instead pushes the RAW magnetic
 *        azimuth, honestly labeled `"magnetic"` — the web engine
 *        (`heading-provider.js`) shows it as an honest "compass, magnetic
 *        (approximate)" reading rather than either (a) showing nothing at
 *        all, or (b) the pre-fix bug of a magnetic reading silently
 *        mislabeled as true north. Once the host calls [setLocation], later
 *        samples push `"true"`.
 *
 * ## Screen orientation
 * Each sample uses the WebView display rotation and SensorManager's
 * remapCoordinateSystem before extracting azimuth. This keeps heading tied
 * to the displayed top edge in all four orientations, including devices
 * whose natural orientation is landscape. Axis selection has JVM coverage;
 * physical sensor accuracy still requires the device checklist.
 *
 * ## Usage (from the host Android app)
 * ```kotlin
 * class VastuArActivity : AppCompatActivity() {
 *     private lateinit var vastuAr: VastuArView
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         vastuAr = VastuArView(this)
 *         setContentView(vastuAr.webView)
 *         vastuAr.load() // call once, after granting/requesting permissions
 *     }
 *
 *     override fun onResume() {
 *         super.onResume()
 *         vastuAr.start() // registers the SensorManager listener
 *     }
 *
 *     override fun onPause() {
 *         vastuAr.stop() // unregisters it — do this in EVERY lifecycle pause path
 *         super.onPause()
 *     }
 *
 *     override fun onDestroy() {
 *         vastuAr.destroy()
 *         super.onDestroy()
 *     }
 * }
 * ```
 *
 * ## Required host-app manifest entries
 * `AndroidManifest.xml` (of the CONSUMING app, not this SDK module — a
 * library module cannot itself request runtime permissions):
 * ```xml
 * <uses-permission android:name="android.permission.CAMERA" />
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * <uses-permission android:name="android.permission.INTERNET" />
 *
 * <!-- android:required="false" on both -- the AR/compass experience should
 *      degrade (manual North entry, no live camera-through-lens overlay),
 *      not make the whole app uninstallable, on a device lacking either. -->
 * <uses-feature android:name="android.hardware.camera" android:required="false" />
 * <uses-feature android:name="android.hardware.sensor.compass" android:required="false" />
 * ```
 * The host app must ALSO run its own runtime permission request
 * (`ActivityCompat.requestPermissions` / the `androidx.activity.result`
 * contracts API) for `CAMERA` and `ACCESS_FINE_LOCATION` BEFORE calling
 * [load]/[start] — this class checks permission state defensively (see
 * [hasLocationPermission]) but does not itself prompt, matching this SDK's
 * existing pattern of never owning UI it doesn't have to.
 */
class VastuArView(private val context: Context, arUrl: String? = null) : SensorEventListener {

    companion object {
        /** Optional remote page; the constructor defaults to bundled offline resources. */
        const val DEFAULT_AR_URL = "https://vedika.io/vastu-ar/"

        /**
         * Throttle: `TYPE_ROTATION_VECTOR` at `SENSOR_DELAY_GAME` fires far
         * faster than the compass UI (or the web engine's own adaptive
         * smoother — see `heading-provider.js`) needs, and every sample
         * crosses the WebView JS bridge (`evaluateJavascript`), which is not
         * free. ~20Hz (50ms) matches the task's own stated target and is
         * comfortably above what a human eye perceives as "live."
         */
        private const val MIN_PUSH_INTERVAL_MS = 50L

        /**
         * Sensor-honesty fix (2026-08-22, ar#12): [setLocation] used to be
         * trusted forever, at any accuracy, once called once — a fix from an
         * hour ago (host app cached it, never refreshed, user walked
         * indoors/underground) or a poor GPS fix (tens/hundreds of meters
         * off) was still fed to [GeomagneticField] and the result still
         * labeled `"true"`. Declination varies slowly with POSITION (not
         * time), so a stale-but-still-nearby fix is usually still fine for
         * declination math specifically — the real risk this guards is a
         * host that set a location once at app-start and never again, or a
         * cold/degraded GPS fix that is wrong by a country-sized margin. Both
         * thresholds are deliberately generous (this is a coarse "is this
         * fix sane at all," not a precision gate): a location older than
         * this or with a worse-than-this accuracy is treated as ABSENT
         * (falls back to the honest `"magnetic"` path in [onSensorChanged]),
         * never silently accepted.
         */
        private const val MAX_LOCATION_AGE_MS = 30 * 60 * 1000L // 30 minutes

        /** [Location.getAccuracy] is in meters (68% confidence radius, per the platform contract). */
        private const val MAX_LOCATION_ACCURACY_METERS = 500f

        /**
         * b2c#23 / ar#20: rendered in place of the AR page on a main-frame
         * navigation failure (see [onReceivedError] above) so a cold offline
         * start shows an honest, first-party message instead of the
         * platform's own blank/default error chrome. Deliberately plain
         * (system font, no external CSS/JS/image fetch) -- anything it
         * loaded over the network would fail for the exact same reason the
         * AR page did.
         */
        private const val OFFLINE_FALLBACK_HTML = """
            <html><head><meta name="viewport" content="width=device-width, initial-scale=1">
            <style>body{font-family:sans-serif;text-align:center;padding:32px;color:#333}</style>
            </head><body>
            <h3>Vastu AR is unavailable offline</h3>
            <p>This view needs a network connection to load. Reconnect and reopen this screen to try again.</p>
            </body></html>
        """
    }

    /** The hosted WebView. The consuming app attaches this to its own layout (see class doc). */
    val webView: WebView = WebView(context)

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val bundled = arUrl == null
    private val arUrlToLoad: String = arUrl ?: "https://appassets.androidplatform.net/vastu/index.html"
    private val resourceRoot = "io/vedika/sdk/vastu-runtime/"
    private val runtimeFiles = setOf("index.html", "runtime.js", "hud-mandala.png")
    init { require(VastuArPolicy.secureOrigin(arUrlToLoad) != null) { "AR URL must use HTTPS without user information" } }

    private fun resource(name: String): ByteArray = requireNotNull(javaClass.classLoader?.getResourceAsStream(resourceRoot + name)) {
        "Missing bundled Vastu runtime resource: $name"
    }.use { it.readBytes() }

    private fun verifyRuntime() {
        val manifest = JSONObject(String(resource("manifest.json"), Charsets.UTF_8))
        check(manifest.getInt("formatVersion") == 1)
        val files = manifest.getJSONObject("files")
        check(files.keys().asSequence().toSet() == runtimeFiles)
        for (name in runtimeFiles) {
            val actual = MessageDigest.getInstance("SHA-256").digest(resource(name)).joinToString("") { "%02x".format(it) }
            check(actual == files.getString(name)) { "Corrupt bundled Vastu runtime resource: $name" }
        }
    }

    private fun trustedPage(url: String?): Boolean = VastuArPolicy.trustedPage(url, arUrlToLoad)
    private fun guardedScript(js: String): String = "if (window.top === window && location.origin === " +
        JSONObject.quote(Uri.parse(arUrlToLoad).let { "https://${it.host}" + if (it.port == -1 || it.port == 443) "" else ":${it.port}" }) + ") { $js }"

    // Declination needs a location fix — the host app supplies one (its own
    // FusedLocationProviderClient/LocationManager last-known fix; this class
    // deliberately does NOT depend on Google Play Services or request
    // location updates itself, to stay dependency-light). Until a location
    // is set, `onSensorChanged` still pushes samples (2026-08-13 fix — see
    // class doc "native -> web contract") but honestly labeled `"magnetic"`
    // rather than withheld entirely (the earlier, pre-fix behavior) or,
    // worse, mislabeled `"true"`.
    @Volatile private var lastLocation: Location? = null
    @Volatile private var reportedNoLocationOnce = false

    /**
     * Sensor-honesty fix (2026-08-22, ar#9): guards the
     * `SENSOR_STATUS_ACCURACY_UNRELIABLE` refusal in [onSensorChanged] so it
     * reports once per unreliable episode, not on every sensor tick --
     * same "report once" shape as [reportedNoLocationOnce].
     */
    @Volatile private var reportedUnreliableOnce = false

    private var lastPushAtMs: Long = 0L
    private val rotationMatrix = FloatArray(9)
    private val displayRotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var started = false

    /**
     * Sets/updates the location used for [GeomagneticField] declination.
     * Call this whenever the host app obtains a fresh fix — accuracy of the
     * TRUE-heading correction is only as good as this location's freshness
     * and precision. Safe to call before or after [start]/[stop].
     */
    fun setLocation(location: Location) {
        lastLocation = Location(location)
        reportedNoLocationOnce = false // a fresh location may resolve a prior no-location gap
    }

    /** Called when a custom page fails to load. The default runtime is bundled. */
    var onLoadFailed: (() -> Unit)? = null

    /**
     * Configures the WebView (camera/location permission grants, JS/DOM
     * storage, the `__VEDIKA_NATIVE__` flag injection) and loads [arUrlToLoad].
     * Call once, after the WebView is attached to the view hierarchy and
     * after the host app's own runtime permission prompts have resolved
     * (granted or denied — this class works either way; a denial simply
     * means the page's own existing "camera unavailable" / "no live compass"
     * fallbacks show, same as in a plain mobile browser).
     */
    @SuppressLint("SetJavaScriptEnabled") // required: this page is 100% first-party (vedika.io) JS
    fun load() {
        if (bundled) verifyRuntime()
        val settings: WebSettings = webView.settings
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true // sandbox API calls the AR page makes use fetch/localStorage-adjacent state
        settings.mediaPlaybackRequiresUserGesture = false // camera stream must start from the page's own "Start AR" tap, not a second native gesture

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean =
                !request.isForMainFrame || !trustedPage(request.url.toString())

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean = !trustedPage(url)

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
                if (!bundled) return null
                val name = request.url.path?.removePrefix("/vastu/")
                if (!trustedPage(request.url.toString()) || name !in runtimeFiles) {
                    return WebResourceResponse("text/plain", "UTF-8", 403, "Forbidden", emptyMap(), ByteArrayInputStream(byteArrayOf()))
                }
                val mime = when (name) { "index.html" -> "text/html"; "runtime.js" -> "application/javascript"; else -> "image/png" }
                return WebResourceResponse(mime, "UTF-8", ByteArrayInputStream(resource(name!!)))
            }

            // The bundled script installs the native flag before mounting.
            // Custom pages also receive a guarded flag at navigation start;
            // early samples are harmless until their bridge is installed.
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url == "about:blank") return
                if (!trustedPage(url)) { view?.stopLoading(); return }
                view?.evaluateJavascript(guardedScript("window.__VEDIKA_NATIVE__ = true;"), null)
            }

            /**
             * Offline/navigation-failure fix (b2c#23 / ar#20): only acts on
             * the MAIN-FRAME request for [arUrlToLoad] itself (checked via
             * [WebResourceRequest.isForMainFrame] + host match) -- a
             * sub-resource failure (one image/script 404ing) is NOT a "the
             * whole AR page is unreachable" event and must not blank the
             * page out from under a session that is otherwise working.
             */
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                val isMainFrameLoad = request == null || request.isForMainFrame
                if (!isMainFrameLoad) return
                onLoadFailed?.invoke()
                view?.loadDataWithBaseURL(
                    null,
                    OFFLINE_FALLBACK_HTML,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            /**
             * Grants camera (`getUserMedia({video:true})` inside the page) —
             * ORIGIN-CHECKED, not a blanket grant: only `RESOURCE_VIDEO_CAPTURE`
             * requests from the configured AR host are honored, and only when
             * this app itself already holds the `CAMERA` runtime permission
             * (checked via [hasCameraPermission] — WebView's own permission grant
             * is a SEPARATE layer from the OS runtime permission; granting the
             * WebView side without the OS side either does nothing (webview
             * fails the getUserMedia call anyway) or, on older WebView builds,
             * can behave inconsistently — always gate on the real OS grant
             * first). Audio is never requested by this page and is never
             * granted here even if asked.
             */
            override fun onPermissionRequest(request: PermissionRequest) {
                val originIsExpected = trustedPage(request.origin.toString()) && trustedPage(webView.url)
                val wanted = request.resources.filter { it == PermissionRequest.RESOURCE_VIDEO_CAPTURE }
                if (originIsExpected && wanted.isNotEmpty() && hasCameraPermission()) {
                    request.grant(wanted.toTypedArray())
                } else {
                    request.deny()
                }
            }

            /**
             * Geolocation is a SEPARATE WebChromeClient callback from camera
             * (`onPermissionRequest` above only covers `PermissionRequest`'s own
             * resource types — `RESOURCE_VIDEO_CAPTURE`/`RESOURCE_AUDIO_CAPTURE`/
             * `RESOURCE_PROTECTED_MEDIA_ID`/`RESOURCE_MIDI_SYSEX` — `getCurrentPosition`/
             * `watchPosition` inside the page route through THIS callback
             * instead). Same origin + OS-permission gating as camera above.
             */
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                val allow = trustedPage(origin) && trustedPage(webView.url) && hasLocationPermission()
                callback?.invoke(origin, allow, false)
            }
        }

        webView.loadUrl(arUrlToLoad)
    }

    /** Registers the rotation-vector sensor listener. Call from `onResume()` (or equivalent). */
    fun start() {
        if (started) return
        if (rotationVectorSensor == null) return // device has no compass — page's own "no compass" fallback handles this
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME)
        started = true
    }

    /** Unregisters the sensor listener. Call from `onPause()` (or equivalent) — EVERY pause path, no exceptions. */
    fun stop() {
        if (!started) return
        sensorManager.unregisterListener(this)
        started = false
    }

    /** Releases the WebView. Call from `onDestroy()`. Safe to call even if [start]/[load] were never called. */
    fun destroy() {
        stop()
        webView.destroy()
    }

    // ── SensorEventListener ──────────────────────────────────────────────

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        val now = SystemClock.elapsedRealtime()
        if (now - lastPushAtMs < MIN_PUSH_INTERVAL_MS) return // ~20Hz throttle (see MIN_PUSH_INTERVAL_MS)

        val accuracyClass = VastuArPolicy.accuracyClass(event.accuracy)
        if (accuracyClass == null || !VastuArPolicy.validRotationVector(event.values)) {
            lastPushAtMs = now
            if (!reportedUnreliableOnce) {
                reportedUnreliableOnce = true
                pushError("Compass sample is invalid or unreliable. Calibrate the device; heading is withheld until valid samples return.")
            }
            return
        }
        reportedUnreliableOnce = false

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        val axes = VastuArPolicy.displayAxes(currentDisplayRotation() ?: -1) ?: return
        if (!SensorManager.remapCoordinateSystem(rotationMatrix, axes.first, axes.second, displayRotationMatrix)) return
        SensorManager.getOrientation(displayRotationMatrix, orientationAngles)
        val azimuthRad = orientationAngles[0] // [-pi, pi], magnetic north from the displayed top edge
        val magneticHeadingDeg = normalizeDeg(Math.toDegrees(azimuthRad.toDouble()))

        if (!magneticHeadingDeg.isFinite()) return
        // Android reports a quality class, not a measured error in degrees.
        // The web engine uses this class to gate precision without inventing one.
        lastPushAtMs = now

        val location = lastLocation?.takeIf { isLocationUsable(it) }
        if (location == null) {
            // 2026-08-13 fix (FIX 1): push the RAW magnetic azimuth, honestly
            // labeled "magnetic" -- see class doc "native -> web contract" for
            // why this replaced the earlier withhold-entirely behavior.
            // 2026-08-22 fix (ar#12): this branch is now ALSO reached when
            // [lastLocation] is non-null but stale/inaccurate (see
            // [isLocationUsable]) -- a bad fix degrades exactly like no fix
            // at all, rather than being trusted at face value.
            if (!reportedNoLocationOnce) {
                reportedNoLocationOnce = true
                val reason = if (lastLocation == null) {
                    "no location set yet"
                } else {
                    "last location is invalid, stale or low-accuracy (>${MAX_LOCATION_AGE_MS / 60_000}min old or worse than ${MAX_LOCATION_ACCURACY_METERS}m accuracy)"
                }
                pushError("$reason -- pushing MAGNETIC heading only (frame=\"magnetic\"); call VastuArView.setLocation() with a fresh, accurate fix to unlock true heading")
            }
            pushSample(magneticHeadingDeg, accuracyClass, "magnetic")
            return
        }

        val declinationDeg = GeomagneticField(
            location.latitude.toFloat(),
            location.longitude.toFloat(),
            if (location.hasAltitude()) location.altitude.toFloat() else 0f,
            location.time.takeIf { it > 0L } ?: System.currentTimeMillis()
        ).declination.toDouble()

        val trueHeadingDeg = normalizeDeg(magneticHeadingDeg + declinationDeg)
        if (trueHeadingDeg.isFinite()) pushSample(trueHeadingDeg, accuracyClass, "true")
    }

    @Suppress("DEPRECATION") // defaultDisplay is the API 23-compatible pre-attach fallback.
    private fun currentDisplayRotation(): Int? = webView.display?.rotation
        ?: (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.rotation

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed -- SensorEvent.accuracy (read per-sample above) already
        // carries the current accuracy class; a separate callback-driven state
        // would just duplicate it.
    }

    // ── WebView bridge calls ─────────────────────────────────────────────

    /**
     * `window.__vedikaNativeFusion` may not exist yet the FIRST time this
     * fires (native-bridge.js installs it at module-evaluation time, which
     * on a slow device could theoretically race an extremely early sensor
     * callback) -- guarded on the JS side so an early call is a harmless
     * no-op rather than a thrown ReferenceError breaking the page. `frame`
     * MUST be `"true"` or `"magnetic"` (2026-08-13 fix) -- see class doc
     * "native -> web contract" and `native-bridge.js`'s own header for the
     * full honesty-fix reasoning; this class only ever passes one of those
     * two literals (see `onSensorChanged`), never a third value.
     */
    private fun pushSample(headingDeg: Double, accuracyClass: String, frame: String) {
        val js = "if (window.__vedikaNativeFusion) { window.__vedikaNativeFusion.push($headingDeg, null, \"$frame\", {accuracyKind:\"quality-class\",accuracyClass:\"$accuracyClass\"}); }"
        if (trustedPage(webView.url)) webView.evaluateJavascript(guardedScript(js), null)
    }

    private fun pushError(message: String) {
        val escaped = message.replace("\\", "\\\\").replace("\"", "\\\"")
        val js = "if (window.__vedikaNativeFusion) { window.__vedikaNativeFusion.pushError(\"$escaped\"); }"
        if (trustedPage(webView.url)) webView.evaluateJavascript(guardedScript(js), null)
    }

    // ── Permission checks (does not itself request -- see class doc) ────
    // Plain framework `Context.checkSelfPermission(String)` (API 23+) --
    // deliberately NOT `androidx.core.content.ContextCompat.checkSelfPermission`
    // (which pre-API-23-guards internally): this SDK's stated floor is a
    // modern one, and using the framework method directly keeps this file's
    // only external dependency `android.jar` itself (see class doc on why
    // that matters for the isolated `kotlinc` compile check this pass ran).

    private fun hasCameraPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun hasLocationPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /**
     * Sensor-honesty gate (ar#12): a [Location] is only trusted for
     * declination math if it is BOTH recent and reasonably accurate. Age is
     * computed from [SystemClock.elapsedRealtime] against
     * [Location.getElapsedRealtimeNanos] where available (API 17+; monotonic,
     * immune to wall-clock jumps from NTP sync / user clock changes) and
     * falls back to [Location.getTime] (wall-clock epoch millis) only if the
     * platform never populated the elapsed-realtime field. A location
     * missing accuracy entirely ([Location.hasAccuracy] false) is treated as
     * unusable, not optimistically accepted.
     */
    private fun isLocationUsable(location: Location): Boolean {
        return VastuArPolicy.usableLocation(
            location.latitude, location.longitude,
            if (location.hasAltitude()) location.altitude else null,
            if (location.hasAccuracy()) location.accuracy else null,
            location.elapsedRealtimeNanos, location.time,
            SystemClock.elapsedRealtimeNanos(), System.currentTimeMillis()
        )
    }

    private fun normalizeDeg(deg: Double): Double {
        val m = deg % 360.0
        return if (m < 0) m + 360.0 else m
    }
}
