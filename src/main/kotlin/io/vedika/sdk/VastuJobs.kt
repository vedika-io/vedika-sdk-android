package io.vedika.sdk

import org.json.JSONObject

/** One item to queue: a unique [id] (1-128 bytes, no surrounding spaces) and its operation [input]. */
data class VastuJobsRequestItem(
    val id: String,
    val input: VastuRequest,
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf("id" to id, "input" to input.toMap())
}

/**
 * Queue 1 to 1,000 assessments, plan analyses or reports (`POST /v2/astrology/vastu/jobs`). The submit
 * needs a caller-retained idempotency key: the same key and body return the
 * same job, a different body under the same key is refused with 409.
 */
data class VastuJobsRequest(
    val items: List<VastuJobsRequestItem>,
    /** An active webhook on this account that receives the job's final event. */
    val webhookId: String? = null,
    val operation: String = "assessments",
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "operation" to operation,
        "items" to items.map { it.toMap() },
        "webhookId" to webhookId,
    ).filterValues { it != null }
}

/** A completed item's portable artifact. Access is scoped to its job owner. */
data class VastuJobArtifact(val raw: JSONObject) {
    val artifactId: String get() = raw.getString("artifactId")
    val jobId: String get() = raw.getString("jobId")
    val itemId: String get() = raw.getString("itemId")
    val contentType: String get() = raw.getString("contentType")
    val filename: String get() = raw.getString("filename")
    val content: String get() = raw.getString("content")
}

/** One finished item. [status] is the HTTP status the single-item call would have returned. */
data class VastuJobResultItem(val raw: JSONObject) {
    val id: String get() = raw.getString("id")
    val index: Int get() = raw.getInt("index")
    val status: Int get() = raw.getInt("status")
    /** Failure code such as INVALID_INPUT or INSUFFICIENT_BALANCE; null on success. */
    val code: String? get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    /** The exact single-item response envelope, including its billing block. */
    val response: JSONObject get() = raw.getJSONObject("response")
    val artifacts: List<VastuJobArtifact> get() {
        val values = raw.optJSONArray("artifacts") ?: return emptyList()
        return List(values.length()) { VastuJobArtifact(values.getJSONObject(it)) }
    }
}

/** The finished items of a results page, typed. */
val VastuJobResultsData.items: List<VastuJobResultItem>
    get() = List(raw.getJSONArray("results").length()) { VastuJobResultItem(raw.getJSONArray("results").getJSONObject(it)) }

data class VastuJobSubmitResponse(val success: Boolean, val data: VastuJobSubmitData, val raw: JSONObject)

data class VastuJobStatusResponse(val success: Boolean, val data: VastuJobStatusData, val raw: JSONObject)

data class VastuJobResultsResponse(val success: Boolean, val data: VastuJobResultsData, val raw: JSONObject)

internal val VASTU_JOB_ID = Regex("^vjob_[0-9a-f]{32}$")

/** `jobs/{id}` and `jobs/{id}/results` are GET; `jobs` and `jobs/{id}/cancel` are POST. */
internal val VASTU_JOB_PATH = Regex("^jobs/([^/]+)(/results|/cancel)?$")

internal fun requireVastuJobId(jobId: String): String {
    require(VASTU_JOB_ID.matches(jobId)) { "jobId must be the vjob_... id returned when the job was submitted" }
    return jobId
}
