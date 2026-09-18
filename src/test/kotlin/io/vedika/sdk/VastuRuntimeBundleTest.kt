package io.vedika.sdk

import java.security.MessageDigest
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

/**
 * The bundled offline AR runtime must match its own manifest.
 *
 * `verifyVastuRuntime` in build.gradle.kts checks the bundle against the web engine
 * it is generated from, which only exists inside the monorepo — published as a
 * standalone repository that task skips. This test needs nothing outside the
 * package, so the integrity of what actually ships is verified either way.
 *
 * It reads through the classpath, so it checks the resources as a consumer of the
 * jar sees them, not as files on disk.
 */
class VastuRuntimeBundleTest {
    private val base = "/io/vedika/sdk/vastu-runtime"

    private fun resource(name: String): ByteArray =
        javaClass.getResourceAsStream("$base/$name")?.readBytes()
            ?: fail("missing packaged resource $base/$name") as ByteArray

    @Test fun everyPackagedResourceMatchesTheManifestDigest() {
        val manifest = JSONObject(String(resource("manifest.json"), Charsets.UTF_8))
        assertEquals(1, manifest.getInt("formatVersion"))

        val files = manifest.getJSONObject("files")
        assertEquals(
            "the runtime bundle must carry exactly these files",
            listOf("hud-mandala.png", "index.html", "runtime.js"),
            files.keys().asSequence().sorted().toList()
        )

        for (name in files.keys()) {
            val digest = MessageDigest.getInstance("SHA-256").digest(resource(name))
                .joinToString("") { "%02x".format(it) }
            assertEquals("$name digest", files.getString(name), digest)
        }
    }

    @Test fun theRuntimeIsNotEmptyAndIsSelfContained() {
        val html = String(resource("index.html"), Charsets.UTF_8)
        assertTrue("index.html should be a real document", html.length > 200)
        // An offline runtime must not reach the network on load.
        for (scheme in listOf("http://", "https://")) {
            assertFalse(
                "index.html must not load $scheme resources — the runtime is offline",
                Regex("""(src|href)\s*=\s*["']$scheme""").containsMatchIn(html)
            )
        }
        assertTrue("runtime.js should be substantial", resource("runtime.js").size > 10_000)
    }
}
