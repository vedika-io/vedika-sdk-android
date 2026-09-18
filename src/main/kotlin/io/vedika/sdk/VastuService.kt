package io.vedika.sdk

import org.json.JSONObject

// Android's platform JSONObject lacks the desktop library's optNumber method.
// Keep optional numeric decoding available without bundling a second JSON library.
private fun JSONObject.optionalNumber(name: String): Number? = when (val value = opt(name)) {
    is Number -> value
    is String -> value.toBigDecimalOrNull()
    else -> null
}

enum class VastuOperation(val path: String) {
    ScansTimelapse("scans/timelapse"),
    ScansDelete("scans/delete"),
    ScansList("scans/list"),
    ScansRetrieve("scans/retrieve"),
    ScansSave("scans/save"),
    ArDeityIcons("ar/deity-icons"),
    ArRoomCapture("ar/room-capture"),
    ArYantraMeshes("ar/yantra-meshes"),
    ArZoneTextures("ar/zone-textures"),
    ArAnchorRecommendations("ar/anchor-recommendations"),
    ArHeatmapRaster("ar/heatmap-raster"),
    PlotShape("plot/shape"),
    PlotRatio("plot/ratio"),
    EntrancePada("entrance/pada"),
    DirectionCorrect("direction/correct"),
    DirectionDeclination("direction/declination"),
    DirectionZoneFromBearing("direction/zone-from-bearing"),
    AuditFloorPlan("audit/floor-plan"),
    AuditFloorPlanDetailed("audit/floor-plan-detailed"),
    AuditSingleRoom("audit/single-room"),
    ArScanQuality("ar/scan-quality"),
    MandalaProject9Zone("mandala/project/9-zone"),
    MandalaProject81Pada("mandala/project/81-pada"),
    MandalaProjectBrahmasthan("mandala/project/brahmasthan"),
    ReferenceDirections8("reference/directions/8"),
    ReferenceMandala9Zone("reference/mandala/9-zone"),
    ReferenceMandala45Devatas("reference/mandala/45-devatas"),
    ReferenceDefectsCatalog("reference/defects/catalog"),
    ReferenceRemediesCatalog("reference/remedies/catalog"),
    ReferenceDirections16("reference/directions/16"),
    ReferenceDirections32("reference/directions/32"),
    ReferenceColorsByZone("reference/colors-by-zone"),
    ReferenceMaterialsByZone("reference/materials-by-zone"),
    ReferenceMandala64Pada("reference/mandala/64-pada"),
    ReferenceGateObstructions("reference/gate-obstructions"),
    RoomKitchen("room/kitchen"),
    RoomBedroom("room/bedroom"),
    RoomPooja("room/pooja"),
    RoomToilet("room/toilet"),
    RoomStaircase("room/staircase"),
    RoomStudy("room/study"),
    RoomLiving("room/living"),
    RoomDining("room/dining"),
    RoomStore("room/store"),
    RoomWaterStorage("room/water-storage"),
    PlacementBorewell("placement/borewell"),
    PlacementWell("placement/well"),
    PlacementSepticTank("placement/septic-tank"),
    PlacementOverheadTank("placement/overhead-tank"),
    PlacementTree("placement/tree"),
    PlacementGarden("placement/garden"),
    PlacementBalcony("placement/balcony"),
    PlacementWindow("placement/window"),
    PlacementGeneratorElectrical("placement/generator-electrical"),
    PlacementMainGate("placement/main-gate"),
    PlotExtensionsCuts("plot/extensions-cuts"),
    PlotSlope("plot/slope"),
    PlotOrientation("plot/orientation"),
    PlotRoadOrientation("plot/road-orientation"),
    EntranceRecommend("entrance/recommend"),
    ElementsDistribution("elements/distribution"),
    ElementsBalanceSuggest("elements/balance-suggest"),
    DirectionAuspiciousFacing("direction/auspicious-facing"),
    ScoreOverall("score/overall"),
    ScoreZoneWise("score/zone-wise"),
    ScoreComplianceIndex("score/compliance-index"),
    MultiStoreyFloorRules("multi-storey/floor-rules"),
    CompoundWallAnalysis("compound/wall-analysis"),
    FloorLevelAnalysis("floor/level-analysis"),
    SpecializedResidential("specialized/residential"),
    SpecializedCommercial("specialized/commercial"),
    SpecializedTemple("specialized/temple"),
    SpecializedFactory("specialized/factory"),
    SpecializedHospital("specialized/hospital"),
    SpecializedRestaurant("specialized/restaurant"),
    SpecializedEducational("specialized/educational"),
    TimingBhumiPujan("timing/bhumi-pujan"),
    TimingGrihapravesh("timing/grihapravesh"),
    TimingConstructionStart("timing/construction-start"),
    TimingVastuShanti("timing/vastu-shanti"),
    PlanAnalyze("plan/analyze"),
    PlanUpload("plan/upload"),
    PlanReport("plan/report"),
    PlanGenerate("plan/generate"),
    PlanFromRequirements("plan/from-requirements"),
    PlanOptimize("plan/optimize"),
    FusionChart("fusion/chart"),
    CompareBeforeAfterRemedy("compare/before-after-remedy"),
    EntranceObstructionCheck("entrance/obstruction-check"),
    DirectionSunPath("direction/sun-path"),
    ArTrueNorthCalibrate("ar/true-north-calibrate"),
    Assessments("assessments"),
    AssessmentsBatch("assessments/batch"),
}

data class VastuPoint(val x: Double, val y: Double)
data class VastuRoomInput(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val polygon: List<VastuPoint>? = null,
    val area: Double? = null,
)
data class VastuPlotInput(
    val width: Double? = null,
    val length: Double? = null,
    val facing: String? = null,
    val polygon: List<VastuPoint>? = null,
)

data class VastuOperationRequest(
    val plotPolygon: List<VastuPoint>? = null,
    val bearingDeg: Double? = null,
    val doorXY: VastuPoint? = null,
    val zone: String? = null,
    val rooms: List<VastuRoomInput>? = null,
    val plot: VastuPlotInput? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val datetime: String? = null,
    val date: String? = null,
    val deviceHeadingAtSunDeg: Double? = null,
    val pointCloudDensity: Double? = null,
    val polygonClosure: Boolean? = null,
    val roomsTagged: Boolean? = null,
    val compassConfidence: Double? = null,
    val gpsConfidence: Double? = null,
    val scanDurationSec: Double? = null,
    val scannedAreaM2: Double? = null,
    val variants: Int? = null,
    val obstruction: String? = null,
    val distance: Double? = null,
    val entranceDirection: String? = null,
) {
    internal fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon?.map { listOf(it.x, it.y) },
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY?.let { listOf(it.x, it.y) },
        "zone" to zone,
        "rooms" to rooms?.map { room -> mapOf("name" to room.name, "roomType" to room.roomType, "zone" to room.zone, "polygon" to room.polygon?.map { listOf(it.x, it.y) }, "area" to room.area).filterValues { it != null } },
        "plot" to plot?.let { mapOf("width" to it.width, "length" to it.length, "facing" to it.facing, "polygon" to it.polygon?.map { point -> listOf(point.x, point.y) }).filterValues { value -> value != null } },
        "lat" to lat, "lon" to lon, "latitude" to latitude, "longitude" to longitude,
        "datetime" to datetime, "date" to date, "deviceHeadingAtSunDeg" to deviceHeadingAtSunDeg,
        "pointCloudDensity" to pointCloudDensity, "polygonClosure" to polygonClosure,
        "roomsTagged" to roomsTagged, "compassConfidence" to compassConfidence,
        "gpsConfidence" to gpsConfidence, "scanDurationSec" to scanDurationSec,
        "scannedAreaM2" to scannedAreaM2, "variants" to variants,
        "obstruction" to obstruction, "distance" to distance, "entranceDirection" to entranceDirection,
    ).filterValues { it != null }
}

data class VastuOperationResult(val raw: JSONObject) {
    private val data: JSONObject = raw.optJSONObject("data") ?: JSONObject()
    val success: Boolean? = raw.opt("success") as? Boolean
    val score: Double? = data.optionalNumber("score")?.toDouble()
    val grade: String? = data.optString("grade").takeIf { it.isNotEmpty() }
    val reliable: Boolean? = data.opt("reliable") as? Boolean
    val reason: String? = data.optString("reason").takeIf { it.isNotEmpty() }
    val offsetDeg: Double? = data.optionalNumber("offsetDeg")?.toDouble()
}







// BEGIN GENERATED VASTU CONTRACTS — source: served OpenAPI

interface VastuEncodable { fun toMap(): Map<String, Any?> }

interface VastuRequest : VastuEncodable

object VastuNoRequest : VastuRequest { override fun toMap(): Map<String, Any?> = emptyMap() }

sealed interface VastuJsonObjectOrArray

data class VastuJsonObject(val value: Map<String, Any?>) : VastuJsonObjectOrArray

data class VastuJsonArray(val value: List<Map<String, Any?>>) : VastuJsonObjectOrArray

private fun encodeVastu(value: Any?): Any? = when (value) { is VastuEncodable -> value.toMap(); is VastuJsonObject -> value.value; is VastuJsonArray -> value.value; is List<*> -> value.map(::encodeVastu); else -> value }

private fun vastuStrings(value: org.json.JSONArray) = List(value.length()) { value.getString(it) }

private fun vastuDoubles(value: org.json.JSONArray) = List(value.length()) { value.getDouble(it) }

private fun vastuInts(value: org.json.JSONArray) = List(value.length()) { value.getInt(it) }

private fun vastuObjects(value: org.json.JSONArray) = List(value.length()) { value.getJSONObject(it) }

private fun vastuArrays(value: org.json.JSONArray) = List(value.length()) { value.getJSONArray(it) }

private fun vastuValues(value: org.json.JSONArray) = List(value.length()) { value.get(it) }

data class VastuArHeatmapRasterRequestRoomsItem(
    val roomType: String,
    val zone: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "roomType" to roomType,
        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArHeatmapRasterRequest(
    val rooms: List<VastuArHeatmapRasterRequestRoomsItem>,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArPlanToWorld(
    val units: String,
    val origin: List<Double>,
    val xAxis: List<Double>,
    val yAxis: List<Double>
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "units" to units,
        "origin" to origin,
        "xAxis" to xAxis,
        "yAxis" to yAxis
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArAnchorRecommendationsRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double,
    val planToWorld: VastuArPlanToWorld
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "planToWorld" to planToWorld
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArZoneTexturesRequest(
    val zone: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArYantraMeshesRequest(
    val model: String,
    val format: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "model" to model,
        "format" to format
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArDeityIconsRequest(
    val zone: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCaptureDevice(
    val platform: String,
    val method: String,
    val depth: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "platform" to platform,
        "method" to method,
        "depth" to depth
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCaptureFrameNorth(
    val referenceFrame: String,
    val headingSource: String,
    val declinationProvenance: String,
    val yawSamples: Int,
    val compassConfidence: Double,
    val declinationDeg: Double? = null,
    val yawSpreadDeg: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "referenceFrame" to referenceFrame,
        "headingSource" to headingSource,
        "declinationDeg" to declinationDeg,
        "declinationProvenance" to declinationProvenance,
        "yawSamples" to yawSamples,
        "yawSpreadDeg" to yawSpreadDeg,
        "compassConfidence" to compassConfidence
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCaptureFrame(
    val units: String,
    val axes: String,
    val north: VastuRoomCaptureFrameNorth,
    val planToWorld: VastuArPlanToWorld? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "units" to units,
        "axes" to axes,
        "north" to north,
        "planToWorld" to planToWorld
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCaptureOutline(
    val polygon: List<List<Double>>,
    val source: String = "traced"
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "polygon" to polygon,
        "source" to source
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCaptureRoomsItemOpeningsItem(
    val kind: String,
    val centerXY: List<Double>,
    val widthM: Double,
    val confidence: String,
    val heightM: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "kind" to kind,
        "centerXY" to centerXY,
        "widthM" to widthM,
        "heightM" to heightM,
        "confidence" to confidence
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCaptureRoomsItem(
    val id: String,
    val label: String?,
    val labelSource: String,
    val polygon: List<List<Double>>,
    val areaM2: Double,
    val floorIndex: Int,
    val heightM: Double? = null,
    val openings: List<VastuRoomCaptureRoomsItemOpeningsItem>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "label" to label,
        "labelSource" to labelSource,
        "polygon" to polygon,
        "areaM2" to areaM2,
        "heightM" to heightM,
        "floorIndex" to floorIndex,
        "openings" to openings
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCaptureQuality(
    val polygonClosure: Boolean,
    val pointCloudDensityBasis: String,
    val roomCount: Int,
    val roomsTagged: Int,
    val closureGapM: Double? = null,
    val pointCloudDensity: Double? = null,
    val coveragePercent: Double? = null,
    val scanDurationSec: Double? = null,
    val scannedAreaM2: Double? = null,
    val expectedRoomCount: Int? = null,
    val gpsConfidence: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "polygonClosure" to polygonClosure,
        "closureGapM" to closureGapM,
        "pointCloudDensity" to pointCloudDensity,
        "pointCloudDensityBasis" to pointCloudDensityBasis,
        "coveragePercent" to coveragePercent,
        "scanDurationSec" to scanDurationSec,
        "scannedAreaM2" to scannedAreaM2,
        "roomCount" to roomCount,
        "expectedRoomCount" to expectedRoomCount,
        "roomsTagged" to roomsTagged,
        "gpsConfidence" to gpsConfidence
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomCapture(
    val schema: String,
    val captureId: String,
    val capturedAtEpoch: Long,
    val device: VastuRoomCaptureDevice,
    val frame: VastuRoomCaptureFrame,
    val outline: VastuRoomCaptureOutline,
    val rooms: List<VastuRoomCaptureRoomsItem>,
    val quality: VastuRoomCaptureQuality,
    val attestation: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "schema" to schema,
        "captureId" to captureId,
        "capturedAtEpoch" to capturedAtEpoch,
        "device" to device,
        "frame" to frame,
        "outline" to outline,
        "rooms" to rooms,
        "quality" to quality,
        "attestation" to attestation
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArRoomCaptureRequest(
    val capture: VastuRoomCapture,
    val zoneResolution: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "capture" to capture,
        "zoneResolution" to zoneResolution
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScanSnapshotRoomsItem(
    val roomType: String,
    val zone: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "roomType" to roomType,
        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScanTelemetry(
    val pointCloudDensity: Double,
    val polygonClosure: Boolean,
    val compassConfidence: Double,
    val gpsConfidence: Double,
    val roomsTagged: Int,
    val scanDurationSec: Double,
    val scannedAreaM2: Double,
    val roomCount: Int? = null,
    val expectedRoomCount: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "pointCloudDensity" to pointCloudDensity,
        "polygonClosure" to polygonClosure,
        "compassConfidence" to compassConfidence,
        "gpsConfidence" to gpsConfidence,
        "roomsTagged" to roomsTagged,
        "scanDurationSec" to scanDurationSec,
        "scannedAreaM2" to scannedAreaM2,
        "roomCount" to roomCount,
        "expectedRoomCount" to expectedRoomCount
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScanSnapshot(
    val inputSource: String,
    val rooms: List<VastuScanSnapshotRoomsItem>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val telemetry: VastuScanTelemetry? = null,
    val capture: VastuRoomCapture? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "inputSource" to inputSource,
        "rooms" to rooms,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "telemetry" to telemetry,
        "capture" to capture
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansSaveRequest(
    val scanId: String,
    val propertyId: String,
    val title: String,
    val retentionDays: Int,
    val snapshot: VastuScanSnapshot
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "scanId" to scanId,
        "propertyId" to propertyId,
        "title" to title,
        "retentionDays" to retentionDays,
        "snapshot" to snapshot
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansRetrieveRequest(
    val requestId: String,
    val scanId: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "requestId" to requestId,
        "scanId" to scanId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansListRequest(
    val requestId: String,
    val limit: Int,
    val cursor: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "requestId" to requestId,
        "limit" to limit,
        "cursor" to cursor
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansDeleteRequest(
    val scanId: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "scanId" to scanId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansTimelapseRequest(
    val requestId: String,
    val scanIds: List<String>
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "requestId" to requestId,
        "scanIds" to scanIds
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}


data class VastuArScanQualityRequest(
    val pointCloudDensity: Double? = null,
    val polygonClosure: Boolean? = null,
    val roomsTagged: Boolean? = null,
    val compassConfidence: Double? = null,
    val gpsConfidence: Double? = null,
    val scanDurationSec: Double? = null,
    val scannedAreaM2: Double? = null,
    val roomCount: Long? = null,
    val expectedRoomCount: Long? = null,
    val pointCloudDensityPerM2: Double? = null,
    val polygonClosed: Boolean? = null,
    val coveragePercent: Double? = null,
    val pointCloudDensityBasis: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "pointCloudDensity" to pointCloudDensity,
        "polygonClosure" to polygonClosure,
        "roomsTagged" to roomsTagged,
        "compassConfidence" to compassConfidence,
        "gpsConfidence" to gpsConfidence,
        "scanDurationSec" to scanDurationSec,
        "scannedAreaM2" to scannedAreaM2,
        "roomCount" to roomCount,
        "expectedRoomCount" to expectedRoomCount,
        "pointCloudDensityPerM2" to pointCloudDensityPerM2,
        "polygonClosed" to polygonClosed,
        "coveragePercent" to coveragePercent,
        "pointCloudDensityBasis" to pointCloudDensityBasis
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArCountedScanQualityRequest(
    val pointCloudDensity: Double? = null,
    val polygonClosure: Boolean? = null,
    val roomsTagged: Long? = null,
    val compassConfidence: Double? = null,
    val gpsConfidence: Double? = null,
    val scanDurationSec: Double? = null,
    val scannedAreaM2: Double? = null,
    val roomCount: Long? = null,
    val expectedRoomCount: Long? = null,
    val pointCloudDensityPerM2: Double? = null,
    val polygonClosed: Boolean? = null,
    val coveragePercent: Double? = null,
    val pointCloudDensityBasis: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "pointCloudDensity" to pointCloudDensity,
        "polygonClosure" to polygonClosure,
        "roomsTagged" to roomsTagged,
        "compassConfidence" to compassConfidence,
        "gpsConfidence" to gpsConfidence,
        "scanDurationSec" to scanDurationSec,
        "scannedAreaM2" to scannedAreaM2,
        "roomCount" to roomCount,
        "expectedRoomCount" to expectedRoomCount,
        "pointCloudDensityPerM2" to pointCloudDensityPerM2,
        "polygonClosed" to polygonClosed,
        "coveragePercent" to coveragePercent,
        "pointCloudDensityBasis" to pointCloudDensityBasis
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArTrueNorthCalibrateRequest(
    val lat: Double,
    val lon: Double,
    val datetime: String,
    val deviceHeadingAtSunDeg: Double,
    val deviceHeadingAccuracyDeg: Double? = null,
    val headingSampleAgeMs: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "lat" to lat,
        "lon" to lon,
        "datetime" to datetime,
        "deviceHeadingAtSunDeg" to deviceHeadingAtSunDeg,
        "deviceHeadingAccuracyDeg" to deviceHeadingAccuracyDeg,
        "headingSampleAgeMs" to headingSampleAgeMs
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAssessmentsRequestRoomsItem(
    val roomType: String,
    val zone: String,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAssessmentsRequest(
    val inputSource: String,
    val rooms: List<VastuAssessmentsRequestRoomsItem>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val doorXY: VastuPoint? = null,
    val bearingDeg: Double? = null,
    val confidence: Double? = null,
    val pointCloudDensity: Double? = null,
    val polygonClosure: Boolean? = null,
    val roomsTagged: Boolean? = null,
    val compassConfidence: Double? = null,
    val gpsConfidence: Double? = null,
    val scanDurationSec: Double? = null,
    val scannedAreaM2: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "inputSource" to inputSource,
        "rooms" to rooms,
        "plotPolygon" to plotPolygon,
        "doorXY" to doorXY?.let { listOf(it.x, it.y) },
        "bearingDeg" to bearingDeg,
        "confidence" to confidence,
        "pointCloudDensity" to pointCloudDensity,
        "polygonClosure" to polygonClosure,
        "roomsTagged" to roomsTagged,
        "compassConfidence" to compassConfidence,
        "gpsConfidence" to gpsConfidence,
        "scanDurationSec" to scanDurationSec,
        "scannedAreaM2" to scannedAreaM2
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAssessmentsBatchRequestItemsItem(
    val id: String,
    val assessment: VastuAssessmentsRequest,
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf("id" to id, "assessment" to assessment.toMap())
}

/** One to twenty items with unique IDs. Retain the caller key for retries. */
data class VastuAssessmentsBatchRequest(
    val items: List<VastuAssessmentsBatchRequestItemsItem>,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf("items" to items.map { it.toMap() })
}


data class VastuAuditFloorPlanDetailedRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAuditFloorPlanDetailedRequest(
    val rooms: List<VastuAuditFloorPlanDetailedRequestRoomsItem>,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAuditFloorPlanRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAuditFloorPlanRequest(
    val rooms: List<VastuAuditFloorPlanRequestRoomsItem>? = null,
    val text: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "text" to text
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAuditSingleRoomRequest(
    val roomType: String,
    val zone: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "roomType" to roomType,
        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuCompareBeforeAfterRemedyRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuCompareBeforeAfterRemedyRequestRemediesItem(
    val room: String? = null,
    val name: String? = null,
    val roomType: String? = null,
    val toZone: String,
    val zone: String? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "room" to room,
        "name" to name,
        "roomType" to roomType,
        "toZone" to toZone,
        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuCompareBeforeAfterRemedyRequest(
    val rooms: List<VastuCompareBeforeAfterRemedyRequestRoomsItem>? = null,
    val text: String? = null,
    val remedies: List<VastuCompareBeforeAfterRemedyRequestRemediesItem>
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "text" to text,
        "remedies" to remedies
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuCompoundWallAnalysisRequest(
    val walls: VastuJsonObjectOrArray? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "walls" to walls
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionAuspiciousFacingRequest(
    val purpose: String,
    val occupant: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "purpose" to purpose,
        "occupant" to occupant
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionCorrectRequest(
    val direction: String,
    val lat: Double,
    val lon: Double,
    val date: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "direction" to direction,
        "lat" to lat,
        "lon" to lon,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionDeclinationRequest(
    val lat: Double,
    val lon: Double,
    val date: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "lat" to lat,
        "lon" to lon,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionSunPathRequest(
    val lat: Double,
    val lon: Double,
    val date: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "lat" to lat,
        "lon" to lon,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionZoneFromBearingRequest(
    val bearingDeg: Double
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuElementsBalanceSuggestRequest(
    val distribution: JSONObject? = null,
    val deficient: List<String>? = null,
    val excess: List<String>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "distribution" to distribution,
        "deficient" to deficient,
        "excess" to excess
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuElementsDistributionRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuElementsDistributionRequest(
    val rooms: List<VastuElementsDistributionRequestRoomsItem>
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuEntranceObstructionCheckRequest(
    val feature: String,
    val houseHeightMeters: Double? = null,
    val distanceMeters: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "feature" to feature,
        "houseHeightMeters" to houseHeightMeters,
        "distanceMeters" to distanceMeters
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuEntrancePadaRequest(
    val plotPolygon: List<List<Double>>,
    val doorXY: List<Double>,
    val bearingDeg: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "doorXY" to doorXY,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuEntranceRecommendRequest(
    val facing: String
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "facing" to facing
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuFloorLevelAnalysisRequest(
    val levels: VastuJsonObjectOrArray? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "levels" to levels
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuFusionChartRequest(
    val datetime: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val facing: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "datetime" to datetime,
        "latitude" to latitude,
        "longitude" to longitude,
        "timezone" to timezone,
        "facing" to facing
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMandalaProject81PadaRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMandalaProject9ZoneRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMandalaProjectBrahmasthanRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMultiStoreyFloorRulesRequest(
    val floors: Int
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "floors" to floors
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementBalconyRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementBorewellRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementGardenRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementGeneratorElectricalRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementMainGateRequest(
    val facing: String,
    val direction: String? = null,
    val zone: String? = null,
    val pada: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "facing" to facing,
        "direction" to direction,
        "zone" to zone,
        "pada" to pada
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementOverheadTankRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementSepticTankRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementTreeRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementWellRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlacementWindowRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanAnalyzeRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanAnalyzeRequest(
    val rooms: List<VastuPlanAnalyzeRequestRoomsItem>,
    val plot: JSONObject? = null,
    val zoneResolution: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plot" to plot,
        "zoneResolution" to zoneResolution
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanFromRequirementsRequestPlotSetbacks(
    val front: Double? = null,
    val rear: Double? = null,
    val left: Double? = null,
    val right: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "front" to front,
        "rear" to rear,
        "left" to left,
        "right" to right
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanFromRequirementsRequestPlot(
    val width: Double? = null,
    val length: Double? = null,
    val facing: String? = null,
    val polygon: List<List<Double>>? = null,
    val setbacks: VastuPlanFromRequirementsRequestPlotSetbacks? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "width" to width,
        "length" to length,
        "facing" to facing,
        "polygon" to polygon,
        "setbacks" to setbacks
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanFromRequirementsRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanFromRequirementsRequest(
    val plot: VastuPlanFromRequirementsRequestPlot,
    val entrance: JSONObject? = null,
    val rooms: List<VastuPlanFromRequirementsRequestRoomsItem>? = null,
    val requirements: JSONObject? = null,
    val parking: JSONObject? = null,
    val staircase: JSONObject? = null,
    val lift: JSONObject? = null,
    val variants: Int? = null,
    val variantSvg: Boolean? = null,
    val includeSvg: Boolean? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plot" to plot,
        "entrance" to entrance,
        "rooms" to rooms,
        "requirements" to requirements,
        "parking" to parking,
        "staircase" to staircase,
        "lift" to lift,
        "variants" to variants,
        "variantSvg" to variantSvg,
        "includeSvg" to includeSvg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanGenerateRequestPlotSetbacks(
    val front: Double? = null,
    val rear: Double? = null,
    val left: Double? = null,
    val right: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "front" to front,
        "rear" to rear,
        "left" to left,
        "right" to right
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanGenerateRequestPlot(
    val width: Double? = null,
    val length: Double? = null,
    val facing: String? = null,
    val polygon: List<List<Double>>? = null,
    val setbacks: VastuPlanGenerateRequestPlotSetbacks? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "width" to width,
        "length" to length,
        "facing" to facing,
        "polygon" to polygon,
        "setbacks" to setbacks
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanGenerateRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanGenerateRequest(
    val plot: VastuPlanGenerateRequestPlot,
    val entrance: JSONObject? = null,
    val rooms: List<VastuPlanGenerateRequestRoomsItem>? = null,
    val requirements: JSONObject? = null,
    val parking: JSONObject? = null,
    val staircase: JSONObject? = null,
    val lift: JSONObject? = null,
    val variants: Int? = null,
    val variantSvg: Boolean? = null,
    val includeSvg: Boolean? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plot" to plot,
        "entrance" to entrance,
        "rooms" to rooms,
        "requirements" to requirements,
        "parking" to parking,
        "staircase" to staircase,
        "lift" to lift,
        "variants" to variants,
        "variantSvg" to variantSvg,
        "includeSvg" to includeSvg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanOptimizeRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanOptimizeRequest(
    val rooms: List<VastuPlanOptimizeRequestRoomsItem>,
    val plot: JSONObject? = null,
    val includeSvg: Boolean? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plot" to plot,
        "includeSvg" to includeSvg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanReportRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanReportRequestBrand(
    val reportTitle: String? = null,
    val generatedFor: String? = null,
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "reportTitle" to reportTitle, "generatedFor" to generatedFor,
    ).filterValues { it != null }
}

data class VastuPlanReportRequest(
    val rooms: List<VastuPlanReportRequestRoomsItem>,
    val plot: JSONObject? = null,
    val format: String? = null,
    val brand: VastuPlanReportRequestBrand? = null,
    val reportTitle: String? = null,
    val generatedFor: String? = null,
    val tenantName: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plot" to plot,
        "format" to format,
        "brand" to brand,
        "reportTitle" to reportTitle,
        "generatedFor" to generatedFor,
        "tenantName" to tenantName
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanUploadRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanUploadRequest(
    val rooms: List<VastuPlanUploadRequestRoomsItem>? = null,
    val layout: JSONObject? = null,
    val asciiGrid: String? = null,
    val plot: JSONObject? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "layout" to layout,
        "asciiGrid" to asciiGrid,
        "plot" to plot
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotExtensionsCutsRequest(
    val plotPolygon: List<List<Double>>? = null,
    val length: Double? = null,
    val width: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "length" to length,
        "width" to width
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotOrientationRequest(
    val facingBearingDeg: Double? = null,
    val bearingDeg: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "facingBearingDeg" to facingBearingDeg,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotRatioRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotRoadOrientationRequest(
    val roads: List<String>? = null,
    val roadSides: List<String>? = null,
    val veedhiShoola: String? = null,
    val tPointFrom: String? = null,
    val roadThrustFrom: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "roads" to roads,
        "roadSides" to roadSides,
        "veedhiShoola" to veedhiShoola,
        "tPointFrom" to tPointFrom,
        "roadThrustFrom" to roadThrustFrom
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotShapeRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotSlopeRequest(
    val slopeDirection: String? = null,
    val lowSide: String? = null,
    val lowCorner: String? = null,
    val slopeBearingDeg: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "slopeDirection" to slopeDirection,
        "lowSide" to lowSide,
        "lowCorner" to lowCorner,
        "slopeBearingDeg" to slopeBearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomBedroomRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomDiningRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomKitchenRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomLivingRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomPoojaRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomStaircaseRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomStoreRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomStudyRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomToiletRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomWaterStorageRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScoreComplianceIndexRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScoreComplianceIndexRequest(
    val rooms: List<VastuScoreComplianceIndexRequestRoomsItem>,
    val plot: JSONObject? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plot" to plot
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScoreOverallRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScoreOverallRequest(
    val rooms: List<VastuScoreOverallRequestRoomsItem>,
    val plot: JSONObject? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plot" to plot
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScoreZoneWiseRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScoreZoneWiseRequest(
    val rooms: List<VastuScoreZoneWiseRequestRoomsItem>,
    val plot: JSONObject? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plot" to plot
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedCommercialRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedCommercialRequest(
    val rooms: List<VastuSpecializedCommercialRequestRoomsItem>,
    val facing: String? = null,
    val buildingFacing: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "facing" to facing,
        "buildingFacing" to buildingFacing,
        "lat" to lat,
        "lon" to lon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedEducationalRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedEducationalRequest(
    val rooms: List<VastuSpecializedEducationalRequestRoomsItem>,
    val facing: String? = null,
    val buildingFacing: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "facing" to facing,
        "buildingFacing" to buildingFacing,
        "lat" to lat,
        "lon" to lon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedFactoryRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedFactoryRequest(
    val rooms: List<VastuSpecializedFactoryRequestRoomsItem>,
    val facing: String? = null,
    val buildingFacing: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "facing" to facing,
        "buildingFacing" to buildingFacing,
        "lat" to lat,
        "lon" to lon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedHospitalRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedHospitalRequest(
    val rooms: List<VastuSpecializedHospitalRequestRoomsItem>,
    val facing: String? = null,
    val buildingFacing: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "facing" to facing,
        "buildingFacing" to buildingFacing,
        "lat" to lat,
        "lon" to lon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedResidentialRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedResidentialRequest(
    val rooms: List<VastuSpecializedResidentialRequestRoomsItem>,
    val facing: String? = null,
    val buildingFacing: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "facing" to facing,
        "buildingFacing" to buildingFacing,
        "lat" to lat,
        "lon" to lon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedRestaurantRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedRestaurantRequest(
    val rooms: List<VastuSpecializedRestaurantRequestRoomsItem>,
    val facing: String? = null,
    val buildingFacing: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "facing" to facing,
        "buildingFacing" to buildingFacing,
        "lat" to lat,
        "lon" to lon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedTempleRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to polygon,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuSpecializedTempleRequest(
    val rooms: List<VastuSpecializedTempleRequestRoomsItem>,
    val facing: String? = null,
    val buildingFacing: String? = null,
    val lat: Double? = null,
    val lon: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "facing" to facing,
        "buildingFacing" to buildingFacing,
        "lat" to lat,
        "lon" to lon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuTimingBhumiPujanRequest(
    val latitude: Double,
    val longitude: Double,
    val datetime: String? = null,
    val date: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val windowDays: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "latitude" to latitude,
        "longitude" to longitude,
        "datetime" to datetime,
        "date" to date,
        "time" to time,
        "timezone" to timezone,
        "windowDays" to windowDays
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuTimingConstructionStartRequest(
    val latitude: Double,
    val longitude: Double,
    val datetime: String? = null,
    val date: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val windowDays: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "latitude" to latitude,
        "longitude" to longitude,
        "datetime" to datetime,
        "date" to date,
        "time" to time,
        "timezone" to timezone,
        "windowDays" to windowDays
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuTimingGrihapraveshRequest(
    val latitude: Double,
    val longitude: Double,
    val datetime: String? = null,
    val date: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val windowDays: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "latitude" to latitude,
        "longitude" to longitude,
        "datetime" to datetime,
        "date" to date,
        "time" to time,
        "timezone" to timezone,
        "windowDays" to windowDays
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuTimingVastuShantiRequest(
    val latitude: Double,
    val longitude: Double,
    val datetime: String? = null,
    val date: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val windowDays: Int? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "latitude" to latitude,
        "longitude" to longitude,
        "datetime" to datetime,
        "date" to date,
        "time" to time,
        "timezone" to timezone,
        "windowDays" to windowDays
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

interface VastuData { val raw: JSONObject }

// BEGIN GENERATED VASTU RESPONSE DATA
data class VastuArAnchorRecommendationsDataAnchorsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val zone: String
        get() = raw.getString("zone")
    val deity: String
        get() = raw.getString("deity")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val planPosition: List<Double>
        get() = List(raw.getJSONArray("planPosition").length()) { index0 -> (raw.getJSONArray("planPosition").get(index0) as Number).toDouble() }
    val worldPosition: List<Double>
        get() = List(raw.getJSONArray("worldPosition").length()) { index0 -> (raw.getJSONArray("worldPosition").get(index0) as Number).toDouble() }
    val normal: List<Double>
        get() = List(raw.getJSONArray("normal").length()) { index0 -> (raw.getJSONArray("normal").get(index0) as Number).toDouble() }
    val insidePlot: Boolean
        get() = raw.getBoolean("insidePlot")
}

data class VastuArDeityIconsDataIconsItem(val raw: JSONObject) {
    val zone: String
        get() = raw.getString("zone")
    val deity: String
        get() = raw.getString("deity")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val kind: String
        get() = raw.getString("kind")
    val png: Any?
        get() = raw.get("png").takeUnless { it == JSONObject.NULL }
    val svg: Any?
        get() = raw.get("svg").takeUnless { it == JSONObject.NULL }
    val width: Int
        get() = raw.getInt("width")
    val height: Int
        get() = raw.getInt("height")
}

data class VastuArHeatmapRasterDataZonesItem(val raw: JSONObject) {
    val zone: String
        get() = raw.getString("zone")
    val deity: String
        get() = raw.getString("deity")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val disturbed: Boolean
        get() = raw.getBoolean("disturbed")
    val observed: Boolean
        get() = raw.getBoolean("observed")
    val roomCount: Int
        get() = raw.getInt("roomCount")
}

data class VastuArHeatmapRasterDataCompleteness(val raw: JSONObject) {
    val status: String
        get() = raw.getString("status")
    val computedComponents: List<String>
        get() = List(raw.getJSONArray("computedComponents").length()) { index0 -> (raw.getJSONArray("computedComponents").get(index0) as String) }
    val missingInputs: List<String>
        get() = List(raw.getJSONArray("missingInputs").length()) { index0 -> (raw.getJSONArray("missingInputs").get(index0) as String) }
    val projectedCellCount: Int
        get() = raw.getInt("projectedCellCount")
    val physicalCoverageVerified: Boolean
        get() = raw.getBoolean("physicalCoverageVerified")
    val note: String
        get() = raw.getString("note")
}

data class VastuArHeatmapRasterDataLegendDisturbed(val raw: JSONObject) {
    val color: String
        get() = raw.getString("color")
    val meaning: String
        get() = raw.getString("meaning")
}

data class VastuArHeatmapRasterDataLegendNeutral(val raw: JSONObject) {
    val color: String
        get() = raw.getString("color")
    val meaning: String
        get() = raw.getString("meaning")
}

data class VastuArHeatmapRasterDataLegend(val raw: JSONObject) {
    val disturbed: VastuArHeatmapRasterDataLegendDisturbed
        get() = VastuArHeatmapRasterDataLegendDisturbed(raw.getJSONObject("disturbed"))
    val neutral: VastuArHeatmapRasterDataLegendNeutral
        get() = VastuArHeatmapRasterDataLegendNeutral(raw.getJSONObject("neutral"))
    val observed: String
        get() = raw.getString("observed")
}

data class VastuArRoomCaptureDataCaptureOutline(val raw: JSONObject) {
    val polygon: List<List<Double>>
        get() = List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val source: String
        get() = raw.getString("source")
    val width: Double
        get() = raw.getDouble("width")
    val length: Double
        get() = raw.getDouble("length")
    val areaM2: Double
        get() = raw.getDouble("areaM2")
}

data class VastuArRoomCaptureDataCapture(val raw: JSONObject) {
    val captureId: String
        get() = raw.getString("captureId")
    val capturedAtEpoch: Long
        get() = raw.getLong("capturedAtEpoch")
    val device: JSONObject
        get() = raw.getJSONObject("device")
    val north: JSONObject
        get() = raw.getJSONObject("north")
    val floorIndex: Int
        get() = raw.getInt("floorIndex")
    val outline: VastuArRoomCaptureDataCaptureOutline
        get() = VastuArRoomCaptureDataCaptureOutline(raw.getJSONObject("outline"))
    val originShiftM: List<Double>
        get() = List(raw.getJSONArray("originShiftM").length()) { index0 -> (raw.getJSONArray("originShiftM").get(index0) as Number).toDouble() }
    val roomCount: Int
        get() = raw.getInt("roomCount")
    val openingCount: Int
        get() = raw.getInt("openingCount")
}

data class VastuArRoomCaptureDataRoomsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val label: String?
        get() = if (!raw.has("label") || raw.isNull("label")) null else raw.getString("label")
    val roomType: String?
        get() = if (!raw.has("roomType") || raw.isNull("roomType")) null else raw.getString("roomType")
    val zone: String
        get() = raw.getString("zone")
    val zoneBasis: String
        get() = raw.getString("zoneBasis")
    val areaM2: Double
        get() = raw.getDouble("areaM2")
    val centroid: List<Double>
        get() = List(raw.getJSONArray("centroid").length()) { index0 -> (raw.getJSONArray("centroid").get(index0) as Number).toDouble() }
    val heightM: Double?
        get() = if (!raw.has("heightM") || raw.isNull("heightM")) null else raw.getDouble("heightM")
    val openingCount: Int
        get() = raw.getInt("openingCount")
    val polygon: List<List<Double>>
        get() = List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
}

data class VastuArRoomCaptureDataDerivedRequests(val raw: JSONObject) {
    val planAnalyze: Any?
        get() = raw.get("planAnalyze").takeUnless { it == JSONObject.NULL }
    val auditFloorPlanDetailed: Any?
        get() = raw.get("auditFloorPlanDetailed").takeUnless { it == JSONObject.NULL }
    val scanQuality: Any?
        get() = raw.get("scanQuality").takeUnless { it == JSONObject.NULL }
    val anchorRecommendations: Any?
        get() = if (!raw.has("anchorRecommendations") || raw.isNull("anchorRecommendations")) null else raw.get("anchorRecommendations").takeUnless { it == JSONObject.NULL }
}

data class VastuArRoomCaptureDataCompleteness(val raw: JSONObject) {
    val acceptForAudit: Boolean
        get() = raw.getBoolean("acceptForAudit")
    val labelledRooms: Int
        get() = raw.getInt("labelledRooms")
    val unlabelledRooms: List<String>
        get() = List(raw.getJSONArray("unlabelledRooms").length()) { index0 -> (raw.getJSONArray("unlabelledRooms").get(index0) as String) }
    val missing: List<String>
        get() = List(raw.getJSONArray("missing").length()) { index0 -> (raw.getJSONArray("missing").get(index0) as String) }
    val warnings: List<String>
        get() = List(raw.getJSONArray("warnings").length()) { index0 -> (raw.getJSONArray("warnings").get(index0) as String) }
}

data class VastuArScanQualityDataDimensionsPointCloudDensity(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val reason: String
        get() = raw.getString("reason")
    val status: String
        get() = raw.getString("status")
    val basis: String
        get() = raw.getString("basis")
}

data class VastuArScanQualityDataDimensionsPolygonClosure(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val reason: String
        get() = raw.getString("reason")
    val status: String
        get() = raw.getString("status")
}

data class VastuArScanQualityDataDimensionsCompassConfidence(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val reason: String
        get() = raw.getString("reason")
    val status: String
        get() = raw.getString("status")
}

data class VastuArScanQualityDataDimensionsGpsConfidence(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val reason: String
        get() = raw.getString("reason")
    val status: String
        get() = raw.getString("status")
}

data class VastuArScanQualityDataDimensionsRoomsTagged(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val reason: String
        get() = raw.getString("reason")
    val status: String
        get() = raw.getString("status")
}

data class VastuArScanQualityDataDimensionsCoverage(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val reason: String
        get() = raw.getString("reason")
    val status: String
        get() = raw.getString("status")
    val basis: String
        get() = raw.getString("basis")
}

data class VastuArScanQualityDataDimensions(val raw: JSONObject) {
    val pointCloudDensity: VastuArScanQualityDataDimensionsPointCloudDensity
        get() = VastuArScanQualityDataDimensionsPointCloudDensity(raw.getJSONObject("pointCloudDensity"))
    val polygonClosure: VastuArScanQualityDataDimensionsPolygonClosure
        get() = VastuArScanQualityDataDimensionsPolygonClosure(raw.getJSONObject("polygonClosure"))
    val compassConfidence: VastuArScanQualityDataDimensionsCompassConfidence
        get() = VastuArScanQualityDataDimensionsCompassConfidence(raw.getJSONObject("compassConfidence"))
    val gpsConfidence: VastuArScanQualityDataDimensionsGpsConfidence
        get() = VastuArScanQualityDataDimensionsGpsConfidence(raw.getJSONObject("gpsConfidence"))
    val roomsTagged: VastuArScanQualityDataDimensionsRoomsTagged
        get() = VastuArScanQualityDataDimensionsRoomsTagged(raw.getJSONObject("roomsTagged"))
    val coverage: VastuArScanQualityDataDimensionsCoverage
        get() = VastuArScanQualityDataDimensionsCoverage(raw.getJSONObject("coverage"))
}

data class VastuArScanQualityDataRoomCoverage(val raw: JSONObject) {
    val expectedRoomCount: Long?
        get() = if (!raw.has("expectedRoomCount") || raw.isNull("expectedRoomCount")) null else raw.getLong("expectedRoomCount")
    val percent: Double?
        get() = if (!raw.has("percent") || raw.isNull("percent")) null else raw.getDouble("percent")
    val status: String
        get() = raw.getString("status")
    val scope: String
        get() = raw.getString("scope")
}

data class VastuArTrueNorthDataInput(val raw: JSONObject) {
    val lat: Double
        get() = raw.getDouble("lat")
    val lon: Double
        get() = raw.getDouble("lon")
    val datetime: String
        get() = raw.getString("datetime")
    val deviceHeadingAtSunDeg: Double
        get() = raw.getDouble("deviceHeadingAtSunDeg")
}

data class VastuArTrueNorthDataHeadingQuality(val raw: JSONObject) {
    val accuracyDeg: Double?
        get() = if (!raw.has("accuracyDeg") || raw.isNull("accuracyDeg")) null else raw.getDouble("accuracyDeg")
    val sampleAgeMs: Double?
        get() = if (!raw.has("sampleAgeMs") || raw.isNull("sampleAgeMs")) null else raw.getDouble("sampleAgeMs")
    val maxAccuracyDeg: Double
        get() = raw.getDouble("maxAccuracyDeg")
    val maxSampleAgeMs: Double
        get() = raw.getDouble("maxSampleAgeMs")
    val reliable: Boolean
        get() = raw.getBoolean("reliable")
    val basis: String
        get() = raw.getString("basis")
}

data class VastuArYantraMeshesDataGeometry(val raw: JSONObject) {
    val vertices: Int
        get() = raw.getInt("vertices")
    val triangles: Int
        get() = raw.getInt("triangles")
    val upAxis: String
        get() = raw.getString("upAxis")
    val northAxis: String
        get() = raw.getString("northAxis")
    val eastAxis: String
        get() = raw.getString("eastAxis")
    val units: String
        get() = raw.getString("units")
}

data class VastuArZoneTexturesDataCellsItem(val raw: JSONObject) {
    val contentInsetPixels: Int
        get() = raw.getInt("contentInsetPixels")
    val devata: String
        get() = raw.getString("devata")
    val gltfUvBoundsTopLeft: List<Double>
        get() = List(raw.getJSONArray("gltfUvBoundsTopLeft").length()) { index0 -> (raw.getJSONArray("gltfUvBoundsTopLeft").get(index0) as Number).toDouble() }
    val maskBit: Int
        get() = raw.getInt("maskBit")
    val pixelBoundsExclusive: List<Int>
        get() = List(raw.getJSONArray("pixelBoundsExclusive").length()) { index0 -> (raw.getJSONArray("pixelBoundsExclusive").get(index0) as Number).toInt() }
    val usdUvBoundsBottomLeft: List<Double>
        get() = List(raw.getJSONArray("usdUvBoundsBottomLeft").length()) { index0 -> (raw.getJSONArray("usdUvBoundsBottomLeft").get(index0) as Number).toDouble() }
    val zone: String
        get() = raw.getString("zone")
}

data class VastuAssessmentBatchDataResultsItemResponseDataBadgeEligibility(val raw: JSONObject) {
    val inputSource: String
        get() = raw.getString("inputSource")
    val badge: String?
        get() = if (!raw.has("badge") || raw.isNull("badge")) null else raw.getString("badge")
    val eligible: Boolean
        get() = raw.getBoolean("eligible")
    val variant: String?
        get() = if (!raw.has("variant") || raw.isNull("variant")) null else raw.getString("variant")
    val confidence: Double?
        get() = if (!raw.has("confidence") || raw.isNull("confidence")) null else raw.getDouble("confidence")
    val fullBadgeThreshold: Double?
        get() = if (!raw.has("fullBadgeThreshold") || raw.isNull("fullBadgeThreshold")) null else raw.getDouble("fullBadgeThreshold")
    val minimumConfidence: Double?
        get() = if (!raw.has("minimumConfidence") || raw.isNull("minimumConfidence")) null else raw.getDouble("minimumConfidence")
    val reason: String
        get() = raw.getString("reason")
}

data class VastuAssessmentBatchDataResultsItemResponseDataSourcesItem(val raw: JSONObject) {
    val source: String
        get() = raw.getString("source")
    val scope: String
        get() = raw.getString("scope")
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val classification: String
        get() = raw.getString("classification")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuAssessmentBatchDataResultsItemResponseData(val raw: JSONObject) {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val status: String
        get() = raw.getString("status")
    val score: Double?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getDouble("score")
    val confidence: Double
        get() = raw.getDouble("confidence")
    val badgeEligibility: VastuAssessmentBatchDataResultsItemResponseDataBadgeEligibility
        get() = VastuAssessmentBatchDataResultsItemResponseDataBadgeEligibility(raw.getJSONObject("badgeEligibility"))
    val findings: List<JSONObject>?
        get() = if (!raw.has("findings") || raw.isNull("findings")) null else List(raw.getJSONArray("findings").length()) { index0 -> (raw.getJSONArray("findings").get(index0) as JSONObject) }
    val maxScore: Double?
        get() = if (!raw.has("maxScore") || raw.isNull("maxScore")) null else raw.getDouble("maxScore")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val gradeLabel: String?
        get() = if (!raw.has("gradeLabel") || raw.isNull("gradeLabel")) null else raw.getString("gradeLabel")
    val scoreBreakdown: JSONObject?
        get() = if (!raw.has("scoreBreakdown") || raw.isNull("scoreBreakdown")) null else raw.getJSONObject("scoreBreakdown")
    val confidenceBasis: JSONObject
        get() = raw.getJSONObject("confidenceBasis")
    val entrance: JSONObject?
        get() = if (!raw.has("entrance") || raw.isNull("entrance")) null else raw.getJSONObject("entrance")
    val scanQuality: JSONObject?
        get() = if (!raw.has("scanQuality") || raw.isNull("scanQuality")) null else raw.getJSONObject("scanQuality")
    val zoneReference: JSONObject?
        get() = if (!raw.has("zoneReference") || raw.isNull("zoneReference")) null else raw.getJSONObject("zoneReference")
    val sources: List<VastuAssessmentBatchDataResultsItemResponseDataSourcesItem>?
        get() = if (!raw.has("sources") || raw.isNull("sources")) null else List(raw.getJSONArray("sources").length()) { index0 -> VastuAssessmentBatchDataResultsItemResponseDataSourcesItem((raw.getJSONArray("sources").get(index0) as JSONObject)) }
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val reason: String?
        get() = if (!raw.has("reason") || raw.isNull("reason")) null else raw.getString("reason")
    val requiredConfidence: Double?
        get() = if (!raw.has("requiredConfidence") || raw.isNull("requiredConfidence")) null else raw.getDouble("requiredConfidence")
    val missingData: List<String>?
        get() = if (!raw.has("missingData") || raw.isNull("missingData")) null else List(raw.getJSONArray("missingData").length()) { index0 -> (raw.getJSONArray("missingData").get(index0) as String) }
    val reScanSuggestions: List<String>?
        get() = if (!raw.has("reScanSuggestions") || raw.isNull("reScanSuggestions")) null else List(raw.getJSONArray("reScanSuggestions").length()) { index0 -> (raw.getJSONArray("reScanSuggestions").get(index0) as String) }
    val charged: Boolean?
        get() = if (!raw.has("charged") || raw.isNull("charged")) null else raw.getBoolean("charged")
    val meta: JSONObject
        get() = raw.getJSONObject("meta")
    val listingId: Any?
        get() = if (!raw.has("listingId") || raw.isNull("listingId")) null else raw.get("listingId").takeUnless { it == JSONObject.NULL }
}

data class VastuAssessmentBatchDataResultsItemResponseBilling(val raw: JSONObject) {
    val charged: Double
        get() = raw.getDouble("charged")
    val currency: String
        get() = raw.getString("currency")
    val balanceBefore: Double
        get() = raw.getDouble("balanceBefore")
    val balanceAfter: Double
        get() = raw.getDouble("balanceAfter")
    val endpoint: String
        get() = raw.getString("endpoint")
    val category: String
        get() = raw.getString("category")
}

data class VastuAssessmentBatchDataResultsItemResponseMeta(val raw: JSONObject) {
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val engine: String
        get() = raw.getString("engine")
    val version: String
        get() = raw.getString("version")
    val dataSource: String?
        get() = if (!raw.has("dataSource") || raw.isNull("dataSource")) null else raw.getString("dataSource")
}

data class VastuAssessmentBatchDataResultsItemResponse(val raw: JSONObject) {
    val success: Boolean
        get() = raw.getBoolean("success")
    val data: VastuAssessmentBatchDataResultsItemResponseData?
        get() = if (!raw.has("data") || raw.isNull("data")) null else VastuAssessmentBatchDataResultsItemResponseData(raw.getJSONObject("data"))
    val error: String?
        get() = if (!raw.has("error") || raw.isNull("error")) null else raw.getString("error")
    val code: String?
        get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    val billing: VastuAssessmentBatchDataResultsItemResponseBilling?
        get() = if (!raw.has("billing") || raw.isNull("billing")) null else VastuAssessmentBatchDataResultsItemResponseBilling(raw.getJSONObject("billing"))
    val meta: VastuAssessmentBatchDataResultsItemResponseMeta?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else VastuAssessmentBatchDataResultsItemResponseMeta(raw.getJSONObject("meta"))
}

data class VastuAssessmentBatchDataResultsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val status: Int
        get() = raw.getInt("status")
    val response: VastuAssessmentBatchDataResultsItemResponse
        get() = VastuAssessmentBatchDataResultsItemResponse(raw.getJSONObject("response"))
}

data class VastuAssessmentBatchDataSummary(val raw: JSONObject) {
    val total: Int
        get() = raw.getInt("total")
    val succeeded: Int
        get() = raw.getInt("succeeded")
    val failed: Int
        get() = raw.getInt("failed")
}

data class VastuAssessmentBadgeEligibility(val raw: JSONObject) {
    val inputSource: String
        get() = raw.getString("inputSource")
    val badge: String?
        get() = if (!raw.has("badge") || raw.isNull("badge")) null else raw.getString("badge")
    val eligible: Boolean
        get() = raw.getBoolean("eligible")
    val variant: String?
        get() = if (!raw.has("variant") || raw.isNull("variant")) null else raw.getString("variant")
    val confidence: Double?
        get() = if (!raw.has("confidence") || raw.isNull("confidence")) null else raw.getDouble("confidence")
    val fullBadgeThreshold: Double?
        get() = if (!raw.has("fullBadgeThreshold") || raw.isNull("fullBadgeThreshold")) null else raw.getDouble("fullBadgeThreshold")
    val minimumConfidence: Double?
        get() = if (!raw.has("minimumConfidence") || raw.isNull("minimumConfidence")) null else raw.getDouble("minimumConfidence")
    val reason: String
        get() = raw.getString("reason")
}

data class VastuAssessmentDataFindingsItem(val raw: JSONObject) {
    val code: String?
        get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val detail: String?
        get() = if (!raw.has("detail") || raw.isNull("detail")) null else raw.getString("detail")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
    val zoneReference: JSONObject?
        get() = if (!raw.has("zoneReference") || raw.isNull("zoneReference")) null else raw.getJSONObject("zoneReference")
    val pada: JSONObject?
        get() = if (!raw.has("pada") || raw.isNull("pada")) null else raw.getJSONObject("pada")
    val missingData: List<Any?>?
        get() = if (!raw.has("missingData") || raw.isNull("missingData")) null else List(raw.getJSONArray("missingData").length()) { index0 -> raw.getJSONArray("missingData").get(index0).takeUnless { it == JSONObject.NULL } }
    val reScanSuggestions: List<Any?>?
        get() = if (!raw.has("reScanSuggestions") || raw.isNull("reScanSuggestions")) null else List(raw.getJSONArray("reScanSuggestions").length()) { index0 -> raw.getJSONArray("reScanSuggestions").get(index0).takeUnless { it == JSONObject.NULL } }
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val computed: Boolean?
        get() = if (!raw.has("computed") || raw.isNull("computed")) null else raw.getBoolean("computed")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
}

data class VastuAssessmentDataSourcesItem(val raw: JSONObject) {
    val source: String
        get() = raw.getString("source")
    val scope: String
        get() = raw.getString("scope")
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val classification: String
        get() = raw.getString("classification")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuCatalogReferenceDataDefectsItem(val raw: JSONObject) {
    val labelKey: String?
        get() = if (!raw.has("labelKey") || raw.isNull("labelKey")) null else raw.getString("labelKey")
    val labelParams: JSONObject?
        get() = if (!raw.has("labelParams") || raw.isNull("labelParams")) null else raw.getJSONObject("labelParams")
    val code: String?
        get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    val label: String?
        get() = if (!raw.has("label") || raw.isNull("label")) null else raw.getString("label")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
}

data class VastuCatalogReferenceDataRemediesItem(val raw: JSONObject) {
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedyParams: JSONObject?
        get() = if (!raw.has("remedyParams") || raw.isNull("remedyParams")) null else raw.getJSONObject("remedyParams")
    val defectCode: String?
        get() = if (!raw.has("defectCode") || raw.isNull("defectCode")) null else raw.getString("defectCode")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
}

data class VastuComplianceIndexDataDrivingDefectsItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val weight: Int?
        get() = if (!raw.has("weight") || raw.isNull("weight")) null else raw.getInt("weight")
    val pointsLost: Double?
        get() = if (!raw.has("pointsLost") || raw.isNull("pointsLost")) null else raw.getDouble("pointsLost")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val computed: Boolean?
        get() = if (!raw.has("computed") || raw.isNull("computed")) null else raw.getBoolean("computed")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val ruleProvenance: JSONObject?
        get() = if (!raw.has("ruleProvenance") || raw.isNull("ruleProvenance")) null else raw.getJSONObject("ruleProvenance")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuComplianceIndexDataScoring(val raw: JSONObject) {
    val version: String
        get() = raw.getString("version")
    val unit: String
        get() = raw.getString("unit")
    val formula: String
        get() = raw.getString("formula")
    val basis: String
        get() = raw.getString("basis")
    val comparisonBasis: String
        get() = raw.getString("comparisonBasis")
    val classification: String
        get() = raw.getString("classification")
    val inputPlacementCount: Int
        get() = raw.getInt("inputPlacementCount")
    val uniquePlacementCount: Int
        get() = raw.getInt("uniquePlacementCount")
    val duplicatePlacementCount: Int
        get() = raw.getInt("duplicatePlacementCount")
    val verified: Boolean
        get() = raw.getBoolean("verified")
}

data class VastuDetailedFloorPlanAuditDataDefectsItemIssueParams(val raw: JSONObject) {
    val roomType: String
        get() = raw.getString("roomType")
    val zone: String
        get() = raw.getString("zone")
    val severity: String
        get() = raw.getString("severity")
}

data class VastuDetailedFloorPlanAuditDataDefectsItem(val raw: JSONObject) {
    val issueKey: String?
        get() = if (!raw.has("issueKey") || raw.isNull("issueKey")) null else raw.getString("issueKey")
    val issueParams: VastuDetailedFloorPlanAuditDataDefectsItemIssueParams?
        get() = if (!raw.has("issueParams") || raw.isNull("issueParams")) null else VastuDetailedFloorPlanAuditDataDefectsItemIssueParams(raw.getJSONObject("issueParams"))
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedyParams: JSONObject?
        get() = if (!raw.has("remedyParams") || raw.isNull("remedyParams")) null else raw.getJSONObject("remedyParams")
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val code: String?
        get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuDetailedFloorPlanAuditDataDevataHeatmapItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val deity: String?
        get() = if (!raw.has("deity") || raw.isNull("deity")) null else raw.getString("deity")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val devatas: List<JSONObject>?
        get() = if (!raw.has("devatas") || raw.isNull("devatas")) null else List(raw.getJSONArray("devatas").length()) { index0 -> (raw.getJSONArray("devatas").get(index0) as JSONObject) }
    val disturbed: Boolean?
        get() = if (!raw.has("disturbed") || raw.isNull("disturbed")) null else raw.getBoolean("disturbed")
}

data class VastuDetailedFloorPlanAuditDataRemediationOrderItem(val raw: JSONObject) {
    val actionKey: String?
        get() = if (!raw.has("actionKey") || raw.isNull("actionKey")) null else raw.getString("actionKey")
    val actionParams: JSONObject?
        get() = if (!raw.has("actionParams") || raw.isNull("actionParams")) null else raw.getJSONObject("actionParams")
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val action: String?
        get() = if (!raw.has("action") || raw.isNull("action")) null else raw.getString("action")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val code: String?
        get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    val step: Int?
        get() = if (!raw.has("step") || raw.isNull("step")) null else raw.getInt("step")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuDetailedFloorPlanAuditDataScoring(val raw: JSONObject) {
    val version: String
        get() = raw.getString("version")
    val unit: String
        get() = raw.getString("unit")
    val formula: String
        get() = raw.getString("formula")
    val basis: String
        get() = raw.getString("basis")
    val comparisonBasis: String
        get() = raw.getString("comparisonBasis")
    val classification: String
        get() = raw.getString("classification")
    val inputPlacementCount: Int
        get() = raw.getInt("inputPlacementCount")
    val uniquePlacementCount: Int
        get() = raw.getInt("uniquePlacementCount")
    val duplicatePlacementCount: Int
        get() = raw.getInt("duplicatePlacementCount")
    val verified: Boolean
        get() = raw.getBoolean("verified")
}

data class VastuDetailedFloorPlanAuditDataCompleteness(val raw: JSONObject) {
    val status: String
        get() = raw.getString("status")
    val computedComponents: List<String>
        get() = List(raw.getJSONArray("computedComponents").length()) { index0 -> (raw.getJSONArray("computedComponents").get(index0) as String) }
    val missingInputs: List<String>
        get() = List(raw.getJSONArray("missingInputs").length()) { index0 -> (raw.getJSONArray("missingInputs").get(index0) as String) }
    val projectedCellCount: Int
        get() = raw.getInt("projectedCellCount")
    val physicalCoverageVerified: Boolean
        get() = raw.getBoolean("physicalCoverageVerified")
    val note: String
        get() = raw.getString("note")
}

data class VastuDirectionsReferenceDataDirectionsItem(val raw: JSONObject) {
    val code: String?
        get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    val sanskrit: String?
        get() = if (!raw.has("sanskrit") || raw.isNull("sanskrit")) null else raw.getString("sanskrit")
    val deity: String?
        get() = if (!raw.has("deity") || raw.isNull("deity")) null else raw.getString("deity")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val element: String?
        get() = if (!raw.has("element") || raw.isNull("element")) null else raw.getString("element")
    val bearingStart: Double?
        get() = if (!raw.has("bearingStart") || raw.isNull("bearingStart")) null else raw.getDouble("bearingStart")
    val bearingEnd: Double?
        get() = if (!raw.has("bearingEnd") || raw.isNull("bearingEnd")) null else raw.getDouble("bearingEnd")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
}

data class VastuEntrancePadaDataPada(val raw: JSONObject) {
    val index: Int
        get() = raw.getInt("index")
    val deity: String
        get() = raw.getString("deity")
    val quadrant: String
        get() = raw.getString("quadrant")
    val subIndex: Int
        get() = raw.getInt("subIndex")
    val bearingStart: Double
        get() = raw.getDouble("bearingStart")
    val bearingEnd: Double
        get() = raw.getDouble("bearingEnd")
    val auspiciousness: String
        get() = raw.getString("auspiciousness")
    val source: String
        get() = raw.getString("source")
    val deityRosterName: String?
        get() = if (!raw.has("deityRosterName") || raw.isNull("deityRosterName")) null else raw.getString("deityRosterName")
    val deityNameClassification: String?
        get() = if (!raw.has("deityNameClassification") || raw.isNull("deityNameClassification")) null else raw.getString("deityNameClassification")
    val deityNameSource: String?
        get() = if (!raw.has("deityNameSource") || raw.isNull("deityNameSource")) null else raw.getString("deityNameSource")
    val deityPlacementClassification: String?
        get() = if (!raw.has("deityPlacementClassification") || raw.isNull("deityPlacementClassification")) null else raw.getString("deityPlacementClassification")
    val deityPlacementSource: String?
        get() = if (!raw.has("deityPlacementSource") || raw.isNull("deityPlacementSource")) null else raw.getString("deityPlacementSource")
}

data class VastuFloorPlanAuditDataDefectsItemIssueParams(val raw: JSONObject) {
    val roomType: String
        get() = raw.getString("roomType")
    val zone: String
        get() = raw.getString("zone")
    val severity: String
        get() = raw.getString("severity")
}

data class VastuFloorPlanAuditDataDefectsItem(val raw: JSONObject) {
    val issueKey: String?
        get() = if (!raw.has("issueKey") || raw.isNull("issueKey")) null else raw.getString("issueKey")
    val issueParams: VastuFloorPlanAuditDataDefectsItemIssueParams?
        get() = if (!raw.has("issueParams") || raw.isNull("issueParams")) null else VastuFloorPlanAuditDataDefectsItemIssueParams(raw.getJSONObject("issueParams"))
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedyParams: JSONObject?
        get() = if (!raw.has("remedyParams") || raw.isNull("remedyParams")) null else raw.getJSONObject("remedyParams")
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val code: String?
        get() = if (!raw.has("code") || raw.isNull("code")) null else raw.getString("code")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuFloorPlanAuditDataScoring(val raw: JSONObject) {
    val version: String
        get() = raw.getString("version")
    val unit: String
        get() = raw.getString("unit")
    val formula: String
        get() = raw.getString("formula")
    val basis: String
        get() = raw.getString("basis")
    val comparisonBasis: String
        get() = raw.getString("comparisonBasis")
    val classification: String
        get() = raw.getString("classification")
    val inputPlacementCount: Int
        get() = raw.getInt("inputPlacementCount")
    val uniquePlacementCount: Int
        get() = raw.getInt("uniquePlacementCount")
    val duplicatePlacementCount: Int
        get() = raw.getInt("duplicatePlacementCount")
    val verified: Boolean
        get() = raw.getBoolean("verified")
}

data class VastuFloorPlanAuditDataTextParse(val raw: JSONObject) {
    val version: String
        get() = raw.getString("version")
    val transliterations: String
        get() = raw.getString("transliterations")
    val grammar: String
        get() = raw.getString("grammar")
    val coverage: String
        get() = raw.getString("coverage")
    val supportedLanguages: List<String>
        get() = List(raw.getJSONArray("supportedLanguages").length()) { index0 -> (raw.getJSONArray("supportedLanguages").get(index0) as String) }
    val roomVocabulary: List<String>
        get() = List(raw.getJSONArray("roomVocabulary").length()) { index0 -> (raw.getJSONArray("roomVocabulary").get(index0) as String) }
    val directionVocabulary: List<String>
        get() = List(raw.getJSONArray("directionVocabulary").length()) { index0 -> (raw.getJSONArray("directionVocabulary").get(index0) as String) }
    val unparsedClauses: List<String>
        get() = List(raw.getJSONArray("unparsedClauses").length()) { index0 -> (raw.getJSONArray("unparsedClauses").get(index0) as String) }
    val parsedClauseCount: Int
        get() = raw.getInt("parsedClauseCount")
}

data class VastuMandalaReferenceDataZonesItem(val raw: JSONObject) {
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedyParams: JSONObject?
        get() = if (!raw.has("remedyParams") || raw.isNull("remedyParams")) null else raw.getJSONObject("remedyParams")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val deity: String?
        get() = if (!raw.has("deity") || raw.isNull("deity")) null else raw.getString("deity")
    val element: String?
        get() = if (!raw.has("element") || raw.isNull("element")) null else raw.getString("element")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val sourceClassification: String?
        get() = if (!raw.has("sourceClassification") || raw.isNull("sourceClassification")) null else raw.getString("sourceClassification")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
    val prescribed: List<String>?
        get() = if (!raw.has("prescribed") || raw.isNull("prescribed")) null else List(raw.getJSONArray("prescribed").length()) { index0 -> (raw.getJSONArray("prescribed").get(index0) as String) }
    val forbidden: List<String>?
        get() = if (!raw.has("forbidden") || raw.isNull("forbidden")) null else List(raw.getJSONArray("forbidden").length()) { index0 -> (raw.getJSONArray("forbidden").get(index0) as String) }
    val verseBackedRooms: List<String>?
        get() = if (!raw.has("verseBackedRooms") || raw.isNull("verseBackedRooms")) null else List(raw.getJSONArray("verseBackedRooms").length()) { index0 -> (raw.getJSONArray("verseBackedRooms").get(index0) as String) }
    val verseBackedRoomsSource: String?
        get() = if (!raw.has("verseBackedRoomsSource") || raw.isNull("verseBackedRoomsSource")) null else raw.getString("verseBackedRoomsSource")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val elementClassification: String?
        get() = if (!raw.has("elementClassification") || raw.isNull("elementClassification")) null else raw.getString("elementClassification")
    val elementSource: String?
        get() = if (!raw.has("elementSource") || raw.isNull("elementSource")) null else raw.getString("elementSource")
}

data class VastuMandalaReferenceDataCellsItem(val raw: JSONObject) {
    val id: String?
        get() = if (!raw.has("id") || raw.isNull("id")) null else raw.getString("id")
    val padaNumber: Int?
        get() = if (!raw.has("padaNumber") || raw.isNull("padaNumber")) null else raw.getInt("padaNumber")
    val row: Int?
        get() = if (!raw.has("row") || raw.isNull("row")) null else raw.getInt("row")
    val col: Int?
        get() = if (!raw.has("col") || raw.isNull("col")) null else raw.getInt("col")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val isBrahmasthan: Boolean?
        get() = if (!raw.has("isBrahmasthan") || raw.isNull("isBrahmasthan")) null else raw.getBoolean("isBrahmasthan")
    val devata: String?
        get() = if (!raw.has("devata") || raw.isNull("devata")) null else raw.getString("devata")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val devataVerified: Boolean?
        get() = if (!raw.has("devataVerified") || raw.isNull("devataVerified")) null else raw.getBoolean("devataVerified")
    val devataNameVerified: Boolean?
        get() = if (!raw.has("devataNameVerified") || raw.isNull("devataNameVerified")) null else raw.getBoolean("devataNameVerified")
    val placementClassification: String?
        get() = if (!raw.has("placementClassification") || raw.isNull("placementClassification")) null else raw.getString("placementClassification")
    val placementSource: String?
        get() = if (!raw.has("placementSource") || raw.isNull("placementSource")) null else raw.getString("placementSource")
    val polygon: List<List<Double>>?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val centroid: List<Double>?
        get() = if (!raw.has("centroid") || raw.isNull("centroid")) null else List(raw.getJSONArray("centroid").length()) { index0 -> (raw.getJSONArray("centroid").get(index0) as Number).toDouble() }
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
    val insidePlot: Boolean?
        get() = if (!raw.has("insidePlot") || raw.isNull("insidePlot")) null else raw.getBoolean("insidePlot")
}

data class VastuOverallScoreDataPlacementsItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val weight: Int?
        get() = if (!raw.has("weight") || raw.isNull("weight")) null else raw.getInt("weight")
    val merit: Double?
        get() = if (!raw.has("merit") || raw.isNull("merit")) null else raw.getDouble("merit")
    val demerit: Double?
        get() = if (!raw.has("demerit") || raw.isNull("demerit")) null else raw.getDouble("demerit")
    val compliant: Boolean?
        get() = if (!raw.has("compliant") || raw.isNull("compliant")) null else raw.getBoolean("compliant")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val computed: Boolean?
        get() = if (!raw.has("computed") || raw.isNull("computed")) null else raw.getBoolean("computed")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val ruleProvenance: JSONObject?
        get() = if (!raw.has("ruleProvenance") || raw.isNull("ruleProvenance")) null else raw.getJSONObject("ruleProvenance")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuOverallScoreDataScoring(val raw: JSONObject) {
    val version: String
        get() = raw.getString("version")
    val unit: String
        get() = raw.getString("unit")
    val formula: String
        get() = raw.getString("formula")
    val basis: String
        get() = raw.getString("basis")
    val comparisonBasis: String
        get() = raw.getString("comparisonBasis")
    val classification: String
        get() = raw.getString("classification")
    val inputPlacementCount: Int
        get() = raw.getInt("inputPlacementCount")
    val uniquePlacementCount: Int
        get() = raw.getInt("uniquePlacementCount")
    val duplicatePlacementCount: Int
        get() = raw.getInt("duplicatePlacementCount")
    val verified: Boolean
        get() = raw.getBoolean("verified")
}

data class VastuPlanAuditDataRoomByRoomItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val zoneSource: String?
        get() = if (!raw.has("zoneSource") || raw.isNull("zoneSource")) null else raw.getString("zoneSource")
    val ideal: String?
        get() = if (!raw.has("ideal") || raw.isNull("ideal")) null else raw.getString("ideal")
    val verdict: String?
        get() = if (!raw.has("verdict") || raw.isNull("verdict")) null else raw.getString("verdict")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val defect: String?
        get() = if (!raw.has("defect") || raw.isNull("defect")) null else raw.getString("defect")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val computed: Boolean?
        get() = if (!raw.has("computed") || raw.isNull("computed")) null else raw.getBoolean("computed")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val ruleProvenance: JSONObject?
        get() = if (!raw.has("ruleProvenance") || raw.isNull("ruleProvenance")) null else raw.getJSONObject("ruleProvenance")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuPlanAuditDataDefectsItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuPlanAuditDataRemediesItem(val raw: JSONObject) {
    val priority: Int?
        get() = if (!raw.has("priority") || raw.isNull("priority")) null else raw.getInt("priority")
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val action: String?
        get() = if (!raw.has("action") || raw.isNull("action")) null else raw.getString("action")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuPlanAuditDataArtifact(val raw: JSONObject) {
    val contentType: String
        get() = raw.getString("contentType")
    val filename: String
        get() = raw.getString("filename")
    val content: String
        get() = raw.getString("content")
}

data class VastuRemedyComparisonDataBeforeDefectsItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuRemedyComparisonDataBefore(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val defectCount: Int?
        get() = if (!raw.has("defectCount") || raw.isNull("defectCount")) null else raw.getInt("defectCount")
    val prescribedCount: Int?
        get() = if (!raw.has("prescribedCount") || raw.isNull("prescribedCount")) null else raw.getInt("prescribedCount")
    val defects: List<VastuRemedyComparisonDataBeforeDefectsItem>?
        get() = if (!raw.has("defects") || raw.isNull("defects")) null else List(raw.getJSONArray("defects").length()) { index0 -> VastuRemedyComparisonDataBeforeDefectsItem((raw.getJSONArray("defects").get(index0) as JSONObject)) }
}

data class VastuRemedyComparisonDataAfterDefectsItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuRemedyComparisonDataAfter(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val defectCount: Int?
        get() = if (!raw.has("defectCount") || raw.isNull("defectCount")) null else raw.getInt("defectCount")
    val prescribedCount: Int?
        get() = if (!raw.has("prescribedCount") || raw.isNull("prescribedCount")) null else raw.getInt("prescribedCount")
    val defects: List<VastuRemedyComparisonDataAfterDefectsItem>?
        get() = if (!raw.has("defects") || raw.isNull("defects")) null else List(raw.getJSONArray("defects").length()) { index0 -> VastuRemedyComparisonDataAfterDefectsItem((raw.getJSONArray("defects").get(index0) as JSONObject)) }
}

data class VastuSpecializedAuditDataFindingsItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val function: String?
        get() = if (!raw.has("function") || raw.isNull("function")) null else raw.getString("function")
    val status: String?
        get() = if (!raw.has("status") || raw.isNull("status")) null else raw.getString("status")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val idealZones: List<String>?
        get() = if (!raw.has("idealZones") || raw.isNull("idealZones")) null else List(raw.getJSONArray("idealZones").length()) { index0 -> (raw.getJSONArray("idealZones").get(index0) as String) }
    val deity: String?
        get() = if (!raw.has("deity") || raw.isNull("deity")) null else raw.getString("deity")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val element: String?
        get() = if (!raw.has("element") || raw.isNull("element")) null else raw.getString("element")
    val elementTradition: String?
        get() = if (!raw.has("elementTradition") || raw.isNull("elementTradition")) null else raw.getString("elementTradition")
    val waterEffect: JSONObject?
        get() = if (!raw.has("waterEffect") || raw.isNull("waterEffect")) null else raw.getJSONObject("waterEffect")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val computed: Boolean?
        get() = if (!raw.has("computed") || raw.isNull("computed")) null else raw.getBoolean("computed")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
}

data class VastuSunPathDataInput(val raw: JSONObject) {
    val lat: Double
        get() = raw.getDouble("lat")
    val lon: Double
        get() = raw.getDouble("lon")
    val date: String
        get() = raw.getString("date")
}

data class VastuZoneWiseScoreDataZonesItemRoomsItem(val raw: JSONObject) {
    val room: String?
        get() = if (!raw.has("room") || raw.isNull("room")) null else raw.getString("room")
    val severity: String?
        get() = if (!raw.has("severity") || raw.isNull("severity")) null else raw.getString("severity")
    val compliant: Boolean?
        get() = if (!raw.has("compliant") || raw.isNull("compliant")) null else raw.getBoolean("compliant")
    val issue: String?
        get() = if (!raw.has("issue") || raw.isNull("issue")) null else raw.getString("issue")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
}

data class VastuZoneWiseScoreDataZonesItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val zoneWeight: Int?
        get() = if (!raw.has("zoneWeight") || raw.isNull("zoneWeight")) null else raw.getInt("zoneWeight")
    val zoneImportance: String?
        get() = if (!raw.has("zoneImportance") || raw.isNull("zoneImportance")) null else raw.getString("zoneImportance")
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val worstSeverity: String?
        get() = if (!raw.has("worstSeverity") || raw.isNull("worstSeverity")) null else raw.getString("worstSeverity")
    val rooms: List<VastuZoneWiseScoreDataZonesItemRoomsItem>?
        get() = if (!raw.has("rooms") || raw.isNull("rooms")) null else List(raw.getJSONArray("rooms").length()) { index0 -> VastuZoneWiseScoreDataZonesItemRoomsItem((raw.getJSONArray("rooms").get(index0) as JSONObject)) }
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuZoneWiseScoreDataScoring(val raw: JSONObject) {
    val version: String
        get() = raw.getString("version")
    val unit: String
        get() = raw.getString("unit")
    val formula: String
        get() = raw.getString("formula")
    val basis: String
        get() = raw.getString("basis")
    val comparisonBasis: String
        get() = raw.getString("comparisonBasis")
    val classification: String
        get() = raw.getString("classification")
    val inputPlacementCount: Int
        get() = raw.getInt("inputPlacementCount")
    val uniquePlacementCount: Int
        get() = raw.getInt("uniquePlacementCount")
    val duplicatePlacementCount: Int
        get() = raw.getInt("duplicatePlacementCount")
    val verified: Boolean
        get() = raw.getBoolean("verified")
}

data class VastuArAnchorRecommendationsData(override val raw: JSONObject) : VastuData {
    val anchors: List<VastuArAnchorRecommendationsDataAnchorsItem>
        get() = List(raw.getJSONArray("anchors").length()) { index0 -> VastuArAnchorRecommendationsDataAnchorsItem((raw.getJSONArray("anchors").get(index0) as JSONObject)) }
    val omittedZones: List<String>
        get() = List(raw.getJSONArray("omittedZones").length()) { index0 -> (raw.getJSONArray("omittedZones").get(index0) as String) }
    val planToWorld: Any?
        get() = raw.get("planToWorld").takeUnless { it == JSONObject.NULL }
    val bearingDeg: Double
        get() = raw.getDouble("bearingDeg")
    val bearingAssumedNorth: Boolean
        get() = raw.getBoolean("bearingAssumedNorth")
    val physicalRegistrationVerified: Boolean
        get() = raw.getBoolean("physicalRegistrationVerified")
    val physicalNorthVerified: Boolean
        get() = raw.getBoolean("physicalNorthVerified")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val provenance: Any?
        get() = raw.get("provenance").takeUnless { it == JSONObject.NULL }
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val physicalCoverageVerified: Boolean
        get() = raw.getBoolean("physicalCoverageVerified")
    val coordinateNote: String
        get() = raw.getString("coordinateNote")
    val omissionNote: String
        get() = raw.getString("omissionNote")
}

data class VastuArDeityIconsData(override val raw: JSONObject) : VastuData {
    val icons: List<VastuArDeityIconsDataIconsItem>
        get() = List(raw.getJSONArray("icons").length()) { index0 -> VastuArDeityIconsDataIconsItem((raw.getJSONArray("icons").get(index0) as JSONObject)) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val provenance: Any?
        get() = raw.get("provenance").takeUnless { it == JSONObject.NULL }
}

data class VastuArHeatmapRasterData(override val raw: JSONObject) : VastuData {
    val mask: Int
        get() = raw.getInt("mask")
    val texture: Any?
        get() = raw.get("texture").takeUnless { it == JSONObject.NULL }
    val maskBitOrder: List<String>
        get() = List(raw.getJSONArray("maskBitOrder").length()) { index0 -> (raw.getJSONArray("maskBitOrder").get(index0) as String) }
    val zones: List<VastuArHeatmapRasterDataZonesItem>
        get() = List(raw.getJSONArray("zones").length()) { index0 -> VastuArHeatmapRasterDataZonesItem((raw.getJSONArray("zones").get(index0) as JSONObject)) }
    val observedZones: List<String>
        get() = List(raw.getJSONArray("observedZones").length()) { index0 -> (raw.getJSONArray("observedZones").get(index0) as String) }
    val unobservedZones: List<String>
        get() = List(raw.getJSONArray("unobservedZones").length()) { index0 -> (raw.getJSONArray("unobservedZones").get(index0) as String) }
    val mandalaProjection: JSONObject?
        get() = if (!raw.has("mandalaProjection") || raw.isNull("mandalaProjection")) null else raw.getJSONObject("mandalaProjection")
    val bearingAssumedNorth: Boolean
        get() = raw.getBoolean("bearingAssumedNorth")
    val completeness: VastuArHeatmapRasterDataCompleteness
        get() = VastuArHeatmapRasterDataCompleteness(raw.getJSONObject("completeness"))
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val physicalCoverageVerified: Boolean
        get() = raw.getBoolean("physicalCoverageVerified")
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val provenance: Any?
        get() = raw.get("provenance").takeUnless { it == JSONObject.NULL }
    val legend: VastuArHeatmapRasterDataLegend
        get() = VastuArHeatmapRasterDataLegend(raw.getJSONObject("legend"))
    val computed: Boolean
        get() = raw.getBoolean("computed")
}

data class VastuArRoomCaptureData(override val raw: JSONObject) : VastuData {
    val method: String
        get() = raw.getString("method")
    val schema: String
        get() = raw.getString("schema")
    val capture: VastuArRoomCaptureDataCapture
        get() = VastuArRoomCaptureDataCapture(raw.getJSONObject("capture"))
    val rooms: List<VastuArRoomCaptureDataRoomsItem>
        get() = List(raw.getJSONArray("rooms").length()) { index0 -> VastuArRoomCaptureDataRoomsItem((raw.getJSONArray("rooms").get(index0) as JSONObject)) }
    val derivedRequests: VastuArRoomCaptureDataDerivedRequests
        get() = VastuArRoomCaptureDataDerivedRequests(raw.getJSONObject("derivedRequests"))
    val planAnalysis: Any?
        get() = raw.get("planAnalysis").takeUnless { it == JSONObject.NULL }
    val audit: Any?
        get() = raw.get("audit").takeUnless { it == JSONObject.NULL }
    val scanQuality: Any?
        get() = raw.get("scanQuality").takeUnless { it == JSONObject.NULL }
    val anchorRecommendations: Any?
        get() = if (!raw.has("anchorRecommendations") || raw.isNull("anchorRecommendations")) null else raw.get("anchorRecommendations").takeUnless { it == JSONObject.NULL }
    val completeness: VastuArRoomCaptureDataCompleteness
        get() = VastuArRoomCaptureDataCompleteness(raw.getJSONObject("completeness"))
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val captureVerification: String
        get() = raw.getString("captureVerification")
    val attestation: String
        get() = raw.getString("attestation")
    val note: String
        get() = raw.getString("note")
}

data class VastuArScanQualityData(override val raw: JSONObject) : VastuData {
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val missingData: List<String>
        get() = List(raw.getJSONArray("missingData").length()) { index0 -> (raw.getJSONArray("missingData").get(index0) as String) }
    val warnings: List<String>
        get() = List(raw.getJSONArray("warnings").length()) { index0 -> (raw.getJSONArray("warnings").get(index0) as String) }
    val reScanSuggestions: List<String>
        get() = List(raw.getJSONArray("reScanSuggestions").length()) { index0 -> (raw.getJSONArray("reScanSuggestions").get(index0) as String) }
    val dimensions: VastuArScanQualityDataDimensions
        get() = VastuArScanQualityDataDimensions(raw.getJSONObject("dimensions"))
    val acceptForAudit: Boolean
        get() = raw.getBoolean("acceptForAudit")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val roomsTaggedCount: Double?
        get() = if (!raw.has("roomsTaggedCount") || raw.isNull("roomsTaggedCount")) null else raw.getDouble("roomsTaggedCount")
    val unreportedDimensions: List<String>
        get() = List(raw.getJSONArray("unreportedDimensions").length()) { index0 -> (raw.getJSONArray("unreportedDimensions").get(index0) as String) }
    val roomCount: Int?
        get() = if (!raw.has("roomCount") || raw.isNull("roomCount")) null else raw.getInt("roomCount")
    val roomCoverage: VastuArScanQualityDataRoomCoverage
        get() = VastuArScanQualityDataRoomCoverage(raw.getJSONObject("roomCoverage"))
    val scoreScope: String
        get() = raw.getString("scoreScope")
    val evidenceSource: String
        get() = raw.getString("evidenceSource")
    val sensorAttestation: Boolean
        get() = raw.getBoolean("sensorAttestation")
    val limitations: String
        get() = raw.getString("limitations")
}

data class VastuArTrueNorthData(override val raw: JSONObject) : VastuData {
    val input: VastuArTrueNorthDataInput
        get() = VastuArTrueNorthDataInput(raw.getJSONObject("input"))
    val sunAzimuthTrueDeg: Double
        get() = raw.getDouble("sunAzimuthTrueDeg")
    val solarElevationDeg: Double
        get() = raw.getDouble("solarElevationDeg")
    val offsetDeg: Double
        get() = raw.getDouble("offsetDeg")
    val headingCorrection: String
        get() = raw.getString("headingCorrection")
    val reliable: Boolean
        get() = raw.getBoolean("reliable")
    val reason: String
        get() = raw.getString("reason")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val solarGeometryReliable: Boolean
        get() = raw.getBoolean("solarGeometryReliable")
    val headingQuality: VastuArTrueNorthDataHeadingQuality
        get() = VastuArTrueNorthDataHeadingQuality(raw.getJSONObject("headingQuality"))
}

data class VastuArYantraMeshesData(override val raw: JSONObject) : VastuData {
    val name: String
        get() = raw.getString("name")
    val format: String
        get() = raw.getString("format")
    val asset: Any?
        get() = raw.get("asset").takeUnless { it == JSONObject.NULL }
    val dimensionsMetres: List<Double>
        get() = List(raw.getJSONArray("dimensionsMetres").length()) { index0 -> (raw.getJSONArray("dimensionsMetres").get(index0) as Number).toDouble() }
    val geometry: VastuArYantraMeshesDataGeometry
        get() = VastuArYantraMeshesDataGeometry(raw.getJSONObject("geometry"))
    val ritualDesign: Boolean
        get() = raw.getBoolean("ritualDesign")
    val remedyEfficacyClaimed: Boolean
        get() = raw.getBoolean("remedyEfficacyClaimed")
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val provenance: Any?
        get() = raw.get("provenance").takeUnless { it == JSONObject.NULL }
    val assetId: String
        get() = raw.getString("assetId")
}

data class VastuArZoneTexturesData(override val raw: JSONObject) : VastuData {
    val png: Any?
        get() = raw.get("png").takeUnless { it == JSONObject.NULL }
    val svg: Any?
        get() = raw.get("svg").takeUnless { it == JSONObject.NULL }
    val width: Int
        get() = raw.getInt("width")
    val height: Int
        get() = raw.getInt("height")
    val cells: List<VastuArZoneTexturesDataCellsItem>
        get() = List(raw.getJSONArray("cells").length()) { index0 -> VastuArZoneTexturesDataCellsItem((raw.getJSONArray("cells").get(index0) as JSONObject)) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val provenance: Any?
        get() = raw.get("provenance").takeUnless { it == JSONObject.NULL }
    val pixelBoundsConvention: String
        get() = raw.getString("pixelBoundsConvention")
    val gltfUvOrigin: String
        get() = raw.getString("gltfUvOrigin")
    val usdUvOrigin: String
        get() = raw.getString("usdUvOrigin")
}

data class VastuAssessmentBatchData(override val raw: JSONObject) : VastuData {
    val results: List<VastuAssessmentBatchDataResultsItem>
        get() = List(raw.getJSONArray("results").length()) { index0 -> VastuAssessmentBatchDataResultsItem((raw.getJSONArray("results").get(index0) as JSONObject)) }
    val summary: VastuAssessmentBatchDataSummary
        get() = VastuAssessmentBatchDataSummary(raw.getJSONObject("summary"))
    val billingBasis: String
        get() = raw.getString("billingBasis")
    val execution: String
        get() = raw.getString("execution")
}

data class VastuAssessmentData(override val raw: JSONObject) : VastuData {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val status: String
        get() = raw.getString("status")
    val score: Double?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getDouble("score")
    val confidence: Double
        get() = raw.getDouble("confidence")
    val badgeEligibility: VastuAssessmentBadgeEligibility
        get() = VastuAssessmentBadgeEligibility(raw.getJSONObject("badgeEligibility"))
    val findings: List<VastuAssessmentDataFindingsItem>?
        get() = if (!raw.has("findings") || raw.isNull("findings")) null else List(raw.getJSONArray("findings").length()) { index0 -> VastuAssessmentDataFindingsItem((raw.getJSONArray("findings").get(index0) as JSONObject)) }
    val maxScore: Double?
        get() = if (!raw.has("maxScore") || raw.isNull("maxScore")) null else raw.getDouble("maxScore")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val gradeLabel: String?
        get() = if (!raw.has("gradeLabel") || raw.isNull("gradeLabel")) null else raw.getString("gradeLabel")
    val scoreBreakdown: JSONObject?
        get() = if (!raw.has("scoreBreakdown") || raw.isNull("scoreBreakdown")) null else raw.getJSONObject("scoreBreakdown")
    val confidenceBasis: JSONObject
        get() = raw.getJSONObject("confidenceBasis")
    val entrance: JSONObject?
        get() = if (!raw.has("entrance") || raw.isNull("entrance")) null else raw.getJSONObject("entrance")
    val scanQuality: JSONObject?
        get() = if (!raw.has("scanQuality") || raw.isNull("scanQuality")) null else raw.getJSONObject("scanQuality")
    val zoneReference: JSONObject?
        get() = if (!raw.has("zoneReference") || raw.isNull("zoneReference")) null else raw.getJSONObject("zoneReference")
    val sources: List<VastuAssessmentDataSourcesItem>?
        get() = if (!raw.has("sources") || raw.isNull("sources")) null else List(raw.getJSONArray("sources").length()) { index0 -> VastuAssessmentDataSourcesItem((raw.getJSONArray("sources").get(index0) as JSONObject)) }
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val reason: String?
        get() = if (!raw.has("reason") || raw.isNull("reason")) null else raw.getString("reason")
    val requiredConfidence: Double?
        get() = if (!raw.has("requiredConfidence") || raw.isNull("requiredConfidence")) null else raw.getDouble("requiredConfidence")
    val missingData: List<String>?
        get() = if (!raw.has("missingData") || raw.isNull("missingData")) null else List(raw.getJSONArray("missingData").length()) { index0 -> (raw.getJSONArray("missingData").get(index0) as String) }
    val reScanSuggestions: List<String>?
        get() = if (!raw.has("reScanSuggestions") || raw.isNull("reScanSuggestions")) null else List(raw.getJSONArray("reScanSuggestions").length()) { index0 -> (raw.getJSONArray("reScanSuggestions").get(index0) as String) }
    val charged: Boolean?
        get() = if (!raw.has("charged") || raw.isNull("charged")) null else raw.getBoolean("charged")
    val meta: JSONObject
        get() = raw.getJSONObject("meta")
    val listingId: Any?
        get() = if (!raw.has("listingId") || raw.isNull("listingId")) null else raw.get("listingId").takeUnless { it == JSONObject.NULL }
}

data class VastuAuspiciousFacingData(override val raw: JSONObject) : VastuData {
    val purpose: String
        get() = raw.getString("purpose")
    val bestFacing: List<JSONObject>
        get() = List(raw.getJSONArray("bestFacing").length()) { index0 -> (raw.getJSONArray("bestFacing").get(index0) as JSONObject) }
    val bestZone: List<JSONObject>
        get() = List(raw.getJSONArray("bestZone").length()) { index0 -> (raw.getJSONArray("bestZone").get(index0) as JSONObject) }
    val avoidFacing: List<JSONObject>
        get() = List(raw.getJSONArray("avoidFacing").length()) { index0 -> (raw.getJSONArray("avoidFacing").get(index0) as JSONObject) }
    val verifiedPlacement: JSONObject?
        get() = if (!raw.has("verifiedPlacement") || raw.isNull("verifiedPlacement")) null else raw.getJSONObject("verifiedPlacement")
    val zoneComplianceCheck: List<JSONObject>
        get() = List(raw.getJSONArray("zoneComplianceCheck").length()) { index0 -> (raw.getJSONArray("zoneComplianceCheck").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val rationale: String?
        get() = if (!raw.has("rationale") || raw.isNull("rationale")) null else raw.getString("rationale")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuBearingZoneData(override val raw: JSONObject) : VastuData {
    val bearingDeg: Double
        get() = raw.getDouble("bearingDeg")
    val zone: String
        get() = raw.getString("zone")
    val deity: String
        get() = raw.getString("deity")
    val element: String
        get() = raw.getString("element")
    val prescribedRooms: List<String>
        get() = List(raw.getJSONArray("prescribedRooms").length()) { index0 -> (raw.getJSONArray("prescribedRooms").get(index0) as String) }
    val forbiddenRooms: List<String>
        get() = List(raw.getJSONArray("forbiddenRooms").length()) { index0 -> (raw.getJSONArray("forbiddenRooms").get(index0) as String) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val elementClassification: String?
        get() = if (!raw.has("elementClassification") || raw.isNull("elementClassification")) null else raw.getString("elementClassification")
    val elementSource: String?
        get() = if (!raw.has("elementSource") || raw.isNull("elementSource")) null else raw.getString("elementSource")
    val verseBackedRooms: List<String>?
        get() = if (!raw.has("verseBackedRooms") || raw.isNull("verseBackedRooms")) null else List(raw.getJSONArray("verseBackedRooms").length()) { index0 -> (raw.getJSONArray("verseBackedRooms").get(index0) as String) }
    val roomRulesClassification: String?
        get() = if (!raw.has("roomRulesClassification") || raw.isNull("roomRulesClassification")) null else raw.getString("roomRulesClassification")
}

data class VastuBrahmasthanProjectionData(override val raw: JSONObject) : VastuData {
    val centerPolygon: List<List<Double>>
        get() = List(raw.getJSONArray("centerPolygon").length()) { index0 -> List((raw.getJSONArray("centerPolygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("centerPolygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val bufferPolygon: List<List<Double>>
        get() = List(raw.getJSONArray("bufferPolygon").length()) { index0 -> List((raw.getJSONArray("bufferPolygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("bufferPolygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val centroid: List<Double>
        get() = List(raw.getJSONArray("centroid").length()) { index0 -> (raw.getJSONArray("centroid").get(index0) as Number).toDouble() }
    val area: Double
        get() = raw.getDouble("area")
    val forbiddenActions: List<String>
        get() = List(raw.getJSONArray("forbiddenActions").length()) { index0 -> (raw.getJSONArray("forbiddenActions").get(index0) as String) }
    val classicalSource: String
        get() = raw.getString("classicalSource")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val classification: String
        get() = raw.getString("classification")
    val bearingAssumedNorth: Boolean?
        get() = if (!raw.has("bearingAssumedNorth") || raw.isNull("bearingAssumedNorth")) null else raw.getBoolean("bearingAssumedNorth")
    val forbiddenActionsClassification: String?
        get() = if (!raw.has("forbiddenActionsClassification") || raw.isNull("forbiddenActionsClassification")) null else raw.getString("forbiddenActionsClassification")
    val forbiddenActionsSource: String?
        get() = if (!raw.has("forbiddenActionsSource") || raw.isNull("forbiddenActionsSource")) null else raw.getString("forbiddenActionsSource")
    val classicalSourceScope: String?
        get() = if (!raw.has("classicalSourceScope") || raw.isNull("classicalSourceScope")) null else raw.getString("classicalSourceScope")
}

data class VastuCatalogReferenceData(override val raw: JSONObject) : VastuData {
    val defectCount: Int?
        get() = if (!raw.has("defectCount") || raw.isNull("defectCount")) null else raw.getInt("defectCount")
    val defects: List<VastuCatalogReferenceDataDefectsItem>?
        get() = if (!raw.has("defects") || raw.isNull("defects")) null else List(raw.getJSONArray("defects").length()) { index0 -> VastuCatalogReferenceDataDefectsItem((raw.getJSONArray("defects").get(index0) as JSONObject)) }
    val remedyCount: Int?
        get() = if (!raw.has("remedyCount") || raw.isNull("remedyCount")) null else raw.getInt("remedyCount")
    val remedies: List<VastuCatalogReferenceDataRemediesItem>?
        get() = if (!raw.has("remedies") || raw.isNull("remedies")) null else List(raw.getJSONArray("remedies").length()) { index0 -> VastuCatalogReferenceDataRemediesItem((raw.getJSONArray("remedies").get(index0) as JSONObject)) }
    val featureCount: Int?
        get() = if (!raw.has("featureCount") || raw.isNull("featureCount")) null else raw.getInt("featureCount")
    val features: List<JSONObject>?
        get() = if (!raw.has("features") || raw.isNull("features")) null else List(raw.getJSONArray("features").length()) { index0 -> (raw.getJSONArray("features").get(index0) as JSONObject) }
    val rangeClassification: String?
        get() = if (!raw.has("rangeClassification") || raw.isNull("rangeClassification")) null else raw.getString("rangeClassification")
    val rangeSource: String?
        get() = if (!raw.has("rangeSource") || raw.isNull("rangeSource")) null else raw.getString("rangeSource")
    val sources: List<String>?
        get() = if (!raw.has("sources") || raw.isNull("sources")) null else List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val referenceVersion: String
        get() = raw.getString("referenceVersion")
}

data class VastuComplianceIndexData(override val raw: JSONObject) : VastuData {
    val score: Double
        get() = raw.getDouble("score")
    val complianceIndex: String
        get() = raw.getString("complianceIndex")
    val drivingDefects: List<VastuComplianceIndexDataDrivingDefectsItem>
        get() = List(raw.getJSONArray("drivingDefects").length()) { index0 -> VastuComplianceIndexDataDrivingDefectsItem((raw.getJSONArray("drivingDefects").get(index0) as JSONObject)) }
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val basis: String?
        get() = if (!raw.has("basis") || raw.isNull("basis")) null else raw.getString("basis")
    val defectsSummary: JSONObject?
        get() = if (!raw.has("defectsSummary") || raw.isNull("defectsSummary")) null else raw.getJSONObject("defectsSummary")
    val indexLabel: String?
        get() = if (!raw.has("indexLabel") || raw.isNull("indexLabel")) null else raw.getString("indexLabel")
    val indexScale: List<JSONObject>?
        get() = if (!raw.has("indexScale") || raw.isNull("indexScale")) null else List(raw.getJSONArray("indexScale").length()) { index0 -> (raw.getJSONArray("indexScale").get(index0) as JSONObject) }
    val indexScaleNote: String?
        get() = if (!raw.has("indexScaleNote") || raw.isNull("indexScaleNote")) null else raw.getString("indexScaleNote")
    val indexType: String?
        get() = if (!raw.has("indexType") || raw.isNull("indexType")) null else raw.getString("indexType")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val verdict: String?
        get() = if (!raw.has("verdict") || raw.isNull("verdict")) null else raw.getString("verdict")
    val scoring: VastuComplianceIndexDataScoring
        get() = VastuComplianceIndexDataScoring(raw.getJSONObject("scoring"))
}

data class VastuDetailedFloorPlanAuditData(override val raw: JSONObject) : VastuData {
    val score: Double
        get() = raw.getDouble("score")
    val grade: String
        get() = raw.getString("grade")
    val totalRooms: Int
        get() = raw.getInt("totalRooms")
    val prescribedCount: Int
        get() = raw.getInt("prescribedCount")
    val defects: List<VastuDetailedFloorPlanAuditDataDefectsItem>
        get() = List(raw.getJSONArray("defects").length()) { index0 -> VastuDetailedFloorPlanAuditDataDefectsItem((raw.getJSONArray("defects").get(index0) as JSONObject)) }
    val devataHeatmap: List<VastuDetailedFloorPlanAuditDataDevataHeatmapItem>
        get() = List(raw.getJSONArray("devataHeatmap").length()) { index0 -> VastuDetailedFloorPlanAuditDataDevataHeatmapItem((raw.getJSONArray("devataHeatmap").get(index0) as JSONObject)) }
    val mandalaProjection: JSONObject?
        get() = if (!raw.has("mandalaProjection") || raw.isNull("mandalaProjection")) null else raw.getJSONObject("mandalaProjection")
    val remediationOrder: List<VastuDetailedFloorPlanAuditDataRemediationOrderItem>
        get() = List(raw.getJSONArray("remediationOrder").length()) { index0 -> VastuDetailedFloorPlanAuditDataRemediationOrderItem((raw.getJSONArray("remediationOrder").get(index0) as JSONObject)) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val classification: String
        get() = raw.getString("classification")
    val bearingAssumedNorth: Boolean?
        get() = if (!raw.has("bearingAssumedNorth") || raw.isNull("bearingAssumedNorth")) null else raw.getBoolean("bearingAssumedNorth")
    val gradeScale: JSONObject?
        get() = if (!raw.has("gradeScale") || raw.isNull("gradeScale")) null else raw.getJSONObject("gradeScale")
    val scoring: VastuDetailedFloorPlanAuditDataScoring
        get() = VastuDetailedFloorPlanAuditDataScoring(raw.getJSONObject("scoring"))
    val completeness: VastuDetailedFloorPlanAuditDataCompleteness
        get() = VastuDetailedFloorPlanAuditDataCompleteness(raw.getJSONObject("completeness"))
}

data class VastuDirectionCorrectData(override val raw: JSONObject) : VastuData {
    val input: JSONObject
        get() = raw.getJSONObject("input")
    val magneticBearingDeg: Double?
        get() = if (!raw.has("magneticBearingDeg") || raw.isNull("magneticBearingDeg")) null else raw.getDouble("magneticBearingDeg")
    val declinationDeg: Double
        get() = raw.getDouble("declinationDeg")
    val trueBearingDeg: Double?
        get() = if (!raw.has("trueBearingDeg") || raw.isNull("trueBearingDeg")) null else raw.getDouble("trueBearingDeg")
    val correctedZone: String
        get() = raw.getString("correctedZone")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val correctedZoneIsMagnetic: Boolean?
        get() = if (!raw.has("correctedZoneIsMagnetic") || raw.isNull("correctedZoneIsMagnetic")) null else raw.getBoolean("correctedZoneIsMagnetic")
    val declinationCoverage: String?
        get() = if (!raw.has("declinationCoverage") || raw.isNull("declinationCoverage")) null else raw.getString("declinationCoverage")
}

data class VastuDirectionDeclinationData(override val raw: JSONObject) : VastuData {
    val lat: Double
        get() = raw.getDouble("lat")
    val lon: Double
        get() = raw.getDouble("lon")
    val date: String
        get() = raw.getString("date")
    val declinationDeg: Double
        get() = raw.getDouble("declinationDeg")
    val interpretation: String
        get() = raw.getString("interpretation")
    val gridEpoch: String
        get() = raw.getString("gridEpoch")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val classification: String
        get() = raw.getString("classification")
    val declinationCoverage: String?
        get() = if (!raw.has("declinationCoverage") || raw.isNull("declinationCoverage")) null else raw.getString("declinationCoverage")
}

data class VastuDirections32ReferenceData(override val raw: JSONObject) : VastuData {
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val padaCount: Int
        get() = raw.getInt("padaCount")
    val padaWidthDeg: Double?
        get() = if (!raw.has("padaWidthDeg") || raw.isNull("padaWidthDeg")) null else raw.getDouble("padaWidthDeg")
    val auspiciousCount: Int?
        get() = if (!raw.has("auspiciousCount") || raw.isNull("auspiciousCount")) null else raw.getInt("auspiciousCount")
    val avoidCount: Int?
        get() = if (!raw.has("avoidCount") || raw.isNull("avoidCount")) null else raw.getInt("avoidCount")
    val classicalDoorScheme: JSONObject?
        get() = if (!raw.has("classicalDoorScheme") || raw.isNull("classicalDoorScheme")) null else raw.getJSONObject("classicalDoorScheme")
    val padas: List<JSONObject>
        get() = List(raw.getJSONArray("padas").length()) { index0 -> (raw.getJSONArray("padas").get(index0) as JSONObject) }
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val referenceVersion: String
        get() = raw.getString("referenceVersion")
}

data class VastuDirectionsReferenceData(override val raw: JSONObject) : VastuData {
    val directionCount: Int
        get() = raw.getInt("directionCount")
    val directions: List<VastuDirectionsReferenceDataDirectionsItem>
        get() = List(raw.getJSONArray("directions").length()) { index0 -> VastuDirectionsReferenceDataDirectionsItem((raw.getJSONArray("directions").get(index0) as JSONObject)) }
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val sources: List<String>?
        get() = if (!raw.has("sources") || raw.isNull("sources")) null else List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val sectorWidthDeg: Double?
        get() = if (!raw.has("sectorWidthDeg") || raw.isNull("sectorWidthDeg")) null else raw.getDouble("sectorWidthDeg")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val referenceVersion: String
        get() = raw.getString("referenceVersion")
}

data class VastuElementBalanceData(override val raw: JSONObject) : VastuData {
    val derivedFrom: String
        get() = raw.getString("derivedFrom")
    val deficientElements: List<String>
        get() = List(raw.getJSONArray("deficientElements").length()) { index0 -> (raw.getJSONArray("deficientElements").get(index0) as String) }
    val excessElements: List<String>
        get() = List(raw.getJSONArray("excessElements").length()) { index0 -> (raw.getJSONArray("excessElements").get(index0) as String) }
    val remedies: List<JSONObject>
        get() = List(raw.getJSONArray("remedies").length()) { index0 -> (raw.getJSONArray("remedies").get(index0) as JSONObject) }
    val balanced: Boolean
        get() = raw.getBoolean("balanced")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val summary: String?
        get() = if (!raw.has("summary") || raw.isNull("summary")) null else raw.getString("summary")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
}

data class VastuElementDistributionData(override val raw: JSONObject) : VastuData {
    val elementDistribution: List<JSONObject>
        get() = List(raw.getJSONArray("elementDistribution").length()) { index0 -> (raw.getJSONArray("elementDistribution").get(index0) as JSONObject) }
    val idealModel: JSONObject
        get() = raw.getJSONObject("idealModel")
    val dominantElement: String
        get() = raw.getString("dominantElement")
    val deficientElements: List<String>
        get() = List(raw.getJSONArray("deficientElements").length()) { index0 -> (raw.getJSONArray("deficientElements").get(index0) as String) }
    val excessElements: List<String>
        get() = List(raw.getJSONArray("excessElements").length()) { index0 -> (raw.getJSONArray("excessElements").get(index0) as String) }
    val zoneBreakdown: List<JSONObject>
        get() = List(raw.getJSONArray("zoneBreakdown").length()) { index0 -> (raw.getJSONArray("zoneBreakdown").get(index0) as JSONObject) }
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val totalRooms: Double?
        get() = if (!raw.has("totalRooms") || raw.isNull("totalRooms")) null else raw.getDouble("totalRooms")
    val weightingBasis: String?
        get() = if (!raw.has("weightingBasis") || raw.isNull("weightingBasis")) null else raw.getString("weightingBasis")
}

data class VastuEntrancePadaData(override val raw: JSONObject) : VastuData {
    val doorXY: List<Double>
        get() = List(raw.getJSONArray("doorXY").length()) { index0 -> (raw.getJSONArray("doorXY").get(index0) as Number).toDouble() }
    val plotCentroid: List<Double>
        get() = List(raw.getJSONArray("plotCentroid").length()) { index0 -> (raw.getJSONArray("plotCentroid").get(index0) as Number).toDouble() }
    val rawBearingDeg: Double
        get() = raw.getDouble("rawBearingDeg")
    val trueBearingDeg: Double
        get() = raw.getDouble("trueBearingDeg")
    val pada: VastuEntrancePadaDataPada
        get() = VastuEntrancePadaDataPada(raw.getJSONObject("pada"))
    val edgeRefined: Boolean
        get() = raw.getBoolean("edgeRefined")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
}

data class VastuEntranceRecommendData(override val raw: JSONObject) : VastuData {
    val facing: JSONObject
        get() = raw.getJSONObject("facing")
    val bestEntrancePada: JSONObject
        get() = raw.getJSONObject("bestEntrancePada")
    val recommendedPadas: List<JSONObject>
        get() = List(raw.getJSONArray("recommendedPadas").length()) { index0 -> (raw.getJSONArray("recommendedPadas").get(index0) as JSONObject) }
    val avoidPadas: List<JSONObject>
        get() = List(raw.getJSONArray("avoidPadas").length()) { index0 -> (raw.getJSONArray("avoidPadas").get(index0) as JSONObject) }
    val facingCaution: JSONObject?
        get() = if (!raw.has("facingCaution") || raw.isNull("facingCaution")) null else raw.getJSONObject("facingCaution")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val poojaPrescribedHere: JSONObject?
        get() = if (!raw.has("poojaPrescribedHere") || raw.isNull("poojaPrescribedHere")) null else raw.getJSONObject("poojaPrescribedHere")
    val prescribedRoomsAtFacing: List<String>?
        get() = if (!raw.has("prescribedRoomsAtFacing") || raw.isNull("prescribedRoomsAtFacing")) null else List(raw.getJSONArray("prescribedRoomsAtFacing").length()) { index0 -> (raw.getJSONArray("prescribedRoomsAtFacing").get(index0) as String) }
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
}

data class VastuFloorPlanAuditData(override val raw: JSONObject) : VastuData {
    val score: Double
        get() = raw.getDouble("score")
    val grade: String
        get() = raw.getString("grade")
    val totalRooms: Int
        get() = raw.getInt("totalRooms")
    val prescribedCount: Int
        get() = raw.getInt("prescribedCount")
    val defects: List<VastuFloorPlanAuditDataDefectsItem>
        get() = List(raw.getJSONArray("defects").length()) { index0 -> VastuFloorPlanAuditDataDefectsItem((raw.getJSONArray("defects").get(index0) as JSONObject)) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val classification: String
        get() = raw.getString("classification")
    val gradeScale: JSONObject?
        get() = if (!raw.has("gradeScale") || raw.isNull("gradeScale")) null else raw.getJSONObject("gradeScale")
    val scoring: VastuFloorPlanAuditDataScoring
        get() = VastuFloorPlanAuditDataScoring(raw.getJSONObject("scoring"))
    val textParse: VastuFloorPlanAuditDataTextParse?
        get() = if (!raw.has("textParse") || raw.isNull("textParse")) null else VastuFloorPlanAuditDataTextParse(raw.getJSONObject("textParse"))
}

data class VastuFloorRulesData(override val raw: JSONObject) : VastuData {
    val masterBedroomFloor: Int
        get() = raw.getInt("masterBedroomFloor")
    val floorRules: List<JSONObject>
        get() = List(raw.getJSONArray("floorRules").length()) { index0 -> (raw.getJSONArray("floorRules").get(index0) as JSONObject) }
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val principle: String?
        get() = if (!raw.has("principle") || raw.isNull("principle")) null else raw.getString("principle")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuFusionChartData(override val raw: JSONObject) : VastuData {
    val ascendant: JSONObject
        get() = raw.getJSONObject("ascendant")
    val grahaDirections: List<JSONObject>
        get() = List(raw.getJSONArray("grahaDirections").length()) { index0 -> (raw.getJSONArray("grahaDirections").get(index0) as JSONObject) }
    val favourableDirections: List<JSONObject>
        get() = List(raw.getJSONArray("favourableDirections").length()) { index0 -> (raw.getJSONArray("favourableDirections").get(index0) as JSONObject) }
    val cautionDirections: List<String>
        get() = List(raw.getJSONArray("cautionDirections").length()) { index0 -> (raw.getJSONArray("cautionDirections").get(index0) as String) }
    val methodology: JSONObject
        get() = raw.getJSONObject("methodology")
    val summary: String
        get() = raw.getString("summary")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
}

data class VastuLevelAnalysisData(override val raw: JSONObject) : VastuData {
    val idealLevels: List<JSONObject>
        get() = List(raw.getJSONArray("idealLevels").length()) { index0 -> (raw.getJSONArray("idealLevels").get(index0) as JSONObject) }
    val observedAnalysis: JSONObject?
        get() = if (!raw.has("observedAnalysis") || raw.isNull("observedAnalysis")) null else raw.getJSONObject("observedAnalysis")
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val idealOrdering: String?
        get() = if (!raw.has("idealOrdering") || raw.isNull("idealOrdering")) null else raw.getString("idealOrdering")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val principle: String?
        get() = if (!raw.has("principle") || raw.isNull("principle")) null else raw.getString("principle")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuMainGateData(override val raw: JSONObject) : VastuData {
    val facing: String
        get() = raw.getString("facing")
    val padaScheme: String
        get() = raw.getString("padaScheme")
    val prescribedPadas: List<Int>
        get() = List(raw.getJSONArray("prescribedPadas").length()) { index0 -> (raw.getJSONArray("prescribedPadas").get(index0) as Number).toInt() }
    val rule: String
        get() = raw.getString("rule")
    val remedy: String
        get() = raw.getString("remedy")
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val classification: String
        get() = raw.getString("classification")
    val padaVerdict: JSONObject?
        get() = if (!raw.has("padaVerdict") || raw.isNull("padaVerdict")) null else raw.getJSONObject("padaVerdict")
    val feature: String?
        get() = if (!raw.has("feature") || raw.isNull("feature")) null else raw.getString("feature")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
}

data class VastuMandalaProjectionData(override val raw: JSONObject) : VastuData {
    val cells: List<JSONObject>
        get() = List(raw.getJSONArray("cells").length()) { index0 -> (raw.getJSONArray("cells").get(index0) as JSONObject) }
    val plotCentroid: List<Double>
        get() = List(raw.getJSONArray("plotCentroid").length()) { index0 -> (raw.getJSONArray("plotCentroid").get(index0) as Number).toDouble() }
    val bearingDeg: Double
        get() = raw.getDouble("bearingDeg")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val bearingAssumedNorth: Boolean?
        get() = if (!raw.has("bearingAssumedNorth") || raw.isNull("bearingAssumedNorth")) null else raw.getBoolean("bearingAssumedNorth")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val computed: Boolean?
        get() = if (!raw.has("computed") || raw.isNull("computed")) null else raw.getBoolean("computed")
}

data class VastuMandalaReferenceData(override val raw: JSONObject) : VastuData {
    val zoneCount: Int?
        get() = if (!raw.has("zoneCount") || raw.isNull("zoneCount")) null else raw.getInt("zoneCount")
    val zones: List<VastuMandalaReferenceDataZonesItem>?
        get() = if (!raw.has("zones") || raw.isNull("zones")) null else List(raw.getJSONArray("zones").length()) { index0 -> VastuMandalaReferenceDataZonesItem((raw.getJSONArray("zones").get(index0) as JSONObject)) }
    val devataCount: Int?
        get() = if (!raw.has("devataCount") || raw.isNull("devataCount")) null else raw.getInt("devataCount")
    val devatas: List<JSONObject>?
        get() = if (!raw.has("devatas") || raw.isNull("devatas")) null else List(raw.getJSONArray("devatas").length()) { index0 -> (raw.getJSONArray("devatas").get(index0) as JSONObject) }
    val cells: List<VastuMandalaReferenceDataCellsItem>?
        get() = if (!raw.has("cells") || raw.isNull("cells")) null else List(raw.getJSONArray("cells").length()) { index0 -> VastuMandalaReferenceDataCellsItem((raw.getJSONArray("cells").get(index0) as JSONObject)) }
    val padaCount: Int?
        get() = if (!raw.has("padaCount") || raw.isNull("padaCount")) null else raw.getInt("padaCount")
    val grid: JSONObject?
        get() = if (!raw.has("grid") || raw.isNull("grid")) null else raw.getJSONObject("grid")
    val sources: List<String>?
        get() = if (!raw.has("sources") || raw.isNull("sources")) null else List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val bearingDeg: Double?
        get() = if (!raw.has("bearingDeg") || raw.isNull("bearingDeg")) null else raw.getDouble("bearingDeg")
    val brahmasthanPadas: List<Double>?
        get() = if (!raw.has("brahmasthanPadas") || raw.isNull("brahmasthanPadas")) null else List(raw.getJSONArray("brahmasthanPadas").length()) { index0 -> (raw.getJSONArray("brahmasthanPadas").get(index0) as Number).toDouble() }
    val classBreakdown: JSONObject?
        get() = if (!raw.has("classBreakdown") || raw.isNull("classBreakdown")) null else raw.getJSONObject("classBreakdown")
    val devataSource: String?
        get() = if (!raw.has("devataSource") || raw.isNull("devataSource")) null else raw.getString("devataSource")
    val devataVerified: Boolean?
        get() = if (!raw.has("devataVerified") || raw.isNull("devataVerified")) null else raw.getBoolean("devataVerified")
    val mandala: String?
        get() = if (!raw.has("mandala") || raw.isNull("mandala")) null else raw.getString("mandala")
    val plotCentroid: List<Double>?
        get() = if (!raw.has("plotCentroid") || raw.isNull("plotCentroid")) null else List(raw.getJSONArray("plotCentroid").length()) { index0 -> (raw.getJSONArray("plotCentroid").get(index0) as Number).toDouble() }
    val projected: Boolean?
        get() = if (!raw.has("projected") || raw.isNull("projected")) null else raw.getBoolean("projected")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val referenceVersion: String
        get() = raw.getString("referenceVersion")
}

data class VastuObstructionData(override val raw: JSONObject) : VastuData {
    val input: JSONObject
        get() = raw.getJSONObject("input")
    val matchedFeature: String
        get() = raw.getString("matchedFeature")
    val effect: String
        get() = raw.getString("effect")
    val rangeChecked: Boolean
        get() = raw.getBoolean("rangeChecked")
    val inRange: Boolean?
        get() = if (!raw.has("inRange") || raw.isNull("inRange")) null else raw.getBoolean("inRange")
    val houseHeightMultiples: Double?
        get() = if (!raw.has("houseHeightMultiples") || raw.isNull("houseHeightMultiples")) null else raw.getDouble("houseHeightMultiples")
    val verdict: String
        get() = raw.getString("verdict")
    val note: String
        get() = raw.getString("note")
    val source: String
        get() = raw.getString("source")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val classification: String
        get() = raw.getString("classification")
    val rangeClassification: String?
        get() = if (!raw.has("rangeClassification") || raw.isNull("rangeClassification")) null else raw.getString("rangeClassification")
    val rangeSource: String?
        get() = if (!raw.has("rangeSource") || raw.isNull("rangeSource")) null else raw.getString("rangeSource")
}

data class VastuOverallScoreData(override val raw: JSONObject) : VastuData {
    val score: Double
        get() = raw.getDouble("score")
    val grade: String
        get() = raw.getString("grade")
    val placements: List<VastuOverallScoreDataPlacementsItem>
        get() = List(raw.getJSONArray("placements").length()) { index0 -> VastuOverallScoreDataPlacementsItem((raw.getJSONArray("placements").get(index0) as JSONObject)) }
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val basis: String?
        get() = if (!raw.has("basis") || raw.isNull("basis")) null else raw.getString("basis")
    val formula: String?
        get() = if (!raw.has("formula") || raw.isNull("formula")) null else raw.getString("formula")
    val gradeLabel: String?
        get() = if (!raw.has("gradeLabel") || raw.isNull("gradeLabel")) null else raw.getString("gradeLabel")
    val indexType: String?
        get() = if (!raw.has("indexType") || raw.isNull("indexType")) null else raw.getString("indexType")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val maxScore: Double?
        get() = if (!raw.has("maxScore") || raw.isNull("maxScore")) null else raw.getDouble("maxScore")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val scoreBreakdown: JSONObject?
        get() = if (!raw.has("scoreBreakdown") || raw.isNull("scoreBreakdown")) null else raw.getJSONObject("scoreBreakdown")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val verdict: String?
        get() = if (!raw.has("verdict") || raw.isNull("verdict")) null else raw.getString("verdict")
    val scoring: VastuOverallScoreDataScoring
        get() = VastuOverallScoreDataScoring(raw.getJSONObject("scoring"))
}

data class VastuPlacementData(override val raw: JSONObject) : VastuData {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val feature: String
        get() = raw.getString("feature")
    val proposedZone: String
        get() = raw.getString("proposedZone")
    val verdict: String
        get() = raw.getString("verdict")
    val severity: String
        get() = raw.getString("severity")
    val idealZones: List<String>
        get() = List(raw.getJSONArray("idealZones").length()) { index0 -> (raw.getJSONArray("idealZones").get(index0) as String) }
    val acceptableZones: List<String>
        get() = List(raw.getJSONArray("acceptableZones").length()) { index0 -> (raw.getJSONArray("acceptableZones").get(index0) as String) }
    val forbiddenZones: List<String>
        get() = List(raw.getJSONArray("forbiddenZones").length()) { index0 -> (raw.getJSONArray("forbiddenZones").get(index0) as String) }
    val deity: String
        get() = raw.getString("deity")
    val deityClassification: String?
        get() = if (!raw.has("deityClassification") || raw.isNull("deityClassification")) null else raw.getString("deityClassification")
    val deitySource: String?
        get() = if (!raw.has("deitySource") || raw.isNull("deitySource")) null else raw.getString("deitySource")
    val element: String
        get() = raw.getString("element")
    val elementVerified: Boolean
        get() = raw.getBoolean("elementVerified")
    val elementSource: String?
        get() = if (!raw.has("elementSource") || raw.isNull("elementSource")) null else raw.getString("elementSource")
    val principle: String
        get() = raw.getString("principle")
    val reason: String
        get() = raw.getString("reason")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val meta: JSONObject
        get() = raw.getJSONObject("meta")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuPlanAuditData(override val raw: JSONObject) : VastuData {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val input: JSONObject
        get() = raw.getJSONObject("input")
    val facing: JSONObject
        get() = raw.getJSONObject("facing")
    val plotShape: JSONObject
        get() = raw.getJSONObject("plotShape")
    val overallScore: Double
        get() = raw.getDouble("overallScore")
    val grade: String
        get() = raw.getString("grade")
    val summary: String
        get() = raw.getString("summary")
    val zoneCompliance: List<JSONObject>
        get() = List(raw.getJSONArray("zoneCompliance").length()) { index0 -> (raw.getJSONArray("zoneCompliance").get(index0) as JSONObject) }
    val roomByRoom: List<VastuPlanAuditDataRoomByRoomItem>
        get() = List(raw.getJSONArray("roomByRoom").length()) { index0 -> VastuPlanAuditDataRoomByRoomItem((raw.getJSONArray("roomByRoom").get(index0) as JSONObject)) }
    val defects: List<VastuPlanAuditDataDefectsItem>
        get() = List(raw.getJSONArray("defects").length()) { index0 -> VastuPlanAuditDataDefectsItem((raw.getJSONArray("defects").get(index0) as JSONObject)) }
    val remedies: List<VastuPlanAuditDataRemediesItem>
        get() = List(raw.getJSONArray("remedies").length()) { index0 -> VastuPlanAuditDataRemediesItem((raw.getJSONArray("remedies").get(index0) as JSONObject)) }
    val elementBalance: JSONObject
        get() = raw.getJSONObject("elementBalance")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val provenance: JSONObject
        get() = raw.getJSONObject("provenance")
    val meta: JSONObject
        get() = raw.getJSONObject("meta")
    val printReady: JSONObject?
        get() = if (!raw.has("printReady") || raw.isNull("printReady")) null else raw.getJSONObject("printReady")
    val tracedGeometry: JSONObject?
        get() = if (!raw.has("tracedGeometry") || raw.isNull("tracedGeometry")) null else raw.getJSONObject("tracedGeometry")
    val gradeLabel: String?
        get() = if (!raw.has("gradeLabel") || raw.isNull("gradeLabel")) null else raw.getString("gradeLabel")
    val scoreDisclaimer: String?
        get() = if (!raw.has("scoreDisclaimer") || raw.isNull("scoreDisclaimer")) null else raw.getString("scoreDisclaimer")
    val artifact: VastuPlanAuditDataArtifact?
        get() = if (!raw.has("artifact") || raw.isNull("artifact")) null else VastuPlanAuditDataArtifact(raw.getJSONObject("artifact"))
}

data class VastuPlanGenerateData(override val raw: JSONObject) : VastuData {
    val plot: JSONObject
        get() = raw.getJSONObject("plot")
    val entrance: JSONObject
        get() = raw.getJSONObject("entrance")
    val rooms: List<JSONObject>
        get() = List(raw.getJSONArray("rooms").length()) { index0 -> (raw.getJSONArray("rooms").get(index0) as JSONObject) }
    val mandala: JSONObject
        get() = raw.getJSONObject("mandala")
    val compliance: JSONObject
        get() = raw.getJSONObject("compliance")
    val openings: JSONObject
        get() = raw.getJSONObject("openings")
    val svg: String?
        get() = if (!raw.has("svg") || raw.isNull("svg")) null else raw.getString("svg")
    val variants: List<JSONObject>
        get() = List(raw.getJSONArray("variants").length()) { index0 -> (raw.getJSONArray("variants").get(index0) as JSONObject) }
    val recommendedVariant: String
        get() = raw.getString("recommendedVariant")
    val architecturalRooms: List<JSONObject>?
        get() = if (!raw.has("architecturalRooms") || raw.isNull("architecturalRooms")) null else List(raw.getJSONArray("architecturalRooms").length()) { index0 -> (raw.getJSONArray("architecturalRooms").get(index0) as JSONObject) }
    val derivedRoomProgramme: List<JSONObject>?
        get() = if (!raw.has("derivedRoomProgramme") || raw.isNull("derivedRoomProgramme")) null else List(raw.getJSONArray("derivedRoomProgramme").length()) { index0 -> (raw.getJSONArray("derivedRoomProgramme").get(index0) as JSONObject) }
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val requirements: JSONObject?
        get() = if (!raw.has("requirements") || raw.isNull("requirements")) null else raw.getJSONObject("requirements")
    val roomProgrammeNote: String?
        get() = if (!raw.has("roomProgrammeNote") || raw.isNull("roomProgrammeNote")) null else raw.getString("roomProgrammeNote")
    val sources: List<JSONObject>?
        get() = if (!raw.has("sources") || raw.isNull("sources")) null else List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val variantCount: Double?
        get() = if (!raw.has("variantCount") || raw.isNull("variantCount")) null else raw.getDouble("variantCount")
    val variantNote: String?
        get() = if (!raw.has("variantNote") || raw.isNull("variantNote")) null else raw.getString("variantNote")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
    val floors: List<JSONObject>?
        get() = if (!raw.has("floors") || raw.isNull("floors")) null else List(raw.getJSONArray("floors").length()) { index0 -> (raw.getJSONArray("floors").get(index0) as JSONObject) }
    val core: JSONObject?
        get() = if (!raw.has("core") || raw.isNull("core")) null else raw.getJSONObject("core")
    val verticalChecks: List<JSONObject>?
        get() = if (!raw.has("verticalChecks") || raw.isNull("verticalChecks")) null else List(raw.getJSONArray("verticalChecks").length()) { index0 -> (raw.getJSONArray("verticalChecks").get(index0) as JSONObject) }
    val floorNote: String?
        get() = if (!raw.has("floorNote") || raw.isNull("floorNote")) null else raw.getString("floorNote")
}

data class VastuPlanOptimizeData(override val raw: JSONObject) : VastuData {
    val before: JSONObject
        get() = raw.getJSONObject("before")
    val after: JSONObject
        get() = raw.getJSONObject("after")
    val improvement: JSONObject
        get() = raw.getJSONObject("improvement")
    val moves: List<JSONObject>
        get() = List(raw.getJSONArray("moves").length()) { index0 -> (raw.getJSONArray("moves").get(index0) as JSONObject) }
    val mandala: JSONObject
        get() = raw.getJSONObject("mandala")
    val svg: String?
        get() = if (!raw.has("svg") || raw.isNull("svg")) null else raw.getString("svg")
    val compliance: JSONObject?
        get() = if (!raw.has("compliance") || raw.isNull("compliance")) null else raw.getJSONObject("compliance")
    val entrance: JSONObject?
        get() = if (!raw.has("entrance") || raw.isNull("entrance")) null else raw.getJSONObject("entrance")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val openings: JSONObject?
        get() = if (!raw.has("openings") || raw.isNull("openings")) null else raw.getJSONObject("openings")
    val plot: JSONObject?
        get() = if (!raw.has("plot") || raw.isNull("plot")) null else raw.getJSONObject("plot")
    val sources: List<JSONObject>?
        get() = if (!raw.has("sources") || raw.isNull("sources")) null else List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
}

data class VastuPlotExtensionsCutsData(override val raw: JSONObject) : VastuData {
    val directions: List<JSONObject>
        get() = List(raw.getJSONArray("directions").length()) { index0 -> (raw.getJSONArray("directions").get(index0) as JSONObject) }
    val extensions: List<String>
        get() = List(raw.getJSONArray("extensions").length()) { index0 -> (raw.getJSONArray("extensions").get(index0) as String) }
    val cuts: List<JSONObject>
        get() = List(raw.getJSONArray("cuts").length()) { index0 -> (raw.getJSONArray("cuts").get(index0) as JSONObject) }
    val severeCuts: List<JSONObject>
        get() = List(raw.getJSONArray("severeCuts").length()) { index0 -> (raw.getJSONArray("severeCuts").get(index0) as JSONObject) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val actualPlotArea: Double?
        get() = if (!raw.has("actualPlotArea") || raw.isNull("actualPlotArea")) null else raw.getDouble("actualPlotArea")
    val areaEfficiency: Double?
        get() = if (!raw.has("areaEfficiency") || raw.isNull("areaEfficiency")) null else raw.getDouble("areaEfficiency")
    val idealRectangleArea: Double?
        get() = if (!raw.has("idealRectangleArea") || raw.isNull("idealRectangleArea")) null else raw.getDouble("idealRectangleArea")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val provenance: JSONObject?
        get() = if (!raw.has("provenance") || raw.isNull("provenance")) null else raw.getJSONObject("provenance")
    val summary: String?
        get() = if (!raw.has("summary") || raw.isNull("summary")) null else raw.getString("summary")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val verdict: String?
        get() = if (!raw.has("verdict") || raw.isNull("verdict")) null else raw.getString("verdict")
}

data class VastuPlotOrientationData(override val raw: JSONObject) : VastuData {
    val facing: String
        get() = raw.getString("facing")
    val grade: String
        get() = raw.getString("grade")
    val doorPadaScheme: JSONObject
        get() = raw.getJSONObject("doorPadaScheme")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val auspicious: Boolean?
        get() = if (!raw.has("auspicious") || raw.isNull("auspicious")) null else raw.getBoolean("auspicious")
    val deity: String?
        get() = if (!raw.has("deity") || raw.isNull("deity")) null else raw.getString("deity")
    val facingSanskrit: String?
        get() = if (!raw.has("facingSanskrit") || raw.isNull("facingSanskrit")) null else raw.getString("facingSanskrit")
    val gradeProvenance: JSONObject?
        get() = if (!raw.has("gradeProvenance") || raw.isNull("gradeProvenance")) null else raw.getJSONObject("gradeProvenance")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val provenance: JSONObject?
        get() = if (!raw.has("provenance") || raw.isNull("provenance")) null else raw.getJSONObject("provenance")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
}

data class VastuPlotRatioData(override val raw: JSONObject) : VastuData {
    val length: Double
        get() = raw.getDouble("length")
    val width: Double
        get() = raw.getDouble("width")
    val units: String
        get() = raw.getString("units")
    val unitsNote: String
        get() = raw.getString("unitsNote")
    val lengthM: Double?
        get() = if (!raw.has("lengthM") || raw.isNull("lengthM")) null else raw.getDouble("lengthM")
    val widthM: Double?
        get() = if (!raw.has("widthM") || raw.isNull("widthM")) null else raw.getDouble("widthM")
    val ratio: Double
        get() = raw.getDouble("ratio")
    val category: String
        get() = raw.getString("category")
    val acceptable: Boolean
        get() = raw.getBoolean("acceptable")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val classicalSource: String
        get() = raw.getString("classicalSource")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val boundingFrame: String
        get() = raw.getString("boundingFrame")
}

data class VastuPlotShapeData(override val raw: JSONObject) : VastuData {
    val shape: String
        get() = raw.getString("shape")
    val vertices: Int
        get() = raw.getInt("vertices")
    val area: Double
        get() = raw.getDouble("area")
    val bboxArea: Double
        get() = raw.getDouble("bboxArea")
    val fillRatio: Double
        get() = raw.getDouble("fillRatio")
    val vastuGrade: String
        get() = raw.getString("vastuGrade")
    val notes: String
        get() = raw.getString("notes")
    val classicalSource: String
        get() = raw.getString("classicalSource")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val boundingFrame: String
        get() = raw.getString("boundingFrame")
}

data class VastuPlotSlopeData(override val raw: JSONObject) : VastuData {
    val downSlopeDirection: String
        get() = raw.getString("downSlopeDirection")
    val classicalReference: JSONObject
        get() = raw.getJSONObject("classicalReference")
    val verdict: String?
        get() = if (!raw.has("verdict") || raw.isNull("verdict")) null else raw.getString("verdict")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val auspicious: Boolean?
        get() = if (!raw.has("auspicious") || raw.isNull("auspicious")) null else raw.getBoolean("auspicious")
    val effect: String?
        get() = if (!raw.has("effect") || raw.isNull("effect")) null else raw.getString("effect")
    val effectProvenance: JSONObject?
        get() = if (!raw.has("effectProvenance") || raw.isNull("effectProvenance")) null else raw.getJSONObject("effectProvenance")
    val idealRule: String?
        get() = if (!raw.has("idealRule") || raw.isNull("idealRule")) null else raw.getString("idealRule")
    val idealRuleProvenance: JSONObject?
        get() = if (!raw.has("idealRuleProvenance") || raw.isNull("idealRuleProvenance")) null else raw.getJSONObject("idealRuleProvenance")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val provenance: JSONObject?
        get() = if (!raw.has("provenance") || raw.isNull("provenance")) null else raw.getJSONObject("provenance")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
}

data class VastuRemedyComparisonData(override val raw: JSONObject) : VastuData {
    val before: VastuRemedyComparisonDataBefore
        get() = VastuRemedyComparisonDataBefore(raw.getJSONObject("before"))
    val after: VastuRemedyComparisonDataAfter
        get() = VastuRemedyComparisonDataAfter(raw.getJSONObject("after"))
    val scoreDelta: Double
        get() = raw.getDouble("scoreDelta")
    val scoring: JSONObject
        get() = raw.getJSONObject("scoring")
    val verdict: String
        get() = raw.getString("verdict")
    val remediesApplied: List<JSONObject>
        get() = List(raw.getJSONArray("remediesApplied").length()) { index0 -> (raw.getJSONArray("remediesApplied").get(index0) as JSONObject) }
    val roomChanges: List<JSONObject>
        get() = List(raw.getJSONArray("roomChanges").length()) { index0 -> (raw.getJSONArray("roomChanges").get(index0) as JSONObject) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val verified: Boolean?
        get() = if (!raw.has("verified") || raw.isNull("verified")) null else raw.getBoolean("verified")
}

data class VastuRoadOrientationData(override val raw: JSONObject) : VastuData {
    val roadAnalysis: List<JSONObject>
        get() = List(raw.getJSONArray("roadAnalysis").length()) { index0 -> (raw.getJSONArray("roadAnalysis").get(index0) as JSONObject) }
    val beneficRoads: List<String>
        get() = List(raw.getJSONArray("beneficRoads").length()) { index0 -> (raw.getJSONArray("beneficRoads").get(index0) as String) }
    val cautionRoads: List<String>
        get() = List(raw.getJSONArray("cautionRoads").length()) { index0 -> (raw.getJSONArray("cautionRoads").get(index0) as String) }
    val veedhiShoola: JSONObject
        get() = raw.getJSONObject("veedhiShoola")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val chaturMukhi: Boolean?
        get() = if (!raw.has("chaturMukhi") || raw.isNull("chaturMukhi")) null else raw.getBoolean("chaturMukhi")
    val hasNorthOrEastRoad: Boolean?
        get() = if (!raw.has("hasNorthOrEastRoad") || raw.isNull("hasNorthOrEastRoad")) null else raw.getBoolean("hasNorthOrEastRoad")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val provenance: JSONObject?
        get() = if (!raw.has("provenance") || raw.isNull("provenance")) null else raw.getJSONObject("provenance")
    val summary: String?
        get() = if (!raw.has("summary") || raw.isNull("summary")) null else raw.getString("summary")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val verdict: String?
        get() = if (!raw.has("verdict") || raw.isNull("verdict")) null else raw.getString("verdict")
}

data class VastuRoomData(override val raw: JSONObject) : VastuData {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val room: String
        get() = raw.getString("room")
    val placement: JSONObject
        get() = raw.getJSONObject("placement")
    val verdict: String
        get() = raw.getString("verdict")
    val severity: String
        get() = raw.getString("severity")
    val idealZones: List<String>
        get() = List(raw.getJSONArray("idealZones").length()) { index0 -> (raw.getJSONArray("idealZones").get(index0) as String) }
    val acceptableZones: List<String>
        get() = List(raw.getJSONArray("acceptableZones").length()) { index0 -> (raw.getJSONArray("acceptableZones").get(index0) as String) }
    val forbiddenZones: List<String>
        get() = List(raw.getJSONArray("forbiddenZones").length()) { index0 -> (raw.getJSONArray("forbiddenZones").get(index0) as String) }
    val defect: JSONObject?
        get() = if (!raw.has("defect") || raw.isNull("defect")) null else raw.getJSONObject("defect")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyType: String?
        get() = if (!raw.has("remedyType") || raw.isNull("remedyType")) null else raw.getString("remedyType")
    val guidance: String?
        get() = if (!raw.has("guidance") || raw.isNull("guidance")) null else raw.getString("guidance")
    val citation: JSONObject
        get() = raw.getJSONObject("citation")
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val meta: JSONObject
        get() = raw.getJSONObject("meta")
    val storageType: String?
        get() = if (!raw.has("storageType") || raw.isNull("storageType")) null else raw.getString("storageType")
    val placementVerified: Boolean?
        get() = if (!raw.has("placementVerified") || raw.isNull("placementVerified")) null else raw.getBoolean("placementVerified")
    val guidanceClassification: String?
        get() = if (!raw.has("guidanceClassification") || raw.isNull("guidanceClassification")) null else raw.getString("guidanceClassification")
}

data class VastuScanStoredData(override val raw: JSONObject) : VastuData {
    val schemaVersion: Int
        get() = raw.getInt("schemaVersion")
    val propertyId: String
        get() = raw.getString("propertyId")
    val snapshot: Any?
        get() = raw.get("snapshot").takeUnless { it == JSONObject.NULL }
    val audit: Any?
        get() = raw.get("audit").takeUnless { it == JSONObject.NULL }
    val scanQuality: Any?
        get() = if (!raw.has("scanQuality") || raw.isNull("scanQuality")) null else raw.get("scanQuality").takeUnless { it == JSONObject.NULL }
    val captureVerification: String
        get() = raw.getString("captureVerification")
    val geometryUnits: String
        get() = raw.getString("geometryUnits")
    val assessmentNote: String
        get() = raw.getString("assessmentNote")
}

data class VastuScansDeleteData(override val raw: JSONObject) : VastuData {
    val scanId: String
        get() = raw.getString("scanId")
    val deleted: Boolean
        get() = raw.getBoolean("deleted")
    val deletionScope: String
        get() = raw.getString("deletionScope")
    val persistence: String
        get() = raw.getString("persistence")
    val previewNote: String?
        get() = if (!raw.has("previewNote") || raw.isNull("previewNote")) null else raw.getString("previewNote")
}

data class VastuScansListData(override val raw: JSONObject) : VastuData {
    val scans: List<Any?>
        get() = List(raw.getJSONArray("scans").length()) { index0 -> raw.getJSONArray("scans").get(index0).takeUnless { it == JSONObject.NULL } }
    val nextCursor: String?
        get() = if (!raw.has("nextCursor") || raw.isNull("nextCursor")) null else raw.getString("nextCursor")
    val paginationNote: String?
        get() = if (!raw.has("paginationNote") || raw.isNull("paginationNote")) null else raw.getString("paginationNote")
    val persistence: String
        get() = raw.getString("persistence")
    val previewNote: String?
        get() = if (!raw.has("previewNote") || raw.isNull("previewNote")) null else raw.getString("previewNote")
}

data class VastuScansRetrieveData(override val raw: JSONObject) : VastuData {
    val scan: Any?
        get() = raw.get("scan").takeUnless { it == JSONObject.NULL }
    val persistence: String
        get() = raw.getString("persistence")
    val previewNote: String?
        get() = if (!raw.has("previewNote") || raw.isNull("previewNote")) null else raw.getString("previewNote")
}

data class VastuScansSaveData(override val raw: JSONObject) : VastuData {
    val scan: Any?
        get() = raw.get("scan").takeUnless { it == JSONObject.NULL }
    val replayed: Boolean
        get() = raw.getBoolean("replayed")
    val retentionNote: String?
        get() = if (!raw.has("retentionNote") || raw.isNull("retentionNote")) null else raw.getString("retentionNote")
    val persistence: String
        get() = raw.getString("persistence")
    val previewNote: String?
        get() = if (!raw.has("previewNote") || raw.isNull("previewNote")) null else raw.getString("previewNote")
}

data class VastuScansTimelapseData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val scans: List<Any?>
        get() = List(raw.getJSONArray("scans").length()) { index0 -> raw.getJSONArray("scans").get(index0).takeUnless { it == JSONObject.NULL } }
    val comparisonNote: String
        get() = raw.getString("comparisonNote")
    val physicalChangeVerified: Boolean
        get() = raw.getBoolean("physicalChangeVerified")
    val persistence: String
        get() = raw.getString("persistence")
    val previewNote: String?
        get() = if (!raw.has("previewNote") || raw.isNull("previewNote")) null else raw.getString("previewNote")
}

data class VastuSingleRoomAuditData(override val raw: JSONObject) : VastuData {
    val input: JSONObject
        get() = raw.getJSONObject("input")
    val compliance: String
        get() = raw.getString("compliance")
    val severity: String
        get() = raw.getString("severity")
    val recommendedZone: String?
        get() = if (!raw.has("recommendedZone") || raw.isNull("recommendedZone")) null else raw.getString("recommendedZone")
    val remedy: String
        get() = raw.getString("remedy")
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val computed: Boolean
        get() = raw.getBoolean("computed")
    val classification: String
        get() = raw.getString("classification")
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedyParams: JSONObject?
        get() = if (!raw.has("remedyParams") || raw.isNull("remedyParams")) null else raw.getJSONObject("remedyParams")
    val remedyClassification: String?
        get() = if (!raw.has("remedyClassification") || raw.isNull("remedyClassification")) null else raw.getString("remedyClassification")
    val remedySource: String?
        get() = if (!raw.has("remedySource") || raw.isNull("remedySource")) null else raw.getString("remedySource")
}

data class VastuSpecializedAuditData(override val raw: JSONObject) : VastuData {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val buildingType: String
        get() = raw.getString("buildingType")
    val score: Double
        get() = raw.getDouble("score")
    val grade: String
        get() = raw.getString("grade")
    val scoringBasis: String
        get() = raw.getString("scoringBasis")
    val auditedRooms: Int
        get() = raw.getInt("auditedRooms")
    val idealCount: Int
        get() = raw.getInt("idealCount")
    val compliantCount: Int
        get() = raw.getInt("compliantCount")
    val defectCount: Int
        get() = raw.getInt("defectCount")
    val findings: List<VastuSpecializedAuditDataFindingsItem>
        get() = List(raw.getJSONArray("findings").length()) { index0 -> VastuSpecializedAuditDataFindingsItem((raw.getJSONArray("findings").get(index0) as JSONObject)) }
    val remedies: List<JSONObject>
        get() = List(raw.getJSONArray("remedies").length()) { index0 -> (raw.getJSONArray("remedies").get(index0) as JSONObject) }
    val unknownRooms: List<JSONObject>
        get() = List(raw.getJSONArray("unknownRooms").length()) { index0 -> (raw.getJSONArray("unknownRooms").get(index0) as JSONObject) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val provenance: JSONObject
        get() = raw.getJSONObject("provenance")
    val meta: JSONObject
        get() = raw.getJSONObject("meta")
    val buildingDirection: JSONObject?
        get() = if (!raw.has("buildingDirection") || raw.isNull("buildingDirection")) null else raw.getJSONObject("buildingDirection")
}

data class VastuSunPathData(override val raw: JSONObject) : VastuData {
    val input: VastuSunPathDataInput
        get() = VastuSunPathDataInput(raw.getJSONObject("input"))
    val sunriseUtc: String
        get() = raw.getString("sunriseUtc")
    val sunriseAzimuthDeg: Double
        get() = raw.getDouble("sunriseAzimuthDeg")
    val solarNoonUtc: String
        get() = raw.getString("solarNoonUtc")
    val solarNoonAzimuthDeg: Double
        get() = raw.getDouble("solarNoonAzimuthDeg")
    val solarNoonElevationDeg: Double
        get() = raw.getDouble("solarNoonElevationDeg")
    val sunsetUtc: String
        get() = raw.getString("sunsetUtc")
    val sunsetAzimuthDeg: Double
        get() = raw.getDouble("sunsetAzimuthDeg")
    val declinationDeg: Double
        get() = raw.getDouble("declinationDeg")
    val arc: List<JSONObject>
        get() = List(raw.getJSONArray("arc").length()) { index0 -> (raw.getJSONArray("arc").get(index0) as JSONObject) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
}

data class VastuTimingData(override val raw: JSONObject) : VastuData {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val activity: String
        get() = raw.getString("activity")
    val input: JSONObject
        get() = raw.getJSONObject("input")
    val summary: JSONObject
        get() = raw.getJSONObject("summary")
    val auspiciousDates: List<JSONObject>
        get() = List(raw.getJSONArray("auspiciousDates").length()) { index0 -> (raw.getJSONArray("auspiciousDates").get(index0) as JSONObject) }
    val meta: JSONObject
        get() = raw.getJSONObject("meta")
    val guidance: JSONObject
        get() = raw.getJSONObject("guidance")
    val foundationRite: JSONObject?
        get() = if (!raw.has("foundationRite") || raw.isNull("foundationRite")) null else raw.getJSONObject("foundationRite")
}

data class VastuWallAnalysisData(override val raw: JSONObject) : VastuData {
    val idealWalls: List<JSONObject>
        get() = List(raw.getJSONArray("idealWalls").length()) { index0 -> (raw.getJSONArray("idealWalls").get(index0) as JSONObject) }
    val observedAnalysis: JSONObject?
        get() = if (!raw.has("observedAnalysis") || raw.isNull("observedAnalysis")) null else raw.getJSONObject("observedAnalysis")
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val idealOrdering: String?
        get() = if (!raw.has("idealOrdering") || raw.isNull("idealOrdering")) null else raw.getString("idealOrdering")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val principle: String?
        get() = if (!raw.has("principle") || raw.isNull("principle")) null else raw.getString("principle")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
}

data class VastuZoneReferenceData(override val raw: JSONObject) : VastuData {
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val zoneCount: Int
        get() = raw.getInt("zoneCount")
    val zones: List<JSONObject>
        get() = List(raw.getJSONArray("zones").length()) { index0 -> (raw.getJSONArray("zones").get(index0) as JSONObject) }
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val referenceVersion: String
        get() = raw.getString("referenceVersion")
}

data class VastuZoneWiseScoreData(override val raw: JSONObject) : VastuData {
    val zones: List<VastuZoneWiseScoreDataZonesItem>
        get() = List(raw.getJSONArray("zones").length()) { index0 -> VastuZoneWiseScoreDataZonesItem((raw.getJSONArray("zones").get(index0) as JSONObject)) }
    val sources: List<JSONObject>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as JSONObject) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val basis: String?
        get() = if (!raw.has("basis") || raw.isNull("basis")) null else raw.getString("basis")
    val indexType: String?
        get() = if (!raw.has("indexType") || raw.isNull("indexType")) null else raw.getString("indexType")
    val input: JSONObject?
        get() = if (!raw.has("input") || raw.isNull("input")) null else raw.getJSONObject("input")
    val meta: JSONObject?
        get() = if (!raw.has("meta") || raw.isNull("meta")) null else raw.getJSONObject("meta")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
    val overallGrade: String
        get() = raw.getString("overallGrade")
    val overallScore: Double
        get() = raw.getDouble("overallScore")
    val strongestZone: String?
        get() = if (!raw.has("strongestZone") || raw.isNull("strongestZone")) null else raw.getString("strongestZone")
    val system: String?
        get() = if (!raw.has("system") || raw.isNull("system")) null else raw.getString("system")
    val tradition: String?
        get() = if (!raw.has("tradition") || raw.isNull("tradition")) null else raw.getString("tradition")
    val weakestZone: String?
        get() = if (!raw.has("weakestZone") || raw.isNull("weakestZone")) null else raw.getString("weakestZone")
    val zoneWeightingNote: String?
        get() = if (!raw.has("zoneWeightingNote") || raw.isNull("zoneWeightingNote")) null else raw.getString("zoneWeightingNote")
    val scoring: VastuZoneWiseScoreDataScoring
        get() = VastuZoneWiseScoreDataScoring(raw.getJSONObject("scoring"))
}

data class VastuTypedResponse<Data : VastuData>(val success: Boolean, val data: Data, val raw: JSONObject) {
    val billing: JSONObject get() = raw.getJSONObject("billing")
}

data class VastuAssessmentsResponse(
    val success: Boolean,
    val data: VastuAssessmentData,
    val raw: JSONObject,
) {
    val billing: JSONObject? get() = raw.optJSONObject("billing")
}

data class VastuAssessmentsBatchResponse(
    val success: Boolean,
    val data: VastuAssessmentBatchData,
    val raw: JSONObject,
)


class VastuContract<Request : VastuRequest, Data : VastuData> internal constructor(
    val operation: VastuOperation,
    val decode: (JSONObject) -> Data,
)

object VastuContracts {
    val arCountedScanQuality: VastuContract<VastuArCountedScanQualityRequest, VastuArScanQualityData> = VastuContract(VastuOperation.ArScanQuality, ::VastuArScanQualityData)






    val arDeityIcons: VastuContract<VastuArDeityIconsRequest, VastuArDeityIconsData> = VastuContract(VastuOperation.ArDeityIcons, ::VastuArDeityIconsData)

    val arRoomCapture: VastuContract<VastuArRoomCaptureRequest, VastuArRoomCaptureData> = VastuContract(VastuOperation.ArRoomCapture, ::VastuArRoomCaptureData)

    val arYantraMeshes: VastuContract<VastuArYantraMeshesRequest, VastuArYantraMeshesData> = VastuContract(VastuOperation.ArYantraMeshes, ::VastuArYantraMeshesData)

    val arZoneTextures: VastuContract<VastuArZoneTexturesRequest, VastuArZoneTexturesData> = VastuContract(VastuOperation.ArZoneTextures, ::VastuArZoneTexturesData)

    val arAnchorRecommendations: VastuContract<VastuArAnchorRecommendationsRequest, VastuArAnchorRecommendationsData> = VastuContract(VastuOperation.ArAnchorRecommendations, ::VastuArAnchorRecommendationsData)

    val arHeatmapRaster: VastuContract<VastuArHeatmapRasterRequest, VastuArHeatmapRasterData> = VastuContract(VastuOperation.ArHeatmapRaster, ::VastuArHeatmapRasterData)


    val arScanQuality: VastuContract<VastuArScanQualityRequest, VastuArScanQualityData> = VastuContract(VastuOperation.ArScanQuality, ::VastuArScanQualityData)

    val arTrueNorthCalibrate: VastuContract<VastuArTrueNorthCalibrateRequest, VastuArTrueNorthData> = VastuContract(VastuOperation.ArTrueNorthCalibrate, ::VastuArTrueNorthData)

    internal val assessments: VastuContract<VastuAssessmentsRequest, VastuAssessmentData> = VastuContract(VastuOperation.Assessments, ::VastuAssessmentData)

    internal val assessmentsBatch: VastuContract<VastuAssessmentsBatchRequest, VastuAssessmentBatchData> = VastuContract(VastuOperation.AssessmentsBatch, ::VastuAssessmentBatchData)

    val auditFloorPlan: VastuContract<VastuAuditFloorPlanRequest, VastuFloorPlanAuditData> = VastuContract(VastuOperation.AuditFloorPlan, ::VastuFloorPlanAuditData)

    val auditFloorPlanDetailed: VastuContract<VastuAuditFloorPlanDetailedRequest, VastuDetailedFloorPlanAuditData> = VastuContract(VastuOperation.AuditFloorPlanDetailed, ::VastuDetailedFloorPlanAuditData)

    val auditSingleRoom: VastuContract<VastuAuditSingleRoomRequest, VastuSingleRoomAuditData> = VastuContract(VastuOperation.AuditSingleRoom, ::VastuSingleRoomAuditData)

    val compareBeforeAfterRemedy: VastuContract<VastuCompareBeforeAfterRemedyRequest, VastuRemedyComparisonData> = VastuContract(VastuOperation.CompareBeforeAfterRemedy, ::VastuRemedyComparisonData)

    val compoundWallAnalysis: VastuContract<VastuCompoundWallAnalysisRequest, VastuWallAnalysisData> = VastuContract(VastuOperation.CompoundWallAnalysis, ::VastuWallAnalysisData)

    val directionAuspiciousFacing: VastuContract<VastuDirectionAuspiciousFacingRequest, VastuAuspiciousFacingData> = VastuContract(VastuOperation.DirectionAuspiciousFacing, ::VastuAuspiciousFacingData)

    val directionCorrect: VastuContract<VastuDirectionCorrectRequest, VastuDirectionCorrectData> = VastuContract(VastuOperation.DirectionCorrect, ::VastuDirectionCorrectData)

    val directionDeclination: VastuContract<VastuDirectionDeclinationRequest, VastuDirectionDeclinationData> = VastuContract(VastuOperation.DirectionDeclination, ::VastuDirectionDeclinationData)

    val directionSunPath: VastuContract<VastuDirectionSunPathRequest, VastuSunPathData> = VastuContract(VastuOperation.DirectionSunPath, ::VastuSunPathData)

    val directionZoneFromBearing: VastuContract<VastuDirectionZoneFromBearingRequest, VastuBearingZoneData> = VastuContract(VastuOperation.DirectionZoneFromBearing, ::VastuBearingZoneData)

    val elementsBalanceSuggest: VastuContract<VastuElementsBalanceSuggestRequest, VastuElementBalanceData> = VastuContract(VastuOperation.ElementsBalanceSuggest, ::VastuElementBalanceData)

    val elementsDistribution: VastuContract<VastuElementsDistributionRequest, VastuElementDistributionData> = VastuContract(VastuOperation.ElementsDistribution, ::VastuElementDistributionData)

    val entranceObstructionCheck: VastuContract<VastuEntranceObstructionCheckRequest, VastuObstructionData> = VastuContract(VastuOperation.EntranceObstructionCheck, ::VastuObstructionData)

    val entrancePada: VastuContract<VastuEntrancePadaRequest, VastuEntrancePadaData> = VastuContract(VastuOperation.EntrancePada, ::VastuEntrancePadaData)

    val entranceRecommend: VastuContract<VastuEntranceRecommendRequest, VastuEntranceRecommendData> = VastuContract(VastuOperation.EntranceRecommend, ::VastuEntranceRecommendData)

    val floorLevelAnalysis: VastuContract<VastuFloorLevelAnalysisRequest, VastuLevelAnalysisData> = VastuContract(VastuOperation.FloorLevelAnalysis, ::VastuLevelAnalysisData)

    val fusionChart: VastuContract<VastuFusionChartRequest, VastuFusionChartData> = VastuContract(VastuOperation.FusionChart, ::VastuFusionChartData)

    val mandalaProject81Pada: VastuContract<VastuMandalaProject81PadaRequest, VastuMandalaProjectionData> = VastuContract(VastuOperation.MandalaProject81Pada, ::VastuMandalaProjectionData)

    val mandalaProject9Zone: VastuContract<VastuMandalaProject9ZoneRequest, VastuMandalaProjectionData> = VastuContract(VastuOperation.MandalaProject9Zone, ::VastuMandalaProjectionData)

    val mandalaProjectBrahmasthan: VastuContract<VastuMandalaProjectBrahmasthanRequest, VastuBrahmasthanProjectionData> = VastuContract(VastuOperation.MandalaProjectBrahmasthan, ::VastuBrahmasthanProjectionData)

    val multiStoreyFloorRules: VastuContract<VastuMultiStoreyFloorRulesRequest, VastuFloorRulesData> = VastuContract(VastuOperation.MultiStoreyFloorRules, ::VastuFloorRulesData)

    val placementBalcony: VastuContract<VastuPlacementBalconyRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementBalcony, ::VastuPlacementData)

    val placementBorewell: VastuContract<VastuPlacementBorewellRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementBorewell, ::VastuPlacementData)

    val placementGarden: VastuContract<VastuPlacementGardenRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementGarden, ::VastuPlacementData)

    val placementGeneratorElectrical: VastuContract<VastuPlacementGeneratorElectricalRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementGeneratorElectrical, ::VastuPlacementData)

    val placementMainGate: VastuContract<VastuPlacementMainGateRequest, VastuMainGateData> = VastuContract(VastuOperation.PlacementMainGate, ::VastuMainGateData)

    val placementOverheadTank: VastuContract<VastuPlacementOverheadTankRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementOverheadTank, ::VastuPlacementData)

    val placementSepticTank: VastuContract<VastuPlacementSepticTankRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementSepticTank, ::VastuPlacementData)

    val placementTree: VastuContract<VastuPlacementTreeRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementTree, ::VastuPlacementData)

    val placementWell: VastuContract<VastuPlacementWellRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementWell, ::VastuPlacementData)

    val placementWindow: VastuContract<VastuPlacementWindowRequest, VastuPlacementData> = VastuContract(VastuOperation.PlacementWindow, ::VastuPlacementData)

    val planAnalyze: VastuContract<VastuPlanAnalyzeRequest, VastuPlanAuditData> = VastuContract(VastuOperation.PlanAnalyze, ::VastuPlanAuditData)

    val planFromRequirements: VastuContract<VastuPlanFromRequirementsRequest, VastuPlanGenerateData> = VastuContract(VastuOperation.PlanFromRequirements, ::VastuPlanGenerateData)

    val planGenerate: VastuContract<VastuPlanGenerateRequest, VastuPlanGenerateData> = VastuContract(VastuOperation.PlanGenerate, ::VastuPlanGenerateData)

    val planOptimize: VastuContract<VastuPlanOptimizeRequest, VastuPlanOptimizeData> = VastuContract(VastuOperation.PlanOptimize, ::VastuPlanOptimizeData)

    val planReport: VastuContract<VastuPlanReportRequest, VastuPlanAuditData> = VastuContract(VastuOperation.PlanReport, ::VastuPlanAuditData)

    val planUpload: VastuContract<VastuPlanUploadRequest, VastuPlanAuditData> = VastuContract(VastuOperation.PlanUpload, ::VastuPlanAuditData)

    val plotExtensionsCuts: VastuContract<VastuPlotExtensionsCutsRequest, VastuPlotExtensionsCutsData> = VastuContract(VastuOperation.PlotExtensionsCuts, ::VastuPlotExtensionsCutsData)

    val plotOrientation: VastuContract<VastuPlotOrientationRequest, VastuPlotOrientationData> = VastuContract(VastuOperation.PlotOrientation, ::VastuPlotOrientationData)

    val plotRatio: VastuContract<VastuPlotRatioRequest, VastuPlotRatioData> = VastuContract(VastuOperation.PlotRatio, ::VastuPlotRatioData)

    val plotRoadOrientation: VastuContract<VastuPlotRoadOrientationRequest, VastuRoadOrientationData> = VastuContract(VastuOperation.PlotRoadOrientation, ::VastuRoadOrientationData)

    val plotShape: VastuContract<VastuPlotShapeRequest, VastuPlotShapeData> = VastuContract(VastuOperation.PlotShape, ::VastuPlotShapeData)

    val plotSlope: VastuContract<VastuPlotSlopeRequest, VastuPlotSlopeData> = VastuContract(VastuOperation.PlotSlope, ::VastuPlotSlopeData)

    val referenceColorsByZone: VastuContract<VastuNoRequest, VastuZoneReferenceData> = VastuContract(VastuOperation.ReferenceColorsByZone, ::VastuZoneReferenceData)

    val referenceDefectsCatalog: VastuContract<VastuNoRequest, VastuCatalogReferenceData> = VastuContract(VastuOperation.ReferenceDefectsCatalog, ::VastuCatalogReferenceData)

    val referenceDirections16: VastuContract<VastuNoRequest, VastuDirectionsReferenceData> = VastuContract(VastuOperation.ReferenceDirections16, ::VastuDirectionsReferenceData)

    val referenceDirections32: VastuContract<VastuNoRequest, VastuDirections32ReferenceData> = VastuContract(VastuOperation.ReferenceDirections32, ::VastuDirections32ReferenceData)

    val referenceDirections8: VastuContract<VastuNoRequest, VastuDirectionsReferenceData> = VastuContract(VastuOperation.ReferenceDirections8, ::VastuDirectionsReferenceData)

    val referenceGateObstructions: VastuContract<VastuNoRequest, VastuCatalogReferenceData> = VastuContract(VastuOperation.ReferenceGateObstructions, ::VastuCatalogReferenceData)

    val referenceMandala45Devatas: VastuContract<VastuNoRequest, VastuMandalaReferenceData> = VastuContract(VastuOperation.ReferenceMandala45Devatas, ::VastuMandalaReferenceData)

    val referenceMandala64Pada: VastuContract<VastuNoRequest, VastuMandalaReferenceData> = VastuContract(VastuOperation.ReferenceMandala64Pada, ::VastuMandalaReferenceData)

    val referenceMandala9Zone: VastuContract<VastuNoRequest, VastuMandalaReferenceData> = VastuContract(VastuOperation.ReferenceMandala9Zone, ::VastuMandalaReferenceData)

    val referenceMaterialsByZone: VastuContract<VastuNoRequest, VastuZoneReferenceData> = VastuContract(VastuOperation.ReferenceMaterialsByZone, ::VastuZoneReferenceData)

    val referenceRemediesCatalog: VastuContract<VastuNoRequest, VastuCatalogReferenceData> = VastuContract(VastuOperation.ReferenceRemediesCatalog, ::VastuCatalogReferenceData)

    val roomBedroom: VastuContract<VastuRoomBedroomRequest, VastuRoomData> = VastuContract(VastuOperation.RoomBedroom, ::VastuRoomData)

    val roomDining: VastuContract<VastuRoomDiningRequest, VastuRoomData> = VastuContract(VastuOperation.RoomDining, ::VastuRoomData)

    val roomKitchen: VastuContract<VastuRoomKitchenRequest, VastuRoomData> = VastuContract(VastuOperation.RoomKitchen, ::VastuRoomData)

    val roomLiving: VastuContract<VastuRoomLivingRequest, VastuRoomData> = VastuContract(VastuOperation.RoomLiving, ::VastuRoomData)

    val roomPooja: VastuContract<VastuRoomPoojaRequest, VastuRoomData> = VastuContract(VastuOperation.RoomPooja, ::VastuRoomData)

    val roomStaircase: VastuContract<VastuRoomStaircaseRequest, VastuRoomData> = VastuContract(VastuOperation.RoomStaircase, ::VastuRoomData)

    val roomStore: VastuContract<VastuRoomStoreRequest, VastuRoomData> = VastuContract(VastuOperation.RoomStore, ::VastuRoomData)

    val roomStudy: VastuContract<VastuRoomStudyRequest, VastuRoomData> = VastuContract(VastuOperation.RoomStudy, ::VastuRoomData)

    val roomToilet: VastuContract<VastuRoomToiletRequest, VastuRoomData> = VastuContract(VastuOperation.RoomToilet, ::VastuRoomData)

    val roomWaterStorage: VastuContract<VastuRoomWaterStorageRequest, VastuRoomData> = VastuContract(VastuOperation.RoomWaterStorage, ::VastuRoomData)

    val scoreComplianceIndex: VastuContract<VastuScoreComplianceIndexRequest, VastuComplianceIndexData> = VastuContract(VastuOperation.ScoreComplianceIndex, ::VastuComplianceIndexData)

    val scoreOverall: VastuContract<VastuScoreOverallRequest, VastuOverallScoreData> = VastuContract(VastuOperation.ScoreOverall, ::VastuOverallScoreData)

    val scoreZoneWise: VastuContract<VastuScoreZoneWiseRequest, VastuZoneWiseScoreData> = VastuContract(VastuOperation.ScoreZoneWise, ::VastuZoneWiseScoreData)

    val specializedCommercial: VastuContract<VastuSpecializedCommercialRequest, VastuSpecializedAuditData> = VastuContract(VastuOperation.SpecializedCommercial, ::VastuSpecializedAuditData)

    val specializedEducational: VastuContract<VastuSpecializedEducationalRequest, VastuSpecializedAuditData> = VastuContract(VastuOperation.SpecializedEducational, ::VastuSpecializedAuditData)

    val specializedFactory: VastuContract<VastuSpecializedFactoryRequest, VastuSpecializedAuditData> = VastuContract(VastuOperation.SpecializedFactory, ::VastuSpecializedAuditData)

    val specializedHospital: VastuContract<VastuSpecializedHospitalRequest, VastuSpecializedAuditData> = VastuContract(VastuOperation.SpecializedHospital, ::VastuSpecializedAuditData)

    val specializedResidential: VastuContract<VastuSpecializedResidentialRequest, VastuSpecializedAuditData> = VastuContract(VastuOperation.SpecializedResidential, ::VastuSpecializedAuditData)

    val specializedRestaurant: VastuContract<VastuSpecializedRestaurantRequest, VastuSpecializedAuditData> = VastuContract(VastuOperation.SpecializedRestaurant, ::VastuSpecializedAuditData)

    val specializedTemple: VastuContract<VastuSpecializedTempleRequest, VastuSpecializedAuditData> = VastuContract(VastuOperation.SpecializedTemple, ::VastuSpecializedAuditData)

    val timingBhumiPujan: VastuContract<VastuTimingBhumiPujanRequest, VastuTimingData> = VastuContract(VastuOperation.TimingBhumiPujan, ::VastuTimingData)

    val timingConstructionStart: VastuContract<VastuTimingConstructionStartRequest, VastuTimingData> = VastuContract(VastuOperation.TimingConstructionStart, ::VastuTimingData)

    val timingGrihapravesh: VastuContract<VastuTimingGrihapraveshRequest, VastuTimingData> = VastuContract(VastuOperation.TimingGrihapravesh, ::VastuTimingData)

    val timingVastuShanti: VastuContract<VastuTimingVastuShantiRequest, VastuTimingData> = VastuContract(VastuOperation.TimingVastuShanti, ::VastuTimingData)

}

// END GENERATED VASTU CONTRACTS

/**
 * Vastu Shastra: plot geometry, mandala projection, entrance/room/placement
 * rules, compliance audits, scoring, and floor-plan generation (93 logical backend
 * operations across the full domain — this first deliverable ships the 12
 * client methods that reach all of them, including the two escape hatches,
 * [vastu] and [vastuReference], for any op/table that doesn't have its own
 * named method). Mirrors the Vastu section of
 * `sdks/flutter/lib/src/services/astrology_service.dart` (lines 206-312) and
 * `sdks/javascript/src/client.ts` (lines 682-821).
 *
 * Vastu takes a BUILDING (plot polygon, room list, compass zone), never a
 * birth chart — do not pass birth-details params here.
 *
 * Every method is a suspend function; call from a coroutine scope (e.g.
 * `viewModelScope.launch { ... }` or `runBlocking { ... }` in tests/CLIs).
 * Caller-owned idempotency keys may be retained across client or process restarts.
 */
class VastuService internal constructor(private val client: VedikaClient) {

    private companion object {
        const val BASE = "/v2/astrology/vastu"
    }

    /** Exact request and result types for one of the 93 mounted operations. */
    suspend fun <Request : VastuRequest, Data : VastuData> vastuOperation(
        contract: VastuContract<Request, Data>,
        request: Request,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<Data> {
        val body = request.toMap()
        val raw = if (isGetOp(contract.operation.path)) {
            client.get("$BASE/${contract.operation.path}", stringifyParams(body), idempotencyKey = idempotencyKey)
        } else {
            client.post("$BASE/${contract.operation.path}", body, idempotencyKey = idempotencyKey)
        }
        return VastuTypedResponse(raw.getBoolean("success"), contract.decode(raw.getJSONObject("data")), raw)
    }

    /**
     * Any Vastu operation by its path suffix under `/v2/astrology/vastu/`,
     * e.g. `vastu("score/overall", mapOf("rooms" to rooms))`.
     *
     * The 11 tables under `reference/` (10 original +
     * `reference/gate-obstructions`) are GET-only (a POST returns 405) and
     * `direction/declination` is a GET+POST dual whose verified path is
     * GET-with-query, so both dispatch GET (params become query string);
     * everything else is POST. Mirrors `VASTU_GET_REFERENCE_ROUTES` +
     * `VASTU_DUAL_ROUTE` in `rust/vedika-api-rust/crates/vedika-v2/src/vastu.rs`.
     */
    suspend fun vastu(op: String, params: Map<String, Any?> = emptyMap(), idempotencyKey: String? = null): JSONObject {
        val path = stripLeadingSlash(op)
        return if (isGetOp(path)) {
            client.get("$BASE/$path", stringifyParams(params), idempotencyKey = idempotencyKey)
        } else {
            client.post("$BASE/$path", params, idempotencyKey = idempotencyKey)
        }
    }

    suspend fun vastuScansSave(request: VastuScansSaveRequest, idempotencyKey: String? = null): VastuScanResponse<VastuScansSaveData> {
        val raw = client.post("$BASE/scans/save", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuScansSaveData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuScansRetrieve(request: VastuScansRetrieveRequest, idempotencyKey: String? = null): VastuScanResponse<VastuScansRetrieveData> {
        val raw = client.post("$BASE/scans/retrieve", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuScansRetrieveData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuScansList(request: VastuScansListRequest, idempotencyKey: String? = null): VastuScanResponse<VastuScansListData> {
        val raw = client.post("$BASE/scans/list", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuScansListData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuScansDelete(request: VastuScansDeleteRequest, idempotencyKey: String? = null): VastuScanResponse<VastuScansDeleteData> {
        val raw = client.post("$BASE/scans/delete", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuScansDeleteData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuScansTimelapse(request: VastuScansTimelapseRequest, idempotencyKey: String? = null): VastuScanResponse<VastuScansTimelapseData> {
        val raw = client.post("$BASE/scans/timelapse", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuScansTimelapseData(raw.getJSONObject("data")), raw)
    }

    /** Closed, typed Vastu operation surface. */
    suspend fun vastuOperation(
        operation: VastuOperation,
        request: VastuOperationRequest = VastuOperationRequest(),
        idempotencyKey: String? = null,
    ): VastuOperationResult {
        val body = request.toMap()
        val raw = if (isGetOp(operation.path)) {
            client.get("$BASE/${operation.path}", stringifyParams(body), idempotencyKey = idempotencyKey)
        } else {
            client.post("$BASE/${operation.path}", body, idempotencyKey = idempotencyKey)
        }
        return VastuOperationResult(raw)
    }

    /**
     * A GET reference table, e.g. `reference/mandala/9-zone`,
     * `reference/mandala/45-devatas`, `reference/directions/8`,
     * `reference/defects/catalog`, `reference/remedies/catalog`,
     * `reference/gate-obstructions`.
     */
    suspend fun vastuReference(table: String, idempotencyKey: String? = null): JSONObject =
        client.get("$BASE/${stripLeadingSlash(table)}", idempotencyKey = idempotencyKey)

    /**
     * Project a mandala onto a plot. [scheme] is `9-zone`, `81-pada` or
     * `brahmasthan`. Body: `{plotPolygon, bearingDeg}`.
     */
    suspend fun vastuMandalaProject(scheme: String, params: Map<String, Any?>, idempotencyKey: String? = null): JSONObject =
        client.post("$BASE/mandala/project/${stripLeadingSlash(scheme)}", params, idempotencyKey = idempotencyKey)

    /** Exact door-pada operation. */
    suspend fun vastuEntrancePada(
        request: VastuEntrancePadaRequest,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<VastuEntrancePadaData> =
        vastuOperation(VastuContracts.entrancePada, request, idempotencyKey = idempotencyKey)

    /** Exact entrance recommendation operation. */
    suspend fun vastuEntranceRecommend(
        request: VastuEntranceRecommendRequest,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<VastuEntranceRecommendData> =
        vastuOperation(VastuContracts.entranceRecommend, request, idempotencyKey = idempotencyKey)

    /** Counted room telemetry; the existing Boolean request remains available. */
    suspend fun vastuArScanQuality(request: VastuArCountedScanQualityRequest, idempotencyKey: String? = null): VastuTypedResponse<VastuArScanQualityData> =
        vastuOperation(VastuContracts.arCountedScanQuality, request, idempotencyKey = idempotencyKey)

    /** Exact AR scan-quality operation. */
    suspend fun vastuArScanQuality(
        request: VastuArScanQualityRequest,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<VastuArScanQualityData> =
        vastuOperation(VastuContracts.arScanQuality, request, idempotencyKey = idempotencyKey)

    /** Exact solar true-north calibration operation. */
    suspend fun vastuArTrueNorthCalibrate(
        request: VastuArTrueNorthCalibrateRequest,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<VastuArTrueNorthData> =
        vastuOperation(VastuContracts.arTrueNorthCalibrate, request, idempotencyKey = idempotencyKey)

    /** Exact assessment operation. Valid insufficient-data responses have no billing object. */
    suspend fun vastuAssessments(
        request: VastuAssessmentsRequest,
        idempotencyKey: String? = null,
    ): VastuAssessmentsResponse {
        val raw = client.post("$BASE/${VastuOperation.Assessments.path}", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuAssessmentsResponse(
            raw.getBoolean("success"),
            VastuAssessmentData(raw.getJSONObject("data")),
            raw,
        )
    }

    /** Batch results carry each item's status and billing. The batch has no separate fee. */
    suspend fun vastuAssessmentsBatch(
        request: VastuAssessmentsBatchRequest,
        idempotencyKey: String,
    ): VastuAssessmentsBatchResponse {
        val raw = client.post("$BASE/${VastuOperation.AssessmentsBatch.path}", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuAssessmentsBatchResponse(
            raw.getBoolean("success"), VastuAssessmentBatchData(raw.getJSONObject("data")), raw,
        )
    }

    /**
     * Single-room placement, e.g. `vastuRoom("kitchen", mapOf("zone" to "southeast"))`.
     * [roomType]: kitchen, bedroom, pooja, toilet, staircase, study, living,
     * dining, store, water-storage.
     */
    suspend fun vastuRoom(roomType: String, params: Map<String, Any?>, idempotencyKey: String? = null): JSONObject =
        client.post("$BASE/room/${stripLeadingSlash(roomType)}", params, idempotencyKey = idempotencyKey)

    /** Site placement, e.g. `vastuPlacement("borewell", mapOf("zone" to "north-east"))`. */
    suspend fun vastuPlacement(feature: String, params: Map<String, Any?>, idempotencyKey: String? = null): JSONObject =
        client.post("$BASE/placement/${stripLeadingSlash(feature)}", params, idempotencyKey = idempotencyKey)

    /**
     * Compliance audit. [kind]: `single-room`, `floor-plan`,
     * `floor-plan-detailed`. Body: `{rooms: [...], plot?}`.
     */
    suspend fun vastuAudit(kind: String, params: Map<String, Any?>, idempotencyKey: String? = null): JSONObject =
        client.post("$BASE/audit/${stripLeadingSlash(kind)}", params, idempotencyKey = idempotencyKey)

    /** Vastu score. [kind]: `overall`, `zone-wise`, `compliance-index`. */
    suspend fun vastuScore(kind: String, params: Map<String, Any?>, idempotencyKey: String? = null): JSONObject =
        client.post("$BASE/score/${stripLeadingSlash(kind)}", params, idempotencyKey = idempotencyKey)

    /** Generate up to 3 ranked floor plans from a plot + room programme. */
    suspend fun vastuPlanGenerate(
        request: VastuPlanGenerateRequest,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<VastuPlanGenerateData> =
        vastuOperation(VastuContracts.planGenerate, request, idempotencyKey = idempotencyKey)

    /** Generate a floor plan from a high-level brief (BHK, bathrooms, parking...). */
    suspend fun vastuPlanFromRequirements(
        request: VastuPlanFromRequirementsRequest,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<VastuPlanGenerateData> =
        vastuOperation(VastuContracts.planFromRequirements, request, idempotencyKey = idempotencyKey)

    /** Magnetic declination (true-north correction) for a location. India grid. */
    suspend fun vastuDeclination(
        lat: Double,
        lon: Double,
        date: String? = null,
        idempotencyKey: String? = null,
    ): VastuTypedResponse<VastuDirectionDeclinationData> =
        vastuOperation(VastuContracts.directionDeclination, VastuDirectionDeclinationRequest(lat, lon, date), idempotencyKey = idempotencyKey)

    private fun isGetOp(path: String): Boolean =
        path.startsWith("reference/") || path == "direction/declination"

    /** Query params are stringified for GET; null values are dropped rather than sent as "null". */
    private fun stringifyParams(params: Map<String, Any?>): Map<String, String> =
        params.mapNotNull { (k, v) -> v?.let { k to it.toString() } }.toMap()

    /** Strips ALL leading slashes so `vastu("/score/overall", …)` and `vastu("score/overall", …)` both work. */
    private fun stripLeadingSlash(s: String): String = s.trimStart('/')
}

data class VastuScanResponse<Data : VastuData>(val success: Boolean, val data: Data, val raw: JSONObject) {
    val billing: JSONObject? get() = raw.optJSONObject("billing")
    val meta: JSONObject? get() = raw.optJSONObject("meta")
}
