package io.vedika.sdk

import java.io.File
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Assume
import org.junit.Test

class VastuResponseFixtureTest {
    @Test fun everyPublicGetterMatchesAllRustResponses() {
        // This parity test reads the canonical OpenAPI spec (2.8 MB) and the shared
        // sandbox corpus (1.4 MB) from the monorepo. Published as a standalone
        // repository those are absent, and duplicating 4.3 MB into the package to
        // keep one test running is not a trade worth making — unlike the Swift
        // package, whose single 1.4 MB fixture IS bundled. So it skips there and
        // stays fully enforced where the canonical data lives, which is the only
        // place the drift it guards can be introduced.
        val root = generateSequence(File(System.getProperty("user.dir"))) { it.parentFile }
            .firstOrNull { File(it, "web/vedika-public/openapi.json").isFile }
        Assume.assumeTrue(
            "skipped: no monorepo checkout above ${System.getProperty("user.dir")}, " +
                "so web/vedika-public/openapi.json is not reachable",
            root != null
        )
        root!!
        val document = JSONObject(File(root, "web/vedika-public/openapi.json").readText())
        val schemas = document.getJSONObject("components").getJSONObject("schemas")
        val corpus = JSONObject(File(root, "web/vedika-public/js/catalog/vastu-sandbox-demos.json").readText())
        val demos = corpus.getJSONObject("demos")
        val fixtures = corpus.getJSONObject("fixtures")
        val seen = mutableSetOf<String>()
        assertEquals(93, demos.length())
        for (key in demos.keys()) {
            val request = fixtures.getJSONObject(key).getJSONObject("request")
            val path = request.getString("path").replace("/sandbox/vastu/", "/v2/astrology/vastu/")
            val operation = document.getJSONObject("paths").getJSONObject(path).getJSONObject(request.getString("method").lowercase())
            val responseName = operation.getJSONObject("responses").getJSONObject("200").getJSONObject("content")
                .getJSONObject("application/json").getJSONObject("schema").getString("\$ref").substringAfterLast('/')
            val name = schemas.getJSONObject(responseName).getJSONObject("properties").getJSONObject("data").getString("\$ref").substringAfterLast('/')
            seen.add(name)
            val raw = demos.getJSONObject(key).getJSONObject("data")
            val decoded = Class.forName("io.vedika.sdk.$name").getConstructor(JSONObject::class.java).newInstance(raw)
            inspect(decoded, raw, schemas.getJSONObject(name), key)
        }
        assertEquals(57, seen.size)
    }

    /// Kotlin does not prefix the getter of a property whose name already begins
    /// with `is`: `val isBrahmasthan: Boolean?` compiles to `isBrahmasthan()`, not
    /// `getIsBrahmasthan()`. Assuming the `get` form made this test throw
    /// `NoSuchMethodException` on the first such field and abort the whole run, so
    /// every response after it went unchecked.
    private fun getterName(type: Class<*>, field: String): String {
        val capitalised = "get" + field.replaceFirstChar { it.uppercaseChar() }
        if (!field.startsWith("is")) return capitalised
        return if (type.methods.any { it.name == field }) field else capitalised
    }

    private fun inspect(value: Any, raw: JSONObject, schema: JSONObject, path: String) {
        val properties = schema.getJSONObject("properties")
        for (field in properties.keys()) {
            val actual = value.javaClass.getMethod(getterName(value.javaClass, field)).invoke(value)
            val expected = raw.opt(field).takeUnless { it == JSONObject.NULL }
            val property = properties.getJSONObject(field)
            if (actual != null && property.optString("type") == "object" && property.has("properties")) {
                inspect(actual, expected as JSONObject, property, "$path.$field")
            } else if (actual != null && property.optString("type") == "array" &&
                property.getJSONObject("items").has("properties")) {
                val rows = actual as List<*>
                val source = expected as JSONArray
                assertEquals("$path.$field", source.length(), rows.size)
                rows.forEachIndexed { index, row ->
                    inspect(row!!, source.getJSONObject(index), property.getJSONObject("items"), "$path.$field[$index]")
                }
            } else {
                assertEquals("$path.$field", normalize(expected), normalize(actual))
            }
        }
    }

    private fun normalize(value: Any?): Any? = when (value) {
        null, JSONObject.NULL -> null
        is JSONObject -> value.keys().asSequence().associateWith { normalize(value.get(it)) }
        is JSONArray -> (0 until value.length()).map { normalize(value.get(it)) }
        is List<*> -> value.map { normalize(it) }
        is Number -> value.toDouble()
        else -> value
    }
}
