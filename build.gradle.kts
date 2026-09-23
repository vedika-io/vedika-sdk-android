// Vedika Android SDK — native Kotlin/OkHttp client for the Vastu surface.
//
// Deliberately a plain `kotlin("jvm")` module, NOT `com.android.library`
// (the shape `integrations/widgets/android/build.gradle.kts` uses). This code
// has zero Android-framework dependency (no Context, no View, no Compose) —
// it is OkHttp + Kotlin only — so it builds and unit-tests on a bare JDK with
// no Android SDK / AGP / ANDROID_HOME installed, and is still a completely
// ordinary dependency for a real Android app to consume (Android apps take
// plain JVM/Kotlin libraries all the time). This also matches the plan's
// explicit call-out: "a plain JVM/Android library (no Compose dependency
// needed)".
plugins {
    kotlin("jvm") version "2.0.20"
}

group = "io.vedika"
version = "1.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

// `VastuArView.kt` (2026-08-13, native AR sensor bridge) is the ONE file in
// this module that touches real Android-framework classes (`WebView`,
// `SensorManager`, `Context`) — see the plugin-block comment above for why
// every OTHER file here is plain JVM. It needs `android.jar` on the compile
// classpath to build, and no full AGP application/library plugin.
//
// 2026-08-22 packaging fix (b2c#21/ar#17): this file used to be excluded
// from compilation entirely, which meant the published SDK jar never
// contained it — a buyer had to copy the `.kt` source into their own app by
// hand to get the AR view at all. That is fixed here with a `compileOnly`
// dependency on the Android SDK's `android.jar` (resolved from
// `ANDROID_HOME`/`ANDROID_SDK_ROOT`, same env vars the Android Gradle Plugin
// and Android Studio use): `android.jar` supplies only compile-time stubs
// (every method throws at runtime in that jar), so it is never bundled into
// this module's output jar and adds no runtime dependency — a real Android
// device supplies the real implementations, which is exactly what
// `compileOnly` means. With this jar on the classpath, `VastuArView.kt`
// compiles as an ordinary part of this module's main source set and ships
// inside the published artifact like every other class here. A consuming
// Android app now gets it via a normal `implementation("io.vedika:vedika-android-sdk:1.0.0")`
// dependency — no manual file copy.
//
// A bare-JDK host can still run the client tests without the AR class.
// Packaging requires the Android SDK: the jar task must never succeed with
// an artifact that silently omits VastuArView. The separate verification
// dependency below also runs when the jar would otherwise be up to date.
val androidHome: String? = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
val androidJar: File? = androidHome
    ?.let { home ->
        // Prefer the newest installed platform; fall back through older ones.
        listOf("android-36", "android-35", "android-34")
            .map { file("$home/platforms/$it/android.jar") }
            .firstOrNull { it.exists() }
    }

if (androidJar == null) {
    sourceSets {
        main {
            kotlin {
                exclude("**/VastuArView.kt")
            }
        }
    }
}

val verifyArPackagingSdk = tasks.register("verifyArPackagingSdk") {
    doLast {
        check(androidJar != null) {
            "Android SDK platform 34, 35, or 36 is required to package VastuArView. " +
                "Set ANDROID_HOME or ANDROID_SDK_ROOT; bare-JDK hosts can run gradle test."
        }
    }
}

tasks.named("jar") {
    dependsOn(verifyArPackagingSdk)
}

// Kotlin tests need main's internal declarations, not its distributable jar.
// Use compiled classes as friend paths so bare-JDK tests do not package an SDK.
tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileTestKotlin") {
    friendPaths.setFrom(sourceSets.main.get().output.classesDirs)
}

dependencies {
    if (androidJar != null) {
        compileOnly(files(androidJar))
    }

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Suspend-function API (idiomatic for an Android client: callers invoke
    // these from a coroutine scope, e.g. `viewModelScope.launch { ... }`,
    // without risking NetworkOnMainThreadException). Deliberately
    // `kotlinx-coroutines-core`, NOT the plan's suggested `-android` variant:
    // this module only needs `Dispatchers.IO` to move the blocking
    // OkHttp `Call.execute()` off the caller's dispatcher, which lives in
    // `-core`. `-android` additionally wires `Dispatchers.Main` to the
    // platform `Looper`, which needs real Android platform classes on the
    // compile classpath and buys nothing here — the calling app already
    // depends on `-android` transitively if it needs `Dispatchers.Main`.
    // Using `-core` keeps this module buildable as pure JVM without
    // ANDROID_HOME, while remaining exactly as usable inside a real Android
    // app (which already pulls `-core` transitively via `-android`).
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // org.json: real Android devices provide a REAL implementation of
    // org.json.* on the platform classpath at runtime, so production code
    // only needs the API surface at compile time (`compileOnly`) — bundling
    // the Maven jar too would duplicate those classes at APK dex-merge time.
    // Plain JVM unit tests (this module's `test` source set) have no Android
    // runtime underneath them, so the test classpath needs the real jar to
    // get working JSONObject/JSONArray instead of Android's unit-test stub
    // (which throws `RuntimeException: not mocked` on every call).
    compileOnly("org.json:json:20240303")
    testImplementation("org.json:json:20240303")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks.test {
    useJUnit()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
    }
}

// Committed shared-engine resources must be current before a distributable jar.
//
// This checks the bundled runtime against the WEB ENGINE it is generated from, so
// it can only run inside the monorepo. Published as a standalone repository the
// generator is not present, and the task skips rather than failing the build — the
// bundled resources are still verified against their own manifest by
// `VastuRuntimeBundleTest`, which ships with the package and always runs.
val vastuRuntimeGenerator = rootDir.resolve("../../scripts/sdks/package-vastu-native-runtime.mjs")
val verifyVastuRuntime = tasks.register<Exec>("verifyVastuRuntime") {
    workingDir = rootDir.resolve("../..")
    commandLine("node", "scripts/sdks/package-vastu-native-runtime.mjs", "--check")
    onlyIf {
        val present = vastuRuntimeGenerator.isFile
        if (!present) logger.lifecycle("verifyVastuRuntime: skipped, no monorepo generator (standalone checkout)")
        present
    }
}
tasks.named("jar") { dependsOn(verifyVastuRuntime) }
