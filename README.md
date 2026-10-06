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
    implementation("com.github.vedika-io:vedika-sdk-android:1.1.1")
}
```

The JitPack artifactId is `vedika-sdk-android` (this repo's name), which differs
from the jar filename below (`vedika-android-sdk-*.jar`, from this module's
Gradle project name) — both are correct, just two different naming schemes for
the same build.

The published jar is `vedika-android-sdk-1.1.1.jar`, attached to each release. Build
it yourself with:

```sh
gradle jar
```

It is a plain `kotlin("jvm")` library, not an Android library module, and an Android
app consumes it like any other JVM dependency. `gradle test` runs on a bare JDK with
no Android SDK. `gradle jar` needs `ANDROID_HOME` pointing at an Android platform,
because `VastuArView` touches Android framework types (`WebView`, `SensorManager`,
`Context`) and compiles against `android.jar`.

Requires JDK 17.

## Use

```kotlin
val client = VedikaClient(apiKey = "vk_live_...")  // server-side only, see Keys
// vastuOperation is a suspend function: call it from a coroutine.
val audit = client.vastu.vastuOperation(
    VastuContracts.auditSingleRoom,
    VastuAuditSingleRoomRequest(roomType = "kitchen", zone = "NE"))
println("${audit.data.compliance} ${audit.data.remedy}")
```

## Errors

All failures throw a `VedikaApiError` subclass. Branch on the type, not the text.

- `VedikaInsufficientCredits` (402): `required`, `available`, `deficit`, `purchaseUrl`. Never retried.
- `VedikaRateLimitError` (429): read `code`. `isDailyLimit` or not `isRetryable` means stop; otherwise wait `retryAfterSeconds`.
- `VedikaAuthError` (401): not retried.
- `VedikaIdempotencyNotSupportedError` (422 `IDEMPOTENCY_NOT_SUPPORTED`): the route rejects keys and nothing was charged.

The client sends an `Idempotency-Key` automatically only on the Vastu routes that
accept one, and never retries a billed POST that has no key on a server error or
network failure. Pass your own `idempotencyKey` on any call you want retried safely.

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
gradle test
```

The response-parity test needs the monorepo's OpenAPI spec and sandbox corpus and
skips here, saying so. Everything else runs.

## License

MIT — see `LICENSE`.
