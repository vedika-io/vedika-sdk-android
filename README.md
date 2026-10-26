# Vedika Android SDK

Native Kotlin client for the [Vedika](https://vedika.io) astrology API, covering the
Vastu surface and the native AR view. OkHttp + Kotlin, with a versioned offline AR
runtime bundled as a resource.

This repository is a mirror. The source of truth is `sdks/android/` in the Vedika
monorepo.

## Install

Via [JitPack](https://jitpack.io/#vedika-io/vedika-sdk-android) (Gradle):

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.vedika-io:vedika-sdk-android:2.0.0")
}
```

The JitPack artifactId is `vedika-sdk-android` (this repo's name), which differs
from the jar filename below (`vedika-android-sdk-*.jar`, from this module's
Gradle project name) — both are correct, just two different naming schemes for
the same build.

The published jar is `vedika-android-sdk-2.0.0.jar`, attached to each release. Build
it yourself with an Android SDK platform 34, 35 or 36 installed (`ANDROID_HOME` or
`ANDROID_SDK_ROOT` set), because `VastuArView` compiles against `android.jar` and the jar
task refuses to produce an artifact without it:

```sh
./gradlew jar
```

It is a plain `kotlin("jvm")` library, not an Android library module, so an Android app
consumes it like any other JVM dependency. `./gradlew test` runs on a bare JDK with no
Android SDK; in that case `VastuArView` is left out of the test build. `VastuArView` is
the one class that touches Android framework types (`WebView`, `SensorManager`,
`Context`).

Requires JDK 17.

## Changes

### 2.0.0 (major: some response accessors changed type)

Breaking for Kotlin callers that read these:

- `VastuService` response models: `cautionDirections` is a list of `VastuFusionChartDataCautionDirectionsItem` (was `List<String>`), `plotShape` is nullable, and `tracedGeometry` is `VastuPlanAuditDataTracedGeometry?` (was `JSONObject?`).
- `VastuJobItem.input` is a `VastuRequest` (was `VastuAssessmentsRequest`). Passing a `VastuAssessmentsRequest` still compiles.

Added since 1.1.1 (merged to the monorepo, not in any earlier release):

- Capture merge: `VastuArCaptureMergeRequest` and its response types, used by `ar/capture-merge` and by `vedika-sdk-android-ar` 1.2.0 and later.
- Vastu portfolio discovery with atomic spending budgets, property collaboration audit and physical erasure, courtyard geometry and unit interchange, remediation and partner report workflows, pinned rule snapshots with signed assessment receipts, lifecycle events and bulk plan-job artifacts (`VastuJobArtifact`), and native device attestation.
- Every delete returns a receipt the customer can keep (VA-16).
- Errors: `VedikaApiError.code`; `VedikaInsufficientBalanceError` exposes `required`, `available`, `deficit` and `purchaseUrl`; `VedikaRateLimitError` exposes `isDailyLimit`, `isMonthlySpendLimit`, `isRetryable` and `upgradeUrl`, and takes its wait from the body's `retryAfter`; new `VedikaIdempotencyNotSupportedError` for a 422 `IDEMPOTENCY_NOT_SUPPORTED`.
- Idempotency: the query route uses `X-Idempotency-Key`, the Vastu routes `Idempotency-Key`; the client attaches an automatic key only on routes that accept one, and never retries a keyless billed POST after a network error or 5xx.
- The Vastu runtime and the bundled AR engine send `Authorization: Bearer`.

## Use

```kotlin
val client = VedikaClient(VedikaConfig(apiKey = "vk_live_..."))
val audit = client.vastu.auditSingleRoom(roomType = "kitchen", zone = "NE")
println(audit.score)
```

## Keys

**Never ship a key inside an app you distribute.** Anything in a shipped APK is
readable by anyone who has it, and calls are billed to that key's account. Route
requests through a server you control and keep the key there.

## Offline AR runtime

`src/main/resources/io/vedika/sdk/vastu-runtime/` carries a generated, hash-pinned
copy of the AR engine, so `VastuArView` needs no network fetch on first load.
`VastuRuntimeBundleTest` verifies every packaged resource against the manifest's
SHA-256 digests through the classpath, and asserts the runtime loads no remote
resource.

## Tests

```sh
./gradlew test
```

The response-parity test needs the monorepo's OpenAPI spec and sandbox corpus and
skips here, saying so. Everything else runs.

## License

MIT — see `LICENSE`.
