# Vedika Android SDK

Native Kotlin client for the [Vedika](https://vedika.io) astrology API, covering the
Vastu surface and the native AR view. OkHttp + Kotlin, with a versioned offline AR
runtime bundled as a resource.

This repository is a mirror. The source of truth is `sdks/android/` in the Vedika
monorepo.

## Install

The published jar is `vedika-android-sdk-1.0.1.jar`, attached to each release. Build
it yourself with:

```sh
gradle jar
```

It is a plain `kotlin("jvm")` library, not an Android library module, so it builds
and unit-tests on a bare JDK with no Android SDK installed — and an Android app
consumes it like any other JVM dependency. `VastuArView` is the one class that
touches Android framework types (`WebView`, `SensorManager`, `Context`); it compiles
against `android.jar` when `ANDROID_HOME` is set and is simply absent otherwise.

Requires JDK 17.

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
gradle test
```

The response-parity test needs the monorepo's OpenAPI spec and sandbox corpus and
skips here, saying so. Everything else runs.

## License

MIT — see `LICENSE`.
