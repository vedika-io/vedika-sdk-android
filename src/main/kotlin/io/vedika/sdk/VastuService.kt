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
    RemediationTasksUpsert("remediation/tasks/upsert"),
    RemediationTasksList("remediation/tasks/list"),
    RemediationTasksDelete("remediation/tasks/delete"),
    RemediationReassess("remediation/reassess"),
    MerchantCatalogUpload("merchant/catalog/upload"),
    MerchantCatalogGet("merchant/catalog/get"),
    MerchantCatalogDelete("merchant/catalog/delete"),
    MerchantRemedies("merchant/remedies"),

    PortfolioSearch("portfolio/search"),
    PortfolioCompare("portfolio/compare"),
    PortfolioAnalytics("portfolio/analytics"),
    PortfolioUsage("portfolio/usage"),
    PortfolioUsageExport("portfolio/usage/export"),
    PortfolioBudgetsSet("portfolio/budgets/set"),
    PortfolioBudgetsGet("portfolio/budgets/get"),
    DrawingSheet("report/drawing-sheet"),
    PropertiesCreate("properties/create"),
    PropertiesUpdate("properties/update"),
    PropertiesCollaborationGet("properties/collaboration/get"),
    PropertiesCollaborationInvite("properties/collaboration/invite"),
    PropertiesCollaborationRevoke("properties/collaboration/revoke"),
    PropertiesCollaborationMembers("properties/collaboration/members"),
    PropertiesCollaborationComment("properties/collaboration/comment"),
    PropertiesCollaborationReview("properties/collaboration/review"),
    PropertiesCollaborationUpdate("properties/collaboration/update"),
    PropertiesActivityList("properties/activity/list"),
    PropertiesActivityExport("properties/activity/export"),
    PropertiesGet("properties/get"),
    PropertiesList("properties/list"),
    PropertiesDelete("properties/delete"),
    PropertiesLinkScan("properties/link-scan"),
    ArchiveTier("archive/tier"),
    ArchiveExport("archive/export"),
    ArchiveDelete("archive/delete"),
    ArchiveSummary("archive/summary"),
    FeedListings("feed/listings"),
    QuoteCalculate("quote/calculate"),

    ScansTimelapse("scans/timelapse"),
    ScansDelete("scans/delete"),
    ScansList("scans/list"),
    ScansRetrieve("scans/retrieve"),
    ScansSave("scans/save"),
    ArDeityIcons("ar/deity-icons"),
    ArCaptureMerge("ar/capture-merge"),
    PlotFromSurvey("plot/from-survey"),
    ArRoomCapture("ar/room-capture"),
    ArAttestationChallenge("ar/attestation/challenge"),
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
    PlanCompareVersions("plan/compare-versions"),
    ReceiptVerify("receipt/verify"),
    RulesVersions("rules/versions"),
    PlanAnalyze("plan/analyze"),
    PlanImportDxf("plan/import-dxf"),
    PlanExportDxf("plan/export-dxf"),
    PlanExportIfc("plan/export-ifc"),
    PlanConvertUnits("plan/convert-units"),
    PlanImportIfc("plan/import-ifc"),
    PlanImportImage("plan/import-image"),
    PlanImportPdf("plan/import-pdf"),
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
    Jobs("jobs"),
    JobsId("jobs/{id}"),
    JobsIdResults("jobs/{id}/results"),
    JobsIdCancel("jobs/{id}/cancel"),
}

data class VastuDrawingSheetRequestTitleBlock(
    val project: String,
    val architect: String,
    val drawingNumber: String? = null,
    val revision: String? = null,
    val date: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "project" to project,
        "architect" to architect,
        "drawingNumber" to drawingNumber,
        "revision" to revision,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDrawingSheetRequestFieldEvidenceItem(
    val label: String,
    val note: String? = null,
    val roomId: String? = null,
    val imageDataUrl: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "label" to label,
        "note" to note,
        "roomId" to roomId,
        "imageDataUrl" to imageDataUrl
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDrawingSheetRequest(
    val plan: JSONObject,
    val titleBlock: VastuDrawingSheetRequestTitleBlock,
    val paperSize: String? = null,
    val scaleDenominator: Int? = null,
    val format: String? = null,
    val zoneOverlay: Boolean? = null,
    val dimensions: Boolean? = null,
    val fieldEvidence: List<VastuDrawingSheetRequestFieldEvidenceItem>? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plan" to plan,
        "titleBlock" to titleBlock,
        "paperSize" to paperSize,
        "scaleDenominator" to scaleDenominator,
        "format" to format,
        "zoneOverlay" to zoneOverlay,
        "dimensions" to dimensions,
        "fieldEvidence" to fieldEvidence
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuWorkspaceRequestDrawingTitleBlock(
    val project: String,
    val architect: String,
    val drawingNumber: String? = null,
    val revision: String? = null,
    val date: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "project" to project,
        "architect" to architect,
        "drawingNumber" to drawingNumber,
        "revision" to revision,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuWorkspaceRequestDrawingFieldEvidenceItem(
    val label: String,
    val note: String? = null,
    val roomId: String? = null,
    val imageDataUrl: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "label" to label,
        "note" to note,
        "roomId" to roomId,
        "imageDataUrl" to imageDataUrl
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuWorkspaceRequestDrawing(
    val plan: JSONObject,
    val titleBlock: VastuWorkspaceRequestDrawingTitleBlock,
    val paperSize: String? = null,
    val scaleDenominator: Int? = null,
    val format: String? = null,
    val zoneOverlay: Boolean? = null,
    val dimensions: Boolean? = null,
    val fieldEvidence: List<VastuWorkspaceRequestDrawingFieldEvidenceItem>? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "plan" to plan,
        "titleBlock" to titleBlock,
        "paperSize" to paperSize,
        "scaleDenominator" to scaleDenominator,
        "format" to format,
        "zoneOverlay" to zoneOverlay,
        "dimensions" to dimensions,
        "fieldEvidence" to fieldEvidence
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuWorkspaceRequest(
    val propertyId: String? = null,
    val jobId: String? = null,
    val idempotencyKey: String? = null,
    val title: String? = null,
    val input: JSONObject? = null,
    val outcome: String? = null,
    val webhookSecret: String? = null,
    val drawing: VastuWorkspaceRequestDrawing? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "jobId" to jobId,
        "idempotencyKey" to idempotencyKey,
        "title" to title,
        "input" to input,
        "outcome" to outcome,
        "webhookSecret" to webhookSecret,
        "drawing" to drawing
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCreateRequestIds(
    val project: String,
    val building: String,
    val unit: String,
    val floor: String,
    val revision: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "project" to project,
        "building" to building,
        "unit" to unit,
        "floor" to floor,
        "revision" to revision
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCreateRequest(
    val propertyId: String? = null,
    val ids: VastuPropertiesCreateRequestIds,
    val title: String,
    val data: JSONObject,
    val retentionDays: Int,
    val linkedScanIds: List<String>? = null,
    val linkedAssessmentIds: List<String>? = null,
    val externalId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId,
        "ids" to ids,
        "title" to title,
        "data" to data,
        "retentionDays" to retentionDays,
        "linkedScanIds" to linkedScanIds,
        "linkedAssessmentIds" to linkedAssessmentIds,
        "externalId" to externalId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesUpdateRequestIds(
    val project: String,
    val building: String,
    val unit: String,
    val floor: String,
    val revision: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "project" to project,
        "building" to building,
        "unit" to unit,
        "floor" to floor,
        "revision" to revision
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesUpdateRequest(
    val propertyId: String,
    val ids: VastuPropertiesUpdateRequestIds,
    val title: String,
    val data: JSONObject,
    val retentionDays: Int,
    val linkedScanIds: List<String>? = null,
    val linkedAssessmentIds: List<String>? = null,
    val externalId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId,
        "ids" to ids,
        "title" to title,
        "data" to data,
        "retentionDays" to retentionDays,
        "linkedScanIds" to linkedScanIds,
        "linkedAssessmentIds" to linkedAssessmentIds,
        "externalId" to externalId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCollaborationGetRequest(
    val propertyId: String,
    val ownerId: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCollaborationInviteRequest(
    val propertyId: String,
    val ownerId: String? = null,
    val accountId: String? = null,
    val email: String? = null,
    val role: String,
    val accept: Boolean? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId,
        "accountId" to accountId,
        "email" to email,
        "role" to role,
        "accept" to accept
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCollaborationRevokeRequest(
    val propertyId: String,
    val ownerId: String? = null,
    val accountId: String? = null,
    val invitationId: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId,
        "accountId" to accountId,
        "invitationId" to invitationId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCollaborationMembersRequest(
    val propertyId: String,
    val ownerId: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCollaborationCommentRequest(
    val propertyId: String,
    val ownerId: String? = null,
    val assessmentId: String,
    val revision: String,
    val expectedContentHash: String,
    val text: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId,
        "assessmentId" to assessmentId,
        "revision" to revision,
        "expectedContentHash" to expectedContentHash,
        "text" to text
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCollaborationReviewRequest(
    val propertyId: String,
    val ownerId: String? = null,
    val assessmentId: String,
    val revision: String,
    val expectedContentHash: String,
    val decision: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId,
        "assessmentId" to assessmentId,
        "revision" to revision,
        "expectedContentHash" to expectedContentHash,
        "decision" to decision
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesCollaborationUpdateRequest(
    val propertyId: String,
    val ownerId: String? = null,
    val revision: String,
    val expectedContentHash: String,
    val title: String,
    val data: Map<String, Any?>) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId,
        "revision" to revision,
        "expectedContentHash" to expectedContentHash,
        "title" to title,
        "data" to data
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesActivityListRequest(
    val propertyId: String,
    val ownerId: String? = null,
    val cursor: Int? = null,
    val limit: Int? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId,
        "cursor" to cursor,
        "limit" to limit
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesActivityExportRequest(
    val propertyId: String,
    val ownerId: String? = null,
    val cursor: Int? = null,
    val limit: Int? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "ownerId" to ownerId,
        "cursor" to cursor,
        "limit" to limit
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesGetRequest(
    val propertyId: String,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesListRequest(
    val limit: Int? = null,
    val cursor: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "limit" to limit,
        "cursor" to cursor
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesDeleteRequest(
    val propertyId: String,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPropertiesLinkScanRequest(
    val propertyId: String,
    val addScanIds: List<String>? = null,
    val addAssessmentIds: List<String>? = null,
    val replace: Boolean? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId,
        "addScanIds" to addScanIds,
        "addAssessmentIds" to addAssessmentIds,
        "replace" to replace
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArchiveTierRequest(
    val propertyId: String,
    val months: Int,
    val preview: Boolean? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId,
        "months" to months,
        "preview" to preview
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArchiveExportRequest(
    val propertyId: String,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArchiveDeleteRequest(
    val propertyId: String,
    val confirmPropertyId: String,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId,
        "confirmPropertyId" to confirmPropertyId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArchiveSummaryRequest(
    val propertyId: String,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "propertyId" to propertyId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuFeedListingsRequestRowsItemPlanAsset(
    val kind: String,
    val value: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "kind" to kind,
        "value" to value
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuFeedListingsRequestRowsItem(
    val externalId: String,
    val revision: String,
    val project: String,
    val building: String,
    val unit: String,
    val floor: String,
    val title: String,
    val address: String? = null,
    val city: String? = null,
    val bearingDeg: Double? = null,
    val planAsset: VastuFeedListingsRequestRowsItemPlanAsset? = null,
    val retentionDays: Int? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "externalId" to externalId,
        "revision" to revision,
        "project" to project,
        "building" to building,
        "unit" to unit,
        "floor" to floor,
        "title" to title,
        "address" to address,
        "city" to city,
        "bearingDeg" to bearingDeg,
        "planAsset" to planAsset,
        "retentionDays" to retentionDays
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuFeedListingsRequest(
    val csv: String? = null,
    val rows: List<VastuFeedListingsRequestRowsItem>? = null,
    val dryRun: Boolean? = null,
    val skipDuplicates: Boolean? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "csv" to csv,
        "rows" to rows,
        "dryRun" to dryRun,
        "skipDuplicates" to skipDuplicates
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuQuoteCalculateRequestOperationsItem(
    val op: String,
    val quantity: Int? = null,
    val label: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "op" to op,
        "quantity" to quantity,
        "label" to label
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuQuoteCalculateRequest(
    val workflowId: String? = null,
    val operations: List<VastuQuoteCalculateRequestOperationsItem>? = null,
    val retentionMonths: Int? = null,
    val retentionStoredBytes: Long? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "workflowId" to workflowId,
        "operations" to operations,
        "retentionMonths" to retentionMonths,
        "retentionStoredBytes" to retentionStoredBytes
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationTasksUpsertRequestTaskEvidenceItem(
    val reference: String,
    val photoRef: String? = null,
    val note: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "reference" to reference,
        "photoRef" to photoRef,
        "note" to note
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationTasksUpsertRequestTask(
    val taskId: String,
    val reportRef: String,
    val findingRef: String,
    val remedyKey: String,
    val title: String,
    val status: String,
    val assignee: String? = null,
    val dueDate: String? = null,
    val evidence: List<VastuRemediationTasksUpsertRequestTaskEvidenceItem>? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "taskId" to taskId,
        "reportRef" to reportRef,
        "findingRef" to findingRef,
        "remedyKey" to remedyKey,
        "title" to title,
        "status" to status,
        "assignee" to assignee,
        "dueDate" to dueDate,
        "evidence" to evidence
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationTasksUpsertRequest(
    val expectedRevision: Long,
    val mutationId: String,
    val propertyId: String,
    val task: VastuRemediationTasksUpsertRequestTask) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "expectedRevision" to expectedRevision,
        "mutationId" to mutationId,
        "propertyId" to propertyId,
        "task" to task
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationTasksListRequest(
    val propertyId: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationTasksDeleteRequest(
    val id: String,
    val confirmId: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "confirmId" to confirmId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationReassessRequestPlanRoomsItem(
    val name: String? = null,
    val room: String? = null,
    val roomType: String? = null,
    val label: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val x: Double? = null,
    val y: Double? = null,
    val w: Double? = null,
    val h: Double? = null,
    val width: Double? = null,
    val height: Double? = null,
    val polygon: List<Any?>? = null,
    val outline: List<Any?>? = null,
    val area: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "room" to room,
        "roomType" to roomType,
        "label" to label,
        "zone" to zone,
        "direction" to direction,
        "x" to x,
        "y" to y,
        "w" to w,
        "h" to h,
        "width" to width,
        "height" to height,
        "polygon" to polygon,
        "outline" to outline,
        "area" to area,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationReassessRequestPlanImportReview(
    val northKnown: Boolean,
    val scaleKnown: Boolean,
    val analysisReady: Boolean,
    val coordinateFrame: String,
    val units: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "northKnown" to northKnown,
        "scaleKnown" to scaleKnown,
        "analysisReady" to analysisReady,
        "coordinateFrame" to coordinateFrame,
        "units" to units
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationReassessRequestPlan(
    val rooms: List<VastuRemediationReassessRequestPlanRoomsItem>,
    val plot: JSONObject? = null,
    val zoneResolution: Int? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val importReview: VastuRemediationReassessRequestPlanImportReview? = null,
    val merchantCatalogId: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rooms" to rooms,
        "plot" to plot,
        "zoneResolution" to zoneResolution,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "importReview" to importReview,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRemediationReassessRequest(
    val expectedRevision: Long,
    val mutationId: String,
    val propertyId: String,
    val taskId: String,
    val plan: VastuRemediationReassessRequestPlan) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "expectedRevision" to expectedRevision,
        "mutationId" to mutationId,
        "propertyId" to propertyId,
        "taskId" to taskId,
        "plan" to plan
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMerchantCatalogUploadRequest(
    val expectedRevision: Long,
    val mutationId: String,
    val catalogId: String,
    val format: String,
    val content: Any?) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "expectedRevision" to expectedRevision,
        "mutationId" to mutationId,
        "catalogId" to catalogId,
        "format" to format,
        "content" to content
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMerchantCatalogGetRequest(
    val catalogId: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "catalogId" to catalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMerchantCatalogDeleteRequest(
    val id: String,
    val confirmId: String) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "confirmId" to confirmId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMerchantRemediesRequest(
    val catalogId: String,
    val remedyKeys: List<String>) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "catalogId" to catalogId,
        "remedyKeys" to remedyKeys
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
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
    val zone: String,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,

        "roomType" to roomType,
        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArHeatmapRasterRequest(
    val rooms: List<VastuArHeatmapRasterRequestRoomsItem>,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val planToWorld: VastuArPlanToWorld,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "planToWorld" to planToWorld
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArZoneTexturesRequest(
    val zone: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "zone" to zone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArYantraMeshesRequest(
    val model: String,
    val format: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "model" to model,
        "format" to format
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArDeityIconsRequest(
    val zone: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val source: String = "traced",
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "polygon" to if (multipolygons == null) polygon else null,
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
    val openings: List<VastuRoomCaptureRoomsItemOpeningsItem>? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,

        "id" to id,
        "label" to label,
        "labelSource" to labelSource,
        "polygon" to if (multipolygons == null) polygon else null,
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

/** Optional native-app device attestation proof; see [VastuOperation.ArAttestationChallenge]. */
data class VastuDeviceAttestation(
    val platform: String,
    val challenge: String,
    val keyId: String? = null,
    val attestationObject: String? = null,
    val assertion: String? = null,
    val integrityToken: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "platform" to platform,
        "challenge" to challenge,
        "keyId" to keyId,
        "attestationObject" to attestationObject,
        "assertion" to assertion,
        "integrityToken" to integrityToken
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArAttestationChallengeRequest(
    val platform: String,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "platform" to platform
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArCaptureMergeRequestLinksItemControlPointsItem(
    val fromXY: List<Double>,
    val toXY: List<Double>) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "fromXY" to fromXY,
        "toXY" to toXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArCaptureMergeRequestLinksItemSharedDoorsItem(
    val fromXY: List<Double>,
    val toXY: List<Double>) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "fromXY" to fromXY,
        "toXY" to toXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArCaptureMergeRequestFloorsItem(
    val floorIndex: Int,
    val elevationM: Double,
    val originXY: List<Double>,
    val bearingDeg: Double) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "floorIndex" to floorIndex,
        "elevationM" to elevationM,
        "originXY" to originXY,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArCaptureMergeRequestCapturesItem(
    val id: String,
    val floorIndex: Int,
    val payload: VastuRoomCapture) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "floorIndex" to floorIndex,
        "payload" to payload
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArCaptureMergeRequestLinksItem(
    val fromCaptureId: String,
    val toCaptureId: String,
    val controlPoints: List<VastuArCaptureMergeRequestLinksItemControlPointsItem>? = null,
    val sharedDoors: List<VastuArCaptureMergeRequestLinksItemSharedDoorsItem>? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "fromCaptureId" to fromCaptureId,
        "toCaptureId" to toCaptureId,
        "controlPoints" to controlPoints,
        "sharedDoors" to sharedDoors
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotFromSurveyRequestControlPointsItem(
    val id: String,
    val xy: List<Double>) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "xy" to xy
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArCaptureMergeRequest(
    val captures: List<VastuArCaptureMergeRequestCapturesItem>,
    val links: List<VastuArCaptureMergeRequestLinksItem>,
    val floors: List<VastuArCaptureMergeRequestFloorsItem>,
    val toleranceM: Double,
    val maxChargeUsd: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "maxChargeUsd" to maxChargeUsd,
        "captures" to captures,
        "links" to links,
        "floors" to floors,
        "toleranceM" to toleranceM
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotFromSurveyRequest(
    val crs: String,
    val units: String,
    val boundary: List<List<Double>>,
    val controlPoints: List<VastuPlotFromSurveyRequestControlPointsItem>? = null,
    val origin: List<Double>? = null,
    val maxChargeUsd: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "maxChargeUsd" to maxChargeUsd,
        "crs" to crs,
        "units" to units,
        "boundary" to boundary,
        "controlPoints" to controlPoints,
        "origin" to origin
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuOptimizationConstraintsWetShaftsItem(
    val id: String,
    val polygon: List<List<Double>>,
    val roomIds: List<String>? = null,
    val maxDistanceM: Double? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "polygon" to polygon,
        "roomIds" to roomIds,
        "maxDistanceM" to maxDistanceM
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuOptimizationConstraintsPlumbingStacksItem(
    val id: String,
    val polygon: List<List<Double>>,
    val roomIds: List<String>? = null,
    val maxDistanceM: Double? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "polygon" to polygon,
        "roomIds" to roomIds,
        "maxDistanceM" to maxDistanceM
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuOptimizationConstraintsColumnsItem(
    val id: String? = null,
    val polygon: List<List<Double>>) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "polygon" to polygon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuOptimizationConstraintsLoadBearingWallsItem(
    val id: String? = null,
    val polygon: List<List<Double>>) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "polygon" to polygon
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuOptimizationConstraints(
    val lockedRooms: List<String>? = null,
    val wetShafts: List<VastuOptimizationConstraintsWetShaftsItem>? = null,
    val plumbingStacks: List<VastuOptimizationConstraintsPlumbingStacksItem>? = null,
    val loadBearingWalls: List<VastuOptimizationConstraintsLoadBearingWallsItem>? = null,
    val columns: List<VastuOptimizationConstraintsColumnsItem>? = null,
    val minSizes: JSONObject? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "lockedRooms" to lockedRooms,
        "wetShafts" to wetShafts,
        "plumbingStacks" to plumbingStacks,
        "loadBearingWalls" to loadBearingWalls,
        "columns" to columns,
        "minSizes" to minSizes
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArRoomCaptureRequest(
    val capture: VastuRoomCapture,
    val zoneResolution: Int? = null,
    val deviceAttestation: VastuDeviceAttestation? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,

        "capture" to capture,
        "zoneResolution" to zoneResolution,
        "deviceAttestation" to deviceAttestation
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
    val snapshot: VastuScanSnapshot,
    val deviceAttestation: VastuDeviceAttestation? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "tenantRef" to tenantRef,

        "scanId" to scanId,
        "propertyId" to propertyId,
        "title" to title,
        "retentionDays" to retentionDays,
        "snapshot" to snapshot,
        "deviceAttestation" to deviceAttestation
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansRetrieveRequest(
    val requestId: String,
    val scanId: String,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "requestId" to requestId,
        "scanId" to scanId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansListRequest(
    val requestId: String,
    val limit: Int,
    val cursor: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "requestId" to requestId,
        "limit" to limit,
        "cursor" to cursor
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansDeleteRequest(
    val scanId: String,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "scanId" to scanId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuScansTimelapseRequest(
    val requestId: String,
    val scanIds: List<String>,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val pointCloudDensityBasis: String? = null,
    val deviceAttestation: VastuDeviceAttestation? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
        "pointCloudDensityBasis" to pointCloudDensityBasis,
        "deviceAttestation" to deviceAttestation
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
    val pointCloudDensityBasis: String? = null,
    val deviceAttestation: VastuDeviceAttestation? = null
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
        "pointCloudDensityBasis" to pointCloudDensityBasis,
        "deviceAttestation" to deviceAttestation
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuArTrueNorthCalibrateRequest(
    val lat: Double,
    val lon: Double,
    val datetime: String,
    val deviceHeadingAtSunDeg: Double,
    val deviceHeadingAccuracyDeg: Double? = null,
    val headingSampleAgeMs: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val scannedAreaM2: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val rulesVersion: String? = null,
    val receipt: Boolean? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rulesVersion" to rulesVersion,
        "receipt" to receipt,
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "items" to items.map { it.toMap() },
    ).filterValues { it != null }
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
    val bearingDeg: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "rooms" to rooms,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg ,
        "merchantCatalogId" to merchantCatalogId
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
    val text: String? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "rooms" to rooms,
        "text" to text ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuAuditSingleRoomRequest(
    val roomType: String,
    val zone: String,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "roomType" to roomType,
        "zone" to zone ,
        "merchantCatalogId" to merchantCatalogId
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
    val remedies: List<VastuCompareBeforeAfterRemedyRequestRemediesItem>,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,

        "rooms" to rooms,
        "text" to text,
        "remedies" to remedies
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuCompoundWallAnalysisRequest(
    val walls: VastuJsonObjectOrArray? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "walls" to walls
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionAuspiciousFacingRequest(
    val purpose: String,
    val occupant: String? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "purpose" to purpose,
        "occupant" to occupant
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionCorrectRequest(
    val direction: String,
    val lat: Double,
    val lon: Double,
    val date: String? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "direction" to direction,
        "lat" to lat,
        "lon" to lon,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionDeclinationRequest(
    val lat: Double,
    val lon: Double,
    val date: String? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "lat" to lat,
        "lon" to lon,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionSunPathRequest(
    val lat: Double,
    val lon: Double,
    val date: String? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "lat" to lat,
        "lon" to lon,
        "date" to date
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuDirectionZoneFromBearingRequest(
    val bearingDeg: Double,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuElementsBalanceSuggestRequest(
    val distribution: JSONObject? = null,
    val deficient: List<String>? = null,
    val excess: List<String>? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val rooms: List<VastuElementsDistributionRequestRoomsItem>,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "rooms" to rooms
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuEntranceObstructionCheckRequest(
    val feature: String,
    val houseHeightMeters: Double? = null,
    val distanceMeters: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "feature" to feature,
        "houseHeightMeters" to houseHeightMeters,
        "distanceMeters" to distanceMeters
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuEntrancePadaRequest(
    val plotPolygon: List<List<Double>>,
    val doorXY: List<Double>,
    val bearingDeg: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plotPolygon" to plotPolygon,
        "doorXY" to doorXY,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuEntranceRecommendRequest(
    val facing: String,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "facing" to facing
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuFloorLevelAnalysisRequest(
    val levels: VastuJsonObjectOrArray? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "levels" to levels
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuFusionChartRequest(
    val datetime: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val facing: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val doorXY: List<Double>? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMandalaProject9ZoneRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMandalaProjectBrahmasthanRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuMultiStoreyFloorRulesRequest(
    val floors: Int,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val pada: Int? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val area: Double? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to if (multipolygons == null) polygon else null,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanAnalyzeRequest(
    val rooms: List<VastuPlanAnalyzeRequestRoomsItem>,
    val plot: JSONObject? = null,
    val zoneResolution: Int? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val merchantCatalogId: String? = null,
    val rulesVersion: String? = null,
    val receipt: Boolean? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val units: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rulesVersion" to rulesVersion,
        "receipt" to receipt,
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "units" to units,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "rooms" to rooms,
        "plot" to plot,
        "zoneResolution" to zoneResolution ,
        "merchantCatalogId" to merchantCatalogId
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
    val setbacks: VastuPlanFromRequirementsRequestPlotSetbacks? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "width" to width,
        "length" to length,
        "facing" to facing,
        "polygon" to if (multipolygons == null) polygon else null,
        "setbacks" to setbacks
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanFromRequirementsRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to if (multipolygons == null) polygon else null,
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
    val includeSvg: Boolean? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val plotPolygon: List<Any>? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val units: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "units" to units,
        "plotPolygon" to plotPolygon,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plot" to plot,
        "entrance" to entrance,
        "rooms" to rooms,
        "requirements" to requirements,
        "parking" to parking,
        "staircase" to staircase,
        "lift" to lift,
        "variants" to variants,
        "variantSvg" to variantSvg,
        "includeSvg" to includeSvg ,
        "merchantCatalogId" to merchantCatalogId
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
    val setbacks: VastuPlanGenerateRequestPlotSetbacks? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "width" to width,
        "length" to length,
        "facing" to facing,
        "polygon" to if (multipolygons == null) polygon else null,
        "setbacks" to setbacks
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanGenerateRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to if (multipolygons == null) polygon else null,
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
    val includeSvg: Boolean? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val plotPolygon: List<Any>? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val units: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "units" to units,
        "plotPolygon" to plotPolygon,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plot" to plot,
        "entrance" to entrance,
        "rooms" to rooms,
        "requirements" to requirements,
        "parking" to parking,
        "staircase" to staircase,
        "lift" to lift,
        "variants" to variants,
        "variantSvg" to variantSvg,
        "includeSvg" to includeSvg ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanOptimizeRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to if (multipolygons == null) polygon else null,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanOptimizeRequest(
    val rooms: List<VastuPlanOptimizeRequestRoomsItem>,
    val plot: JSONObject? = null,
    val includeSvg: Boolean? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null
,
    val constraints: VastuOptimizationConstraints? = null,
    val plotPolygon: List<Any>? = null,
    val lockedRooms: List<String>? = null,
    val wetShafts: List<JSONObject>? = null,
    val plumbingStacks: List<JSONObject>? = null,
    val loadBearingWalls: List<JSONObject>? = null,
    val columns: List<JSONObject>? = null,
    val minSizes: JSONObject? = null,
    val id: String? = null,
    val locked: Boolean? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val units: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "units" to units,
        "id" to id,
        "locked" to locked,

        "plotPolygon" to plotPolygon,
        "lockedRooms" to lockedRooms,
        "wetShafts" to wetShafts,
        "plumbingStacks" to plumbingStacks,
        "loadBearingWalls" to loadBearingWalls,
        "columns" to columns,
        "minSizes" to minSizes,

        "constraints" to constraints,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "rooms" to rooms,
        "plot" to plot,
        "includeSvg" to includeSvg ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanReportRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to if (multipolygons == null) polygon else null,
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

data class VastuPlanReportRequestCompositionCtaBlocksItem(
    val label: String,
    val link: String,
    val phone: String? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "label" to label,
        "link" to link,
        "phone" to phone
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanReportRequestComposition(
    val sections: List<String>? = null,
    val intro: String? = null,
    val outro: String? = null,
    val ctaBlocks: List<VastuPlanReportRequestCompositionCtaBlocksItem>? = null) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "sections" to sections,
        "intro" to intro,
        "outro" to outro,
        "ctaBlocks" to ctaBlocks
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanReportRequest(
    val rooms: List<VastuPlanReportRequestRoomsItem>,
    val plot: JSONObject? = null,
    val format: String? = null,
    val brand: VastuPlanReportRequestBrand? = null,
    val reportTitle: String? = null,
    val generatedFor: String? = null,
    val tenantName: String? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val composition: VastuPlanReportRequestComposition? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val units: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "units" to units,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "rooms" to rooms,
        "plot" to plot,
        "format" to format,
        "brand" to brand,
        "reportTitle" to reportTitle,
        "generatedFor" to generatedFor,
        "tenantName" to tenantName,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "composition" to composition,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanUploadRequestRoomsItem(
    val name: String,
    val roomType: String? = null,
    val zone: String? = null,
    val direction: String? = null,
    val polygon: List<List<Double>>? = null,
    val area: Double? = null,
    val holes: List<List<List<Double>>>? = null,
    val multipolygons: List<JSONObject>? = null
) : VastuEncodable {
    override fun toMap(): Map<String, Any?> = mapOf(
        "multipolygons" to multipolygons,
        "holes" to holes,
        "name" to name,
        "roomType" to roomType,
        "zone" to zone,
        "direction" to direction,
        "polygon" to if (multipolygons == null) polygon else null,
        "area" to area
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanImportImageRequest(val fileBase64: String, val northBearingDeg: Double? = null, val scaleMetersPerUnit: Double? = null, val units: String? = null, val inputUnits: String? = null, val scaleInputUnitsPerUnit: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "fileBase64" to fileBase64,
        "units" to units,
        "inputUnits" to inputUnits,
        "scaleInputUnitsPerUnit" to scaleInputUnitsPerUnit,
        "northBearingDeg" to northBearingDeg,
        "scaleMetersPerUnit" to scaleMetersPerUnit,
    ).filterValues { it != null }
}

data class VastuPlanImportPdfRequest(val fileBase64: String, val northBearingDeg: Double? = null, val scaleMetersPerUnit: Double? = null, val page: Int, val units: String? = null, val inputUnits: String? = null, val scaleInputUnitsPerUnit: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "fileBase64" to fileBase64,
        "units" to units,
        "inputUnits" to inputUnits,
        "scaleInputUnitsPerUnit" to scaleInputUnitsPerUnit,
        "northBearingDeg" to northBearingDeg,
        "scaleMetersPerUnit" to scaleMetersPerUnit,
        "page" to page,
    ).filterValues { it != null }
}

data class VastuPlanUploadRequest(
    val rooms: List<VastuPlanUploadRequestRoomsItem>? = null,
    val layout: JSONObject? = null,
    val asciiGrid: String? = null,
    val plot: JSONObject? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val units: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "units" to units,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "rooms" to rooms,
        "layout" to layout,
        "asciiGrid" to asciiGrid,
        "plot" to plot ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotExtensionsCutsRequest(
    val plotPolygon: List<List<Double>>? = null,
    val length: Double? = null,
    val width: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plotPolygon" to plotPolygon,
        "length" to length,
        "width" to width
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotOrientationRequest(
    val facingBearingDeg: Double? = null,
    val bearingDeg: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "facingBearingDeg" to facingBearingDeg,
        "bearingDeg" to bearingDeg
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotRatioRequest(
    val plotPolygon: List<List<Double>>,
    val bearingDeg: Double? = null,
    val doorXY: List<Double>? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val roadThrustFrom: String? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val doorXY: List<Double>? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,
        "doorXY" to doorXY
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlotSlopeRequest(
    val slopeDirection: String? = null,
    val lowSide: String? = null,
    val lowCorner: String? = null,
    val slopeBearingDeg: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomDiningRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomKitchenRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomLivingRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomPoojaRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomStaircaseRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomStoreRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomStudyRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomToiletRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuRoomWaterStorageRequest(
    val zone: String? = null,
    val direction: String? = null,
    val proposedZone: String? = null,
    val proposedDirection: String? = null,
    val placement: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val pointXY: List<Double>? = null,
    val plotPolygon: List<List<Double>>? = null,
    val bearingDeg: Double? = null,
    val merchantCatalogId: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "pointXY" to pointXY,
        "plotPolygon" to plotPolygon,
        "bearingDeg" to bearingDeg,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
        "zone" to zone,
        "direction" to direction,
        "proposedZone" to proposedZone,
        "proposedDirection" to proposedDirection,
        "placement" to placement,
        "latitude" to latitude,
        "longitude" to longitude ,
        "merchantCatalogId" to merchantCatalogId
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
    val plot: JSONObject? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val rulesVersion: String? = null,
    val receipt: Boolean? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rulesVersion" to rulesVersion,
        "receipt" to receipt,
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val plot: JSONObject? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val rulesVersion: String? = null,
    val receipt: Boolean? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rulesVersion" to rulesVersion,
        "receipt" to receipt,
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val plot: JSONObject? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val rulesVersion: String? = null,
    val receipt: Boolean? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "rulesVersion" to rulesVersion,
        "receipt" to receipt,
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val lon: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val lon: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val lon: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val lon: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val lon: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val lon: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val lon: Double? = null,
    val headingErrorDeg: Double? = null,
    val positionErrorM: Double? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "headingErrorDeg" to headingErrorDeg,
        "positionErrorM" to positionErrorM,
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
    val windowDays: Int? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val windowDays: Int? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val windowDays: Int? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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
    val windowDays: Int? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

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

data class VastuArAttestationChallengeDataDeviceAttestation(val raw: JSONObject) {
    val status: String
        get() = raw.getString("status")
    val platform: String
        get() = raw.getString("platform")
}

data class VastuArCaptureMergeDataPairResidualsItem(val raw: JSONObject) {
    val controlCount: Int?
        get() = if (!raw.has("controlCount") || raw.isNull("controlCount")) null else raw.getInt("controlCount")
    val rmsResidualM: Double
        get() = raw.getDouble("rmsResidualM")
    val maxResidualM: Double
        get() = raw.getDouble("maxResidualM")
    val fromCaptureId: String
        get() = raw.getString("fromCaptureId")
    val toCaptureId: String
        get() = raw.getString("toCaptureId")
}

data class VastuArCaptureMergeDataPricing(val raw: JSONObject) {
    val attributableCostUsd: String
        get() = raw.getString("attributableCostUsd")
    val markupMultiplier: Int
        get() = raw.getInt("markupMultiplier")
    val computedPriceUsd: String
        get() = raw.getString("computedPriceUsd")
    val settledChargeUsd: String
        get() = raw.getString("settledChargeUsd")
    val settlement: String
        get() = raw.getString("settlement")
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

data class VastuArRoomCaptureDataCaptureOutlineGeometry(val raw: JSONObject) {
    val polygon: Any?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else raw.get("polygon").takeUnless { it == JSONObject.NULL }
    val holes: List<Any?>?
        get() = if (!raw.has("holes") || raw.isNull("holes")) null else List(raw.getJSONArray("holes").length()) { index0 -> raw.getJSONArray("holes").get(index0).takeUnless { it == JSONObject.NULL } }
    val multipolygons: List<Any?>?
        get() = if (!raw.has("multipolygons") || raw.isNull("multipolygons")) null else List(raw.getJSONArray("multipolygons").length()) { index0 -> raw.getJSONArray("multipolygons").get(index0).takeUnless { it == JSONObject.NULL } }
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
    val geometry: VastuArRoomCaptureDataCaptureOutlineGeometry?
        get() = if (!raw.has("geometry") || raw.isNull("geometry")) null else VastuArRoomCaptureDataCaptureOutlineGeometry(raw.getJSONObject("geometry"))
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
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
}

data class VastuArRoomCaptureDataRoomsItemGeometry(val raw: JSONObject) {
    val polygon: Any?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else raw.get("polygon").takeUnless { it == JSONObject.NULL }
    val holes: List<Any?>?
        get() = if (!raw.has("holes") || raw.isNull("holes")) null else List(raw.getJSONArray("holes").length()) { index0 -> raw.getJSONArray("holes").get(index0).takeUnless { it == JSONObject.NULL } }
    val multipolygons: List<Any?>?
        get() = if (!raw.has("multipolygons") || raw.isNull("multipolygons")) null else List(raw.getJSONArray("multipolygons").length()) { index0 -> raw.getJSONArray("multipolygons").get(index0).takeUnless { it == JSONObject.NULL } }
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
    val geometry: VastuArRoomCaptureDataRoomsItemGeometry?
        get() = if (!raw.has("geometry") || raw.isNull("geometry")) null else VastuArRoomCaptureDataRoomsItemGeometry(raw.getJSONObject("geometry"))
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

data class VastuArRoomCaptureDataAuditStatus(val raw: JSONObject) {
    val status: String
        get() = raw.getString("status")
    val reasons: List<String>
        get() = List(raw.getJSONArray("reasons").length()) { index0 -> (raw.getJSONArray("reasons").get(index0) as String) }
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

data class VastuArScanQualityDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
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

data class VastuArchiveDeleteDataErasureReceiptRemovedItem(val raw: JSONObject) {
    val kind: String
        get() = raw.getString("kind")
    val recordId: String?
        get() = if (!raw.has("recordId") || raw.isNull("recordId")) null else raw.getString("recordId")
    val artifactId: String?
        get() = if (!raw.has("artifactId") || raw.isNull("artifactId")) null else raw.getString("artifactId")
    val artifactHash: String?
        get() = if (!raw.has("artifactHash") || raw.isNull("artifactHash")) null else raw.getString("artifactHash")
    val versionHash: String?
        get() = if (!raw.has("versionHash") || raw.isNull("versionHash")) null else raw.getString("versionHash")
    val deleteMarker: Boolean?
        get() = if (!raw.has("deleteMarker") || raw.isNull("deleteMarker")) null else raw.getBoolean("deleteMarker")
    val versionCount: Int?
        get() = if (!raw.has("versionCount") || raw.isNull("versionCount")) null else raw.getInt("versionCount")
    val deleteMarkerCount: Int?
        get() = if (!raw.has("deleteMarkerCount") || raw.isNull("deleteMarkerCount")) null else raw.getInt("deleteMarkerCount")
    val versionsSha256: String?
        get() = if (!raw.has("versionsSha256") || raw.isNull("versionsSha256")) null else raw.getString("versionsSha256")
}

data class VastuArchiveDeleteDataErasureReceiptRetainedItem(val raw: JSONObject) {
    val kind: String
        get() = raw.getString("kind")
    val purpose: String
        get() = raw.getString("purpose")
}

data class VastuArchiveDeleteDataErasureReceipt(val raw: JSONObject) {
    val schemaVersion: Int
        get() = raw.getInt("schemaVersion")
    val propertyId: String
        get() = raw.getString("propertyId")
    val status: String
        get() = raw.getString("status")
    val scope: String
        get() = raw.getString("scope")
    val completedAt: String
        get() = raw.getString("completedAt")
    val removed: List<VastuArchiveDeleteDataErasureReceiptRemovedItem>
        get() = List(raw.getJSONArray("removed").length()) { index0 -> VastuArchiveDeleteDataErasureReceiptRemovedItem((raw.getJSONArray("removed").get(index0) as JSONObject)) }
    val retained: List<VastuArchiveDeleteDataErasureReceiptRetainedItem>
        get() = List(raw.getJSONArray("retained").length()) { index0 -> VastuArchiveDeleteDataErasureReceiptRetainedItem((raw.getJSONArray("retained").get(index0) as JSONObject)) }
    val backupRetentionDays: Int
        get() = raw.getInt("backupRetentionDays")
    val backupPolicy: String
        get() = raw.getString("backupPolicy")
    val hashAlgorithm: String
        get() = raw.getString("hashAlgorithm")
    val receiptHash: String
        get() = raw.getString("receiptHash")
    val linkedScanIds: List<String>?
        get() = if (!raw.has("linkedScanIds") || raw.isNull("linkedScanIds")) null else List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>?
        get() = if (!raw.has("linkedAssessmentIds") || raw.isNull("linkedAssessmentIds")) null else List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val deletedByAccountHash: String
        get() = raw.getString("deletedByAccountHash")
    val deletedAtEpoch: Long
        get() = raw.getLong("deletedAtEpoch")
    val revisionId: String?
        get() = if (!raw.has("revisionId") || raw.isNull("revisionId")) null else raw.getString("revisionId")
    val receiptId: String?
        get() = if (!raw.has("receiptId") || raw.isNull("receiptId")) null else raw.getString("receiptId")
}

data class VastuArchiveSummaryDataArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuArchiveTierDataIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuArchiveTierDataArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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

data class VastuAssessmentDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuAssessmentDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
}

data class VastuBrahmasthanProjectionDataGridFrame(val raw: JSONObject) {
    val orientation: String?
        get() = if (!raw.has("orientation") || raw.isNull("orientation")) null else raw.getString("orientation")
    val fittedTo: String?
        get() = if (!raw.has("fittedTo") || raw.isNull("fittedTo")) null else raw.getString("fittedTo")
    val rotationDeg: Double?
        get() = if (!raw.has("rotationDeg") || raw.isNull("rotationDeg")) null else raw.getDouble("rotationDeg")
    val gridBoxArea: Double?
        get() = if (!raw.has("gridBoxArea") || raw.isNull("gridBoxArea")) null else raw.getDouble("gridBoxArea")
    val plotAreaShareOfGridBox: Double?
        get() = if (!raw.has("plotAreaShareOfGridBox") || raw.isNull("plotAreaShareOfGridBox")) null else raw.getDouble("plotAreaShareOfGridBox")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
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

data class VastuCatalogReferenceDataZoneRemediesItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedyParams: JSONObject?
        get() = if (!raw.has("remedyParams") || raw.isNull("remedyParams")) null else raw.getJSONObject("remedyParams")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
}

data class VastuChatUploadDataBilling(val raw: JSONObject) {
    val chargedCents: Int?
        get() = if (!raw.has("chargedCents") || raw.isNull("chargedCents")) null else raw.getInt("chargedCents")
    val balanceAfterCents: Int?
        get() = if (!raw.has("balanceAfterCents") || raw.isNull("balanceAfterCents")) null else raw.getInt("balanceAfterCents")
    val currency: String?
        get() = if (!raw.has("currency") || raw.isNull("currency")) null else raw.getString("currency")
}

data class VastuCompareVersionsDataChangesItem(val raw: JSONObject) {
    val ruleId: String
        get() = raw.getString("ruleId")
    val before: Any?
        get() = raw.get("before").takeUnless { it == JSONObject.NULL }
    val after: Any?
        get() = raw.get("after").takeUnless { it == JSONObject.NULL }
    val reason: String
        get() = raw.getString("reason")
    val fromReason: JSONObject?
        get() = if (!raw.has("fromReason") || raw.isNull("fromReason")) null else raw.getJSONObject("fromReason")
    val toReason: JSONObject?
        get() = if (!raw.has("toReason") || raw.isNull("toReason")) null else raw.getJSONObject("toReason")
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

data class VastuComplianceIndexDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuComplianceIndexDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
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

data class VastuDetailedFloorPlanAuditDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuDetailedFloorPlanAuditDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
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

data class VastuFloorPlanAuditDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuFloorPlanAuditDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
}

data class VastuFusionChartDataCautionDirectionsItem(val raw: JSONObject) {
    val direction: String
        get() = raw.getString("direction")
    val deity: String?
        get() = if (!raw.has("deity") || raw.isNull("deity")) null else raw.getString("deity")
    val element: String?
        get() = if (!raw.has("element") || raw.isNull("element")) null else raw.getString("element")
    val ruledBy: String?
        get() = if (!raw.has("ruledBy") || raw.isNull("ruledBy")) null else raw.getString("ruledBy")
    val strengthPct: Double?
        get() = if (!raw.has("strengthPct") || raw.isNull("strengthPct")) null else raw.getDouble("strengthPct")
    val rationale: String
        get() = raw.getString("rationale")
    val avoidUse: List<String>?
        get() = if (!raw.has("avoidUse") || raw.isNull("avoidUse")) null else List(raw.getJSONArray("avoidUse").length()) { index0 -> (raw.getJSONArray("avoidUse").get(index0) as String) }
}

data class VastuJobStatusDataCounts(val raw: JSONObject) {
    val succeeded: Int
        get() = raw.getInt("succeeded")
    val failed: Int
        get() = raw.getInt("failed")
    val pending: Int
        get() = raw.getInt("pending")
    val cancelled: Int
        get() = raw.getInt("cancelled")
}

data class VastuJobStatusDataBilling(val raw: JSONObject) {
    val currency: String
        get() = raw.getString("currency")
    val pricePerItem: Double
        get() = raw.getDouble("pricePerItem")
    val maxCharge: Double
        get() = raw.getDouble("maxCharge")
    val charged: Double
        get() = raw.getDouble("charged")
    val basis: String
        get() = raw.getString("basis")
}

data class VastuMandalaProjectionDataGridFrame(val raw: JSONObject) {
    val orientation: String?
        get() = if (!raw.has("orientation") || raw.isNull("orientation")) null else raw.getString("orientation")
    val fittedTo: String?
        get() = if (!raw.has("fittedTo") || raw.isNull("fittedTo")) null else raw.getString("fittedTo")
    val rotationDeg: Double?
        get() = if (!raw.has("rotationDeg") || raw.isNull("rotationDeg")) null else raw.getDouble("rotationDeg")
    val gridBoxArea: Double?
        get() = if (!raw.has("gridBoxArea") || raw.isNull("gridBoxArea")) null else raw.getDouble("gridBoxArea")
    val plotAreaShareOfGridBox: Double?
        get() = if (!raw.has("plotAreaShareOfGridBox") || raw.isNull("plotAreaShareOfGridBox")) null else raw.getDouble("plotAreaShareOfGridBox")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
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

data class VastuMeasurementUncertaintyResultsItem(val raw: JSONObject) {
    val id: Any?
        get() = if (!raw.has("id") || raw.isNull("id")) null else raw.get("id").takeUnless { it == JSONObject.NULL }
    val pointResult: JSONObject
        get() = raw.getJSONObject("pointResult")
    val possibleZones: List<String>
        get() = List(raw.getJSONArray("possibleZones").length()) { index0 -> (raw.getJSONArray("possibleZones").get(index0) as String) }
    val possiblePadas: List<JSONObject>
        get() = List(raw.getJSONArray("possiblePadas").length()) { index0 -> (raw.getJSONArray("possiblePadas").get(index0) as JSONObject) }
    val stable: Boolean
        get() = raw.getBoolean("stable")
    val bearingMarginDeg: Double?
        get() = if (!raw.has("bearingMarginDeg") || raw.isNull("bearingMarginDeg")) null else raw.getDouble("bearingMarginDeg")
    val headingErrorDeg: Double?
        get() = if (!raw.has("headingErrorDeg") || raw.isNull("headingErrorDeg")) null else raw.getDouble("headingErrorDeg")
    val positionErrorM: Double?
        get() = if (!raw.has("positionErrorM") || raw.isNull("positionErrorM")) null else raw.getDouble("positionErrorM")
    val method: String?
        get() = if (!raw.has("method") || raw.isNull("method")) null else raw.getString("method")
}

data class VastuMeasurementUncertaintyBounds(val raw: JSONObject) {
    val headingErrorDeg: Double
        get() = raw.getDouble("headingErrorDeg")
    val positionErrorM: Double
        get() = raw.getDouble("positionErrorM")
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

data class VastuOverallScoreDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuOverallScoreDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
}

data class VastuPlanAuditDataRoomByRoomItemMappedItemsItem(val raw: JSONObject) {
    val remedyKey: String
        get() = raw.getString("remedyKey")
    val itemId: String
        get() = raw.getString("itemId")
    val kind: String
        get() = raw.getString("kind")
    val label: String
        get() = raw.getString("label")
    val availability: String
        get() = raw.getString("availability")
    val link: String?
        get() = if (!raw.has("link") || raw.isNull("link")) null else raw.getString("link")
    val referralRef: String?
        get() = if (!raw.has("referralRef") || raw.isNull("referralRef")) null else raw.getString("referralRef")
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
    val statedZone: String?
        get() = if (!raw.has("statedZone") || raw.isNull("statedZone")) null else raw.getString("statedZone")
    val zoneConflict: Boolean?
        get() = if (!raw.has("zoneConflict") || raw.isNull("zoneConflict")) null else raw.getBoolean("zoneConflict")
    val mappedItems: List<VastuPlanAuditDataRoomByRoomItemMappedItemsItem>?
        get() = if (!raw.has("mappedItems") || raw.isNull("mappedItems")) null else List(raw.getJSONArray("mappedItems").length()) { index0 -> VastuPlanAuditDataRoomByRoomItemMappedItemsItem((raw.getJSONArray("mappedItems").get(index0) as JSONObject)) }
}

data class VastuPlanAuditDataDefectsItemMappedItemsItem(val raw: JSONObject) {
    val remedyKey: String
        get() = raw.getString("remedyKey")
    val itemId: String
        get() = raw.getString("itemId")
    val kind: String
        get() = raw.getString("kind")
    val label: String
        get() = raw.getString("label")
    val availability: String
        get() = raw.getString("availability")
    val link: String?
        get() = if (!raw.has("link") || raw.isNull("link")) null else raw.getString("link")
    val referralRef: String?
        get() = if (!raw.has("referralRef") || raw.isNull("referralRef")) null else raw.getString("referralRef")
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
    val mappedItems: List<VastuPlanAuditDataDefectsItemMappedItemsItem>?
        get() = if (!raw.has("mappedItems") || raw.isNull("mappedItems")) null else List(raw.getJSONArray("mappedItems").length()) { index0 -> VastuPlanAuditDataDefectsItemMappedItemsItem((raw.getJSONArray("mappedItems").get(index0) as JSONObject)) }
}

data class VastuPlanAuditDataRemediesItemMappedItemsItem(val raw: JSONObject) {
    val remedyKey: String
        get() = raw.getString("remedyKey")
    val itemId: String
        get() = raw.getString("itemId")
    val kind: String
        get() = raw.getString("kind")
    val label: String
        get() = raw.getString("label")
    val availability: String
        get() = raw.getString("availability")
    val link: String?
        get() = if (!raw.has("link") || raw.isNull("link")) null else raw.getString("link")
    val referralRef: String?
        get() = if (!raw.has("referralRef") || raw.isNull("referralRef")) null else raw.getString("referralRef")
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
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedyParams: JSONObject?
        get() = if (!raw.has("remedyParams") || raw.isNull("remedyParams")) null else raw.getJSONObject("remedyParams")
    val mappedItems: List<VastuPlanAuditDataRemediesItemMappedItemsItem>?
        get() = if (!raw.has("mappedItems") || raw.isNull("mappedItems")) null else List(raw.getJSONArray("mappedItems").length()) { index0 -> VastuPlanAuditDataRemediesItemMappedItemsItem((raw.getJSONArray("mappedItems").get(index0) as JSONObject)) }
}

data class VastuPlanAuditDataTracedGeometryRegionGeometry(val raw: JSONObject) {
    val polygon: Any?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else raw.get("polygon").takeUnless { it == JSONObject.NULL }
    val holes: List<Any?>?
        get() = if (!raw.has("holes") || raw.isNull("holes")) null else List(raw.getJSONArray("holes").length()) { index0 -> raw.getJSONArray("holes").get(index0).takeUnless { it == JSONObject.NULL } }
    val multipolygons: List<Any?>?
        get() = if (!raw.has("multipolygons") || raw.isNull("multipolygons")) null else List(raw.getJSONArray("multipolygons").length()) { index0 -> raw.getJSONArray("multipolygons").get(index0).takeUnless { it == JSONObject.NULL } }
}

data class VastuPlanAuditDataTracedGeometryRegionBrahmasthan(val raw: JSONObject) {
    val pole: List<Double>?
        get() = if (!raw.has("pole") || raw.isNull("pole")) null else List(raw.getJSONArray("pole").length()) { index0 -> (raw.getJSONArray("pole").get(index0) as Number).toDouble() }
    val poleInside: Boolean?
        get() = if (!raw.has("poleInside") || raw.isNull("poleInside")) null else raw.getBoolean("poleInside")
    val basis: String?
        get() = if (!raw.has("basis") || raw.isNull("basis")) null else raw.getString("basis")
    val netArea: Double?
        get() = if (!raw.has("netArea") || raw.isNull("netArea")) null else raw.getDouble("netArea")
}

data class VastuPlanAuditDataTracedGeometryRegionGridZonesItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
}

data class VastuPlanAuditDataTracedGeometryRegionSectorsItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
}

data class VastuPlanAuditDataTracedGeometryRegion(val raw: JSONObject) {
    val geometry: VastuPlanAuditDataTracedGeometryRegionGeometry?
        get() = if (!raw.has("geometry") || raw.isNull("geometry")) null else VastuPlanAuditDataTracedGeometryRegionGeometry(raw.getJSONObject("geometry"))
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
    val centroid: List<Double>?
        get() = if (!raw.has("centroid") || raw.isNull("centroid")) null else List(raw.getJSONArray("centroid").length()) { index0 -> (raw.getJSONArray("centroid").get(index0) as Number).toDouble() }
    val centroidInside: Boolean?
        get() = if (!raw.has("centroidInside") || raw.isNull("centroidInside")) null else raw.getBoolean("centroidInside")
    val brahmasthan: VastuPlanAuditDataTracedGeometryRegionBrahmasthan?
        get() = if (!raw.has("brahmasthan") || raw.isNull("brahmasthan")) null else VastuPlanAuditDataTracedGeometryRegionBrahmasthan(raw.getJSONObject("brahmasthan"))
    val gridZones: List<VastuPlanAuditDataTracedGeometryRegionGridZonesItem>?
        get() = if (!raw.has("gridZones") || raw.isNull("gridZones")) null else List(raw.getJSONArray("gridZones").length()) { index0 -> VastuPlanAuditDataTracedGeometryRegionGridZonesItem((raw.getJSONArray("gridZones").get(index0) as JSONObject)) }
    val sectors: List<VastuPlanAuditDataTracedGeometryRegionSectorsItem>?
        get() = if (!raw.has("sectors") || raw.isNull("sectors")) null else List(raw.getJSONArray("sectors").length()) { index0 -> VastuPlanAuditDataTracedGeometryRegionSectorsItem((raw.getJSONArray("sectors").get(index0) as JSONObject)) }
}

data class VastuPlanAuditDataTracedGeometry(val raw: JSONObject) {
    val region: VastuPlanAuditDataTracedGeometryRegion?
        get() = if (!raw.has("region") || raw.isNull("region")) null else VastuPlanAuditDataTracedGeometryRegion(raw.getJSONObject("region"))
}

data class VastuPlanAuditDataArtifact(val raw: JSONObject) {
    val contentType: String
        get() = raw.getString("contentType")
    val filename: String
        get() = raw.getString("filename")
    val content: String
        get() = raw.getString("content")
    val encoding: String?
        get() = if (!raw.has("encoding") || raw.isNull("encoding")) null else raw.getString("encoding")
}

data class VastuPlanAuditDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuPlanAuditDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
}

data class VastuPlanExportIfcDataStoreysItem(val raw: JSONObject) {
    val level: Int
        get() = raw.getInt("level")
    val name: String
        get() = raw.getString("name")
    val elevationM: Double
        get() = raw.getDouble("elevationM")
    val roomCount: Int
        get() = raw.getInt("roomCount")
}

data class VastuPlanGenerateDataFloorsItem(val raw: JSONObject) {
    val level: Int
        get() = raw.getInt("level")
    val label: String
        get() = raw.getString("label")
    val rooms: List<String>
        get() = List(raw.getJSONArray("rooms").length()) { index0 -> (raw.getJSONArray("rooms").get(index0) as String) }
    val roomCount: Int
        get() = raw.getInt("roomCount")
    val openTerrace: Boolean
        get() = raw.getBoolean("openTerrace")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val compliance: JSONObject?
        get() = if (!raw.has("compliance") || raw.isNull("compliance")) null else raw.getJSONObject("compliance")
    val placedArea: Double?
        get() = if (!raw.has("placedArea") || raw.isNull("placedArea")) null else raw.getDouble("placedArea")
    val utilisationPct: Double?
        get() = if (!raw.has("utilisationPct") || raw.isNull("utilisationPct")) null else raw.getDouble("utilisationPct")
    val svg: String?
        get() = if (!raw.has("svg") || raw.isNull("svg")) null else raw.getString("svg")
}

data class VastuPlanGenerateDataWarningsItem(val raw: JSONObject) {
    val code: String
        get() = raw.getString("code")
    val floor: Int?
        get() = if (!raw.has("floor") || raw.isNull("floor")) null else raw.getInt("floor")
    val label: String?
        get() = if (!raw.has("label") || raw.isNull("label")) null else raw.getString("label")
    val field: String
        get() = raw.getString("field")
    val message: String
        get() = raw.getString("message")
}

data class VastuPlanGenerateDataPlotRegionGeometry(val raw: JSONObject) {
    val polygon: Any?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else raw.get("polygon").takeUnless { it == JSONObject.NULL }
    val holes: List<Any?>?
        get() = if (!raw.has("holes") || raw.isNull("holes")) null else List(raw.getJSONArray("holes").length()) { index0 -> raw.getJSONArray("holes").get(index0).takeUnless { it == JSONObject.NULL } }
    val multipolygons: List<Any?>?
        get() = if (!raw.has("multipolygons") || raw.isNull("multipolygons")) null else List(raw.getJSONArray("multipolygons").length()) { index0 -> raw.getJSONArray("multipolygons").get(index0).takeUnless { it == JSONObject.NULL } }
}

data class VastuPlanGenerateDataPlotRegionBrahmasthan(val raw: JSONObject) {
    val pole: List<Double>?
        get() = if (!raw.has("pole") || raw.isNull("pole")) null else List(raw.getJSONArray("pole").length()) { index0 -> (raw.getJSONArray("pole").get(index0) as Number).toDouble() }
    val poleInside: Boolean?
        get() = if (!raw.has("poleInside") || raw.isNull("poleInside")) null else raw.getBoolean("poleInside")
    val basis: String?
        get() = if (!raw.has("basis") || raw.isNull("basis")) null else raw.getString("basis")
    val netArea: Double?
        get() = if (!raw.has("netArea") || raw.isNull("netArea")) null else raw.getDouble("netArea")
}

data class VastuPlanGenerateDataPlotRegionGridZonesItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
}

data class VastuPlanGenerateDataPlotRegionSectorsItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
}

data class VastuPlanGenerateDataPlotRegion(val raw: JSONObject) {
    val geometry: VastuPlanGenerateDataPlotRegionGeometry?
        get() = if (!raw.has("geometry") || raw.isNull("geometry")) null else VastuPlanGenerateDataPlotRegionGeometry(raw.getJSONObject("geometry"))
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
    val centroid: List<Double>?
        get() = if (!raw.has("centroid") || raw.isNull("centroid")) null else List(raw.getJSONArray("centroid").length()) { index0 -> (raw.getJSONArray("centroid").get(index0) as Number).toDouble() }
    val centroidInside: Boolean?
        get() = if (!raw.has("centroidInside") || raw.isNull("centroidInside")) null else raw.getBoolean("centroidInside")
    val brahmasthan: VastuPlanGenerateDataPlotRegionBrahmasthan?
        get() = if (!raw.has("brahmasthan") || raw.isNull("brahmasthan")) null else VastuPlanGenerateDataPlotRegionBrahmasthan(raw.getJSONObject("brahmasthan"))
    val gridZones: List<VastuPlanGenerateDataPlotRegionGridZonesItem>?
        get() = if (!raw.has("gridZones") || raw.isNull("gridZones")) null else List(raw.getJSONArray("gridZones").length()) { index0 -> VastuPlanGenerateDataPlotRegionGridZonesItem((raw.getJSONArray("gridZones").get(index0) as JSONObject)) }
    val sectors: List<VastuPlanGenerateDataPlotRegionSectorsItem>?
        get() = if (!raw.has("sectors") || raw.isNull("sectors")) null else List(raw.getJSONArray("sectors").length()) { index0 -> VastuPlanGenerateDataPlotRegionSectorsItem((raw.getJSONArray("sectors").get(index0) as JSONObject)) }
}

data class VastuPlanImportDataPlanPlot(val raw: JSONObject) {
    val polygon: List<List<Double>>
        get() = List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val width: Double?
        get() = if (!raw.has("width") || raw.isNull("width")) null else raw.getDouble("width")
    val length: Double?
        get() = if (!raw.has("length") || raw.isNull("length")) null else raw.getDouble("length")
}

data class VastuPlanImportDataPlanRoomsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val name: String
        get() = raw.getString("name")
    val roomType: String
        get() = raw.getString("roomType")
    val label: String
        get() = raw.getString("label")
    val polygon: List<List<Double>>
        get() = List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
}

data class VastuPlanImportDataPlanOpeningsItem(val raw: JSONObject) {
    val start: List<Double>
        get() = List(raw.getJSONArray("start").length()) { index0 -> (raw.getJSONArray("start").get(index0) as Number).toDouble() }
    val end: List<Double>
        get() = List(raw.getJSONArray("end").length()) { index0 -> (raw.getJSONArray("end").get(index0) as Number).toDouble() }
    val kind: String
        get() = raw.getString("kind")
}

data class VastuPlanImportDataPlanEntrance(val raw: JSONObject) {
    val start: List<Double>
        get() = List(raw.getJSONArray("start").length()) { index0 -> (raw.getJSONArray("start").get(index0) as Number).toDouble() }
    val end: List<Double>
        get() = List(raw.getJSONArray("end").length()) { index0 -> (raw.getJSONArray("end").get(index0) as Number).toDouble() }
    val kind: String
        get() = raw.getString("kind")
}

data class VastuPlanImportDataPlanImportReview(val raw: JSONObject) {
    val northKnown: Boolean
        get() = raw.getBoolean("northKnown")
    val scaleKnown: Boolean
        get() = raw.getBoolean("scaleKnown")
    val analysisReady: Boolean
        get() = raw.getBoolean("analysisReady")
    val coordinateFrame: String
        get() = raw.getString("coordinateFrame")
    val units: String
        get() = raw.getString("units")
}

data class VastuPlanImportDataPlan(val raw: JSONObject) {
    val plot: VastuPlanImportDataPlanPlot
        get() = VastuPlanImportDataPlanPlot(raw.getJSONObject("plot"))
    val rooms: List<VastuPlanImportDataPlanRoomsItem>
        get() = List(raw.getJSONArray("rooms").length()) { index0 -> VastuPlanImportDataPlanRoomsItem((raw.getJSONArray("rooms").get(index0) as JSONObject)) }
    val openings: List<VastuPlanImportDataPlanOpeningsItem>
        get() = List(raw.getJSONArray("openings").length()) { index0 -> VastuPlanImportDataPlanOpeningsItem((raw.getJSONArray("openings").get(index0) as JSONObject)) }
    val entrance: VastuPlanImportDataPlanEntrance?
        get() = if (!raw.has("entrance") || raw.isNull("entrance")) null else VastuPlanImportDataPlanEntrance(raw.getJSONObject("entrance"))
    val importReview: VastuPlanImportDataPlanImportReview
        get() = VastuPlanImportDataPlanImportReview(raw.getJSONObject("importReview"))
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
}

data class VastuPlanImportDataNorth(val raw: JSONObject) {
    val bearingDeg: Double?
        get() = if (!raw.has("bearingDeg") || raw.isNull("bearingDeg")) null else raw.getDouble("bearingDeg")
    val confidence: Double
        get() = raw.getDouble("confidence")
    val source: String
        get() = raw.getString("source")
}

data class VastuPlanImportDataScale(val raw: JSONObject) {
    val metersPerUnit: Double?
        get() = if (!raw.has("metersPerUnit") || raw.isNull("metersPerUnit")) null else raw.getDouble("metersPerUnit")
    val source: String
        get() = raw.getString("source")
}

data class VastuPlanImportDataDimensionsItem(val raw: JSONObject) {
    val text: String
        get() = raw.getString("text")
    val start: List<Double>
        get() = List(raw.getJSONArray("start").length()) { index0 -> (raw.getJSONArray("start").get(index0) as Number).toDouble() }
    val end: List<Double>
        get() = List(raw.getJSONArray("end").length()) { index0 -> (raw.getJSONArray("end").get(index0) as Number).toDouble() }
    val confidence: Double
        get() = raw.getDouble("confidence")
}

data class VastuPlanImportDataNeedsReviewItem(val raw: JSONObject) {
    val field: String
        get() = raw.getString("field")
    val reason: String
        get() = raw.getString("reason")
}

data class VastuPlanImportDataSourceDocument(val raw: JSONObject) {
    val page: Int
        get() = raw.getInt("page")
    val pageCount: Int
        get() = raw.getInt("pageCount")
}

data class VastuPlanImportDataSource(val raw: JSONObject) {
    val method: String
        get() = raw.getString("method")
    val document: VastuPlanImportDataSourceDocument?
        get() = if (!raw.has("document") || raw.isNull("document")) null else VastuPlanImportDataSourceDocument(raw.getJSONObject("document"))
}

data class VastuPlanImportDataUsage(val raw: JSONObject) {
    val inputTokens: Long
        get() = raw.getLong("inputTokens")
    val outputTokens: Long
        get() = raw.getLong("outputTokens")
    val computeMicros: Long
        get() = raw.getLong("computeMicros")
    val cpuMicros: Long
        get() = raw.getLong("cpuMicros")
    val deliveryBytes: Long
        get() = raw.getLong("deliveryBytes")
}

data class VastuPlanImportDataPricing(val raw: JSONObject) {
    val currency: String
        get() = raw.getString("currency")
    val attributableCost: String
        get() = raw.getString("attributableCost")
    val modelCost: String
        get() = raw.getString("modelCost")
    val computeCost: String
        get() = raw.getString("computeCost")
    val markup: Int
        get() = raw.getInt("markup")
    val price: String
        get() = raw.getString("price")
    val unit: String
        get() = raw.getString("unit")
    val walletRounding: String
        get() = raw.getString("walletRounding")
    val computedPrice: String
        get() = raw.getString("computedPrice")
    val deliveryCost: String
        get() = raw.getString("deliveryCost")
}

data class VastuPlanImportDxfDataPlanPlot(val raw: JSONObject) {
    val width: Double
        get() = raw.getDouble("width")
    val length: Double
        get() = raw.getDouble("length")
    val polygon: List<List<Double>>?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val facing: String?
        get() = if (!raw.has("facing") || raw.isNull("facing")) null else raw.getString("facing")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
}

data class VastuPlanImportDxfDataPlanRoomsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val name: String
        get() = raw.getString("name")
    val polygon: List<List<Double>>
        get() = List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val x: Double?
        get() = if (!raw.has("x") || raw.isNull("x")) null else raw.getDouble("x")
    val y: Double?
        get() = if (!raw.has("y") || raw.isNull("y")) null else raw.getDouble("y")
    val w: Double?
        get() = if (!raw.has("w") || raw.isNull("w")) null else raw.getDouble("w")
    val h: Double?
        get() = if (!raw.has("h") || raw.isNull("h")) null else raw.getDouble("h")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
    val centre: List<Double>?
        get() = if (!raw.has("centre") || raw.isNull("centre")) null else List(raw.getJSONArray("centre").length()) { index0 -> (raw.getJSONArray("centre").get(index0) as Number).toDouble() }
    val holes: List<List<List<Double>>>?
        get() = if (!raw.has("holes") || raw.isNull("holes")) null else List(raw.getJSONArray("holes").length()) { index0 -> List((raw.getJSONArray("holes").get(index0) as org.json.JSONArray).length()) { index1 -> List(((raw.getJSONArray("holes").get(index0) as org.json.JSONArray).get(index1) as org.json.JSONArray).length()) { index2 -> (((raw.getJSONArray("holes").get(index0) as org.json.JSONArray).get(index1) as org.json.JSONArray).get(index2) as Number).toDouble() } } }
    val source: JSONObject?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getJSONObject("source")
    val labelEntityId: String?
        get() = if (!raw.has("labelEntityId") || raw.isNull("labelEntityId")) null else raw.getString("labelEntityId")
}

data class VastuPlanImportDxfDataPlanOpeningsDoorsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val type: String
        get() = raw.getString("type")
    val line: List<List<Double>>
        get() = List(raw.getJSONArray("line").length()) { index0 -> List((raw.getJSONArray("line").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("line").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val width: Double
        get() = raw.getDouble("width")
    val centre: List<Double>?
        get() = if (!raw.has("centre") || raw.isNull("centre")) null else List(raw.getJSONArray("centre").length()) { index0 -> (raw.getJSONArray("centre").get(index0) as Number).toDouble() }
    val source: JSONObject?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getJSONObject("source")
}

data class VastuPlanImportDxfDataPlanOpeningsWindowsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val type: String
        get() = raw.getString("type")
    val line: List<List<Double>>
        get() = List(raw.getJSONArray("line").length()) { index0 -> List((raw.getJSONArray("line").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("line").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val width: Double
        get() = raw.getDouble("width")
    val centre: List<Double>?
        get() = if (!raw.has("centre") || raw.isNull("centre")) null else List(raw.getJSONArray("centre").length()) { index0 -> (raw.getJSONArray("centre").get(index0) as Number).toDouble() }
    val source: JSONObject?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getJSONObject("source")
}

data class VastuPlanImportDxfDataPlanOpenings(val raw: JSONObject) {
    val doors: List<VastuPlanImportDxfDataPlanOpeningsDoorsItem>?
        get() = if (!raw.has("doors") || raw.isNull("doors")) null else List(raw.getJSONArray("doors").length()) { index0 -> VastuPlanImportDxfDataPlanOpeningsDoorsItem((raw.getJSONArray("doors").get(index0) as JSONObject)) }
    val windows: List<VastuPlanImportDxfDataPlanOpeningsWindowsItem>?
        get() = if (!raw.has("windows") || raw.isNull("windows")) null else List(raw.getJSONArray("windows").length()) { index0 -> VastuPlanImportDxfDataPlanOpeningsWindowsItem((raw.getJSONArray("windows").get(index0) as JSONObject)) }
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
}

data class VastuPlanImportDxfDataPlan(val raw: JSONObject) {
    val plot: VastuPlanImportDxfDataPlanPlot
        get() = VastuPlanImportDxfDataPlanPlot(raw.getJSONObject("plot"))
    val rooms: List<VastuPlanImportDxfDataPlanRoomsItem>
        get() = List(raw.getJSONArray("rooms").length()) { index0 -> VastuPlanImportDxfDataPlanRoomsItem((raw.getJSONArray("rooms").get(index0) as JSONObject)) }
    val openings: VastuPlanImportDxfDataPlanOpenings?
        get() = if (!raw.has("openings") || raw.isNull("openings")) null else VastuPlanImportDxfDataPlanOpenings(raw.getJSONObject("openings"))
    val trueNorthDeg: Double
        get() = raw.getDouble("trueNorthDeg")
    val orientationDeg: Double?
        get() = if (!raw.has("orientationDeg") || raw.isNull("orientationDeg")) null else raw.getDouble("orientationDeg")
    val units: String
        get() = raw.getString("units")
    val cadMetadata: JSONObject?
        get() = if (!raw.has("cadMetadata") || raw.isNull("cadMetadata")) null else raw.getJSONObject("cadMetadata")
    val canonicalUnits: String?
        get() = if (!raw.has("canonicalUnits") || raw.isNull("canonicalUnits")) null else raw.getString("canonicalUnits")
    val sourceInputUnits: String?
        get() = if (!raw.has("sourceInputUnits") || raw.isNull("sourceInputUnits")) null else raw.getString("sourceInputUnits")
}

data class VastuPlanImportDxfDataMappingReportItem(val raw: JSONObject) {
    val entityId: String
        get() = raw.getString("entityId")
    val handle: String?
        get() = if (!raw.has("handle") || raw.isNull("handle")) null else raw.getString("handle")
    val stepId: Int?
        get() = if (!raw.has("stepId") || raw.isNull("stepId")) null else raw.getInt("stepId")
    val entityType: String
        get() = raw.getString("entityType")
    val layer: String?
        get() = if (!raw.has("layer") || raw.isNull("layer")) null else raw.getString("layer")
    val role: String?
        get() = if (!raw.has("role") || raw.isNull("role")) null else raw.getString("role")
    val status: String
        get() = raw.getString("status")
    val planIds: List<String>?
        get() = if (!raw.has("planIds") || raw.isNull("planIds")) null else List(raw.getJSONArray("planIds").length()) { index0 -> (raw.getJSONArray("planIds").get(index0) as String) }
    val reason: String?
        get() = if (!raw.has("reason") || raw.isNull("reason")) null else raw.getString("reason")
    val parentId: String?
        get() = if (!raw.has("parentId") || raw.isNull("parentId")) null else raw.getString("parentId")
}

data class VastuPlanImportDxfDataReviewReasonsItem(val raw: JSONObject) {
    val id: String?
        get() = if (!raw.has("id") || raw.isNull("id")) null else raw.getString("id")
    val reason: String
        get() = raw.getString("reason")
}

data class VastuPlanImportIfcDataBuildingsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val name: String
        get() = raw.getString("name")
}

data class VastuPlanImportIfcDataStoreysItemPlanPlot(val raw: JSONObject) {
    val width: Double
        get() = raw.getDouble("width")
    val length: Double
        get() = raw.getDouble("length")
    val polygon: List<List<Double>>?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
}

data class VastuPlanImportIfcDataStoreysItemPlanRoomsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val name: String
        get() = raw.getString("name")
    val polygon: List<List<Double>>
        get() = List(raw.getJSONArray("polygon").length()) { index0 -> List((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("polygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val x: Double?
        get() = if (!raw.has("x") || raw.isNull("x")) null else raw.getDouble("x")
    val y: Double?
        get() = if (!raw.has("y") || raw.isNull("y")) null else raw.getDouble("y")
    val w: Double?
        get() = if (!raw.has("w") || raw.isNull("w")) null else raw.getDouble("w")
    val h: Double?
        get() = if (!raw.has("h") || raw.isNull("h")) null else raw.getDouble("h")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
    val centre: List<Double>?
        get() = if (!raw.has("centre") || raw.isNull("centre")) null else List(raw.getJSONArray("centre").length()) { index0 -> (raw.getJSONArray("centre").get(index0) as Number).toDouble() }
    val holes: List<List<List<Double>>>?
        get() = if (!raw.has("holes") || raw.isNull("holes")) null else List(raw.getJSONArray("holes").length()) { index0 -> List((raw.getJSONArray("holes").get(index0) as org.json.JSONArray).length()) { index1 -> List(((raw.getJSONArray("holes").get(index0) as org.json.JSONArray).get(index1) as org.json.JSONArray).length()) { index2 -> (((raw.getJSONArray("holes").get(index0) as org.json.JSONArray).get(index1) as org.json.JSONArray).get(index2) as Number).toDouble() } } }
    val source: JSONObject?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getJSONObject("source")
}

data class VastuPlanImportIfcDataStoreysItemPlanOpeningsDoorsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val type: String
        get() = raw.getString("type")
    val line: List<List<Double>>
        get() = List(raw.getJSONArray("line").length()) { index0 -> List((raw.getJSONArray("line").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("line").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val width: Double
        get() = raw.getDouble("width")
    val centre: List<Double>?
        get() = if (!raw.has("centre") || raw.isNull("centre")) null else List(raw.getJSONArray("centre").length()) { index0 -> (raw.getJSONArray("centre").get(index0) as Number).toDouble() }
    val source: JSONObject?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getJSONObject("source")
    val name: String?
        get() = if (!raw.has("name") || raw.isNull("name")) null else raw.getString("name")
    val openingHeight: Double?
        get() = if (!raw.has("openingHeight") || raw.isNull("openingHeight")) null else raw.getDouble("openingHeight")
}

data class VastuPlanImportIfcDataStoreysItemPlanOpeningsWindowsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val type: String
        get() = raw.getString("type")
    val line: List<List<Double>>
        get() = List(raw.getJSONArray("line").length()) { index0 -> List((raw.getJSONArray("line").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("line").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val width: Double
        get() = raw.getDouble("width")
    val centre: List<Double>?
        get() = if (!raw.has("centre") || raw.isNull("centre")) null else List(raw.getJSONArray("centre").length()) { index0 -> (raw.getJSONArray("centre").get(index0) as Number).toDouble() }
    val source: JSONObject?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getJSONObject("source")
    val name: String?
        get() = if (!raw.has("name") || raw.isNull("name")) null else raw.getString("name")
    val openingHeight: Double?
        get() = if (!raw.has("openingHeight") || raw.isNull("openingHeight")) null else raw.getDouble("openingHeight")
}

data class VastuPlanImportIfcDataStoreysItemPlanOpenings(val raw: JSONObject) {
    val doors: List<VastuPlanImportIfcDataStoreysItemPlanOpeningsDoorsItem>?
        get() = if (!raw.has("doors") || raw.isNull("doors")) null else List(raw.getJSONArray("doors").length()) { index0 -> VastuPlanImportIfcDataStoreysItemPlanOpeningsDoorsItem((raw.getJSONArray("doors").get(index0) as JSONObject)) }
    val windows: List<VastuPlanImportIfcDataStoreysItemPlanOpeningsWindowsItem>?
        get() = if (!raw.has("windows") || raw.isNull("windows")) null else List(raw.getJSONArray("windows").length()) { index0 -> VastuPlanImportIfcDataStoreysItemPlanOpeningsWindowsItem((raw.getJSONArray("windows").get(index0) as JSONObject)) }
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
}

data class VastuPlanImportIfcDataStoreysItemPlan(val raw: JSONObject) {
    val plot: VastuPlanImportIfcDataStoreysItemPlanPlot
        get() = VastuPlanImportIfcDataStoreysItemPlanPlot(raw.getJSONObject("plot"))
    val rooms: List<VastuPlanImportIfcDataStoreysItemPlanRoomsItem>
        get() = List(raw.getJSONArray("rooms").length()) { index0 -> VastuPlanImportIfcDataStoreysItemPlanRoomsItem((raw.getJSONArray("rooms").get(index0) as JSONObject)) }
    val openings: VastuPlanImportIfcDataStoreysItemPlanOpenings?
        get() = if (!raw.has("openings") || raw.isNull("openings")) null else VastuPlanImportIfcDataStoreysItemPlanOpenings(raw.getJSONObject("openings"))
    val trueNorthDeg: Double
        get() = raw.getDouble("trueNorthDeg")
    val orientationDeg: Double?
        get() = if (!raw.has("orientationDeg") || raw.isNull("orientationDeg")) null else raw.getDouble("orientationDeg")
    val units: String
        get() = raw.getString("units")
    val cadMetadata: JSONObject?
        get() = if (!raw.has("cadMetadata") || raw.isNull("cadMetadata")) null else raw.getJSONObject("cadMetadata")
    val canonicalUnits: String?
        get() = if (!raw.has("canonicalUnits") || raw.isNull("canonicalUnits")) null else raw.getString("canonicalUnits")
    val sourceInputUnits: String?
        get() = if (!raw.has("sourceInputUnits") || raw.isNull("sourceInputUnits")) null else raw.getString("sourceInputUnits")
}

data class VastuPlanImportIfcDataStoreysItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val name: String
        get() = raw.getString("name")
    val buildingId: String?
        get() = if (!raw.has("buildingId") || raw.isNull("buildingId")) null else raw.getString("buildingId")
    val elevationMetres: Double?
        get() = if (!raw.has("elevationMetres") || raw.isNull("elevationMetres")) null else raw.getDouble("elevationMetres")
    val plan: VastuPlanImportIfcDataStoreysItemPlan
        get() = VastuPlanImportIfcDataStoreysItemPlan(raw.getJSONObject("plan"))
}

data class VastuPlanImportIfcDataMappingReportItem(val raw: JSONObject) {
    val entityId: String
        get() = raw.getString("entityId")
    val handle: String?
        get() = if (!raw.has("handle") || raw.isNull("handle")) null else raw.getString("handle")
    val stepId: Int?
        get() = if (!raw.has("stepId") || raw.isNull("stepId")) null else raw.getInt("stepId")
    val entityType: String
        get() = raw.getString("entityType")
    val layer: String?
        get() = if (!raw.has("layer") || raw.isNull("layer")) null else raw.getString("layer")
    val role: String?
        get() = if (!raw.has("role") || raw.isNull("role")) null else raw.getString("role")
    val status: String
        get() = raw.getString("status")
    val planIds: List<String>?
        get() = if (!raw.has("planIds") || raw.isNull("planIds")) null else List(raw.getJSONArray("planIds").length()) { index0 -> (raw.getJSONArray("planIds").get(index0) as String) }
    val reason: String?
        get() = if (!raw.has("reason") || raw.isNull("reason")) null else raw.getString("reason")
    val storeyId: String?
        get() = if (!raw.has("storeyId") || raw.isNull("storeyId")) null else raw.getString("storeyId")
}

data class VastuPlanImportIfcDataReviewReasonsItem(val raw: JSONObject) {
    val id: String?
        get() = if (!raw.has("id") || raw.isNull("id")) null else raw.getString("id")
    val reason: String
        get() = raw.getString("reason")
}

data class VastuPlanOptimizeDataPlotRegionGeometry(val raw: JSONObject) {
    val polygon: Any?
        get() = if (!raw.has("polygon") || raw.isNull("polygon")) null else raw.get("polygon").takeUnless { it == JSONObject.NULL }
    val holes: List<Any?>?
        get() = if (!raw.has("holes") || raw.isNull("holes")) null else List(raw.getJSONArray("holes").length()) { index0 -> raw.getJSONArray("holes").get(index0).takeUnless { it == JSONObject.NULL } }
    val multipolygons: List<Any?>?
        get() = if (!raw.has("multipolygons") || raw.isNull("multipolygons")) null else List(raw.getJSONArray("multipolygons").length()) { index0 -> raw.getJSONArray("multipolygons").get(index0).takeUnless { it == JSONObject.NULL } }
}

data class VastuPlanOptimizeDataPlotRegionBrahmasthan(val raw: JSONObject) {
    val pole: List<Double>?
        get() = if (!raw.has("pole") || raw.isNull("pole")) null else List(raw.getJSONArray("pole").length()) { index0 -> (raw.getJSONArray("pole").get(index0) as Number).toDouble() }
    val poleInside: Boolean?
        get() = if (!raw.has("poleInside") || raw.isNull("poleInside")) null else raw.getBoolean("poleInside")
    val basis: String?
        get() = if (!raw.has("basis") || raw.isNull("basis")) null else raw.getString("basis")
    val netArea: Double?
        get() = if (!raw.has("netArea") || raw.isNull("netArea")) null else raw.getDouble("netArea")
}

data class VastuPlanOptimizeDataPlotRegionGridZonesItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
}

data class VastuPlanOptimizeDataPlotRegionSectorsItem(val raw: JSONObject) {
    val zone: String?
        get() = if (!raw.has("zone") || raw.isNull("zone")) null else raw.getString("zone")
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
}

data class VastuPlanOptimizeDataPlotRegion(val raw: JSONObject) {
    val geometry: VastuPlanOptimizeDataPlotRegionGeometry?
        get() = if (!raw.has("geometry") || raw.isNull("geometry")) null else VastuPlanOptimizeDataPlotRegionGeometry(raw.getJSONObject("geometry"))
    val area: Double?
        get() = if (!raw.has("area") || raw.isNull("area")) null else raw.getDouble("area")
    val centroid: List<Double>?
        get() = if (!raw.has("centroid") || raw.isNull("centroid")) null else List(raw.getJSONArray("centroid").length()) { index0 -> (raw.getJSONArray("centroid").get(index0) as Number).toDouble() }
    val centroidInside: Boolean?
        get() = if (!raw.has("centroidInside") || raw.isNull("centroidInside")) null else raw.getBoolean("centroidInside")
    val brahmasthan: VastuPlanOptimizeDataPlotRegionBrahmasthan?
        get() = if (!raw.has("brahmasthan") || raw.isNull("brahmasthan")) null else VastuPlanOptimizeDataPlotRegionBrahmasthan(raw.getJSONObject("brahmasthan"))
    val gridZones: List<VastuPlanOptimizeDataPlotRegionGridZonesItem>?
        get() = if (!raw.has("gridZones") || raw.isNull("gridZones")) null else List(raw.getJSONArray("gridZones").length()) { index0 -> VastuPlanOptimizeDataPlotRegionGridZonesItem((raw.getJSONArray("gridZones").get(index0) as JSONObject)) }
    val sectors: List<VastuPlanOptimizeDataPlotRegionSectorsItem>?
        get() = if (!raw.has("sectors") || raw.isNull("sectors")) null else List(raw.getJSONArray("sectors").length()) { index0 -> VastuPlanOptimizeDataPlotRegionSectorsItem((raw.getJSONArray("sectors").get(index0) as JSONObject)) }
}

data class VastuPlotFromSurveyDataPricing(val raw: JSONObject) {
    val attributableCostUsd: String
        get() = raw.getString("attributableCostUsd")
    val markupMultiplier: Int
        get() = raw.getInt("markupMultiplier")
    val computedPriceUsd: String
        get() = raw.getString("computedPriceUsd")
    val settledChargeUsd: String
        get() = raw.getString("settledChargeUsd")
    val settlement: String
        get() = raw.getString("settlement")
}

data class VastuPortfolioAnalyticsDataAccountCounts(val raw: JSONObject) {
    val propertiesCreated: Int
        get() = raw.getInt("propertiesCreated")
    val propertiesAssessed: Int
        get() = raw.getInt("propertiesAssessed")
    val reportsDelivered: Int
        get() = raw.getInt("reportsDelivered")
    val returningProperties: Int
        get() = raw.getInt("returningProperties")
}

data class VastuPortfolioAnalyticsDataAccountDailyItemCounts(val raw: JSONObject) {
    val propertiesCreated: Int
        get() = raw.getInt("propertiesCreated")
    val propertiesAssessed: Int
        get() = raw.getInt("propertiesAssessed")
    val reportsDelivered: Int
        get() = raw.getInt("reportsDelivered")
    val returningProperties: Int
        get() = raw.getInt("returningProperties")
}

data class VastuPortfolioAnalyticsDataAccountDailyItem(val raw: JSONObject) {
    val date: String
        get() = raw.getString("date")
    val counts: VastuPortfolioAnalyticsDataAccountDailyItemCounts
        get() = VastuPortfolioAnalyticsDataAccountDailyItemCounts(raw.getJSONObject("counts"))
}

data class VastuPortfolioAnalyticsDataAccount(val raw: JSONObject) {
    val counts: VastuPortfolioAnalyticsDataAccountCounts
        get() = VastuPortfolioAnalyticsDataAccountCounts(raw.getJSONObject("counts"))
    val daily: List<VastuPortfolioAnalyticsDataAccountDailyItem>
        get() = List(raw.getJSONArray("daily").length()) { index0 -> VastuPortfolioAnalyticsDataAccountDailyItem((raw.getJSONArray("daily").get(index0) as JSONObject)) }
    val returningDefinition: String
        get() = raw.getString("returningDefinition")
    val deliveryDefinition: String
        get() = raw.getString("deliveryDefinition")
    val coverage: String
        get() = raw.getString("coverage")
}

data class VastuPortfolioBudgetsSetDataScope(val raw: JSONObject) {
    val propertyId: String?
        get() = if (!raw.has("propertyId") || raw.isNull("propertyId")) null else raw.getString("propertyId")
    val tenantRef: String?
        get() = if (!raw.has("tenantRef") || raw.isNull("tenantRef")) null else raw.getString("tenantRef")
}

data class VastuPortfolioCompareDataPropertiesItemAssessment(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val inputSource: String?
        get() = if (!raw.has("inputSource") || raw.isNull("inputSource")) null else raw.getString("inputSource")
    val ruleset: String?
        get() = if (!raw.has("ruleset") || raw.isNull("ruleset")) null else raw.getString("ruleset")
    val zoneDefects: List<String>?
        get() = if (!raw.has("zoneDefects") || raw.isNull("zoneDefects")) null else List(raw.getJSONArray("zoneDefects").length()) { index0 -> (raw.getJSONArray("zoneDefects").get(index0) as String) }
    val kind: String?
        get() = if (!raw.has("kind") || raw.isNull("kind")) null else raw.getString("kind")
}

data class VastuPortfolioCompareDataPropertiesItem(val raw: JSONObject) {
    val propertyId: String
        get() = raw.getString("propertyId")
    val title: String
        get() = raw.getString("title")
    val city: String
        get() = raw.getString("city")
    val tags: List<String>
        get() = List(raw.getJSONArray("tags").length()) { index0 -> (raw.getJSONArray("tags").get(index0) as String) }
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val assessment: VastuPortfolioCompareDataPropertiesItemAssessment?
        get() = if (!raw.has("assessment") || raw.isNull("assessment")) null else VastuPortfolioCompareDataPropertiesItemAssessment(raw.getJSONObject("assessment"))
    val assessedAtEpoch: Long?
        get() = if (!raw.has("assessedAtEpoch") || raw.isNull("assessedAtEpoch")) null else raw.getLong("assessedAtEpoch")
}

data class VastuPortfolioSearchDataPropertiesItemAssessment(val raw: JSONObject) {
    val score: Int?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getInt("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val inputSource: String?
        get() = if (!raw.has("inputSource") || raw.isNull("inputSource")) null else raw.getString("inputSource")
    val ruleset: String?
        get() = if (!raw.has("ruleset") || raw.isNull("ruleset")) null else raw.getString("ruleset")
    val zoneDefects: List<String>?
        get() = if (!raw.has("zoneDefects") || raw.isNull("zoneDefects")) null else List(raw.getJSONArray("zoneDefects").length()) { index0 -> (raw.getJSONArray("zoneDefects").get(index0) as String) }
    val kind: String?
        get() = if (!raw.has("kind") || raw.isNull("kind")) null else raw.getString("kind")
}

data class VastuPortfolioSearchDataPropertiesItem(val raw: JSONObject) {
    val propertyId: String
        get() = raw.getString("propertyId")
    val title: String
        get() = raw.getString("title")
    val city: String
        get() = raw.getString("city")
    val tags: List<String>
        get() = List(raw.getJSONArray("tags").length()) { index0 -> (raw.getJSONArray("tags").get(index0) as String) }
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val assessment: VastuPortfolioSearchDataPropertiesItemAssessment?
        get() = if (!raw.has("assessment") || raw.isNull("assessment")) null else VastuPortfolioSearchDataPropertiesItemAssessment(raw.getJSONObject("assessment"))
    val assessedAtEpoch: Long?
        get() = if (!raw.has("assessedAtEpoch") || raw.isNull("assessedAtEpoch")) null else raw.getLong("assessedAtEpoch")
}

data class VastuPortfolioUsageDataGroupsItem(val raw: JSONObject) {
    val propertyId: String?
        get() = if (!raw.has("propertyId") || raw.isNull("propertyId")) null else raw.getString("propertyId")
    val tenantRef: String?
        get() = if (!raw.has("tenantRef") || raw.isNull("tenantRef")) null else raw.getString("tenantRef")
    val calls: Int
        get() = raw.getInt("calls")
    val chargedUsd: String
        get() = raw.getString("chargedUsd")
    val pending: Int
        get() = raw.getInt("pending")
}

data class VastuPropertiesActivityExportDataEventsItem(val raw: JSONObject) {
    val sequence: Long
        get() = raw.getLong("sequence")
    val actorId: String
        get() = raw.getString("actorId")
    val propertyId: String
        get() = raw.getString("propertyId")
    val revision: String
        get() = raw.getString("revision")
    val contentHash: String
        get() = raw.getString("contentHash")
    val action: String
        get() = raw.getString("action")
    val at: Long
        get() = raw.getLong("at")
    val previousHash: String
        get() = raw.getString("previousHash")
    val hash: String
        get() = raw.getString("hash")
    val details: JSONObject
        get() = raw.getJSONObject("details")
}

data class VastuPropertiesActivityListDataEventsItem(val raw: JSONObject) {
    val sequence: Long
        get() = raw.getLong("sequence")
    val actorId: String
        get() = raw.getString("actorId")
    val propertyId: String
        get() = raw.getString("propertyId")
    val revision: String
        get() = raw.getString("revision")
    val contentHash: String
        get() = raw.getString("contentHash")
    val action: String
        get() = raw.getString("action")
    val at: Long
        get() = raw.getLong("at")
    val previousHash: String
        get() = raw.getString("previousHash")
    val hash: String
        get() = raw.getString("hash")
    val details: JSONObject
        get() = raw.getJSONObject("details")
}

data class VastuPropertiesCollaborationCommentDataComment(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val actorId: String
        get() = raw.getString("actorId")
    val assessmentId: String
        get() = raw.getString("assessmentId")
    val revision: String
        get() = raw.getString("revision")
    val contentHash: String
        get() = raw.getString("contentHash")
    val at: Long
        get() = raw.getLong("at")
    val text: String
        get() = raw.getString("text")
}

data class VastuPropertiesCollaborationGetDataPropertyIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuPropertiesCollaborationGetDataPropertyArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuPropertiesCollaborationGetDataProperty(val raw: JSONObject) {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String
        get() = raw.getString("ownerId")
    val ids: VastuPropertiesCollaborationGetDataPropertyIds
        get() = VastuPropertiesCollaborationGetDataPropertyIds(raw.getJSONObject("ids"))
    val title: String
        get() = raw.getString("title")
    val data: JSONObject
        get() = raw.getJSONObject("data")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuPropertiesCollaborationGetDataPropertyArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuPropertiesCollaborationGetDataPropertyArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long
        get() = raw.getLong("updatedAtEpoch")
    val contentHash: String
        get() = raw.getString("contentHash")
}

data class VastuPropertiesCollaborationGetDataCommentsItem(val raw: JSONObject) {
    val id: String
        get() = raw.getString("id")
    val actorId: String
        get() = raw.getString("actorId")
    val assessmentId: String
        get() = raw.getString("assessmentId")
    val revision: String
        get() = raw.getString("revision")
    val contentHash: String
        get() = raw.getString("contentHash")
    val at: Long
        get() = raw.getLong("at")
    val text: String
        get() = raw.getString("text")
}

data class VastuPropertiesCollaborationGetDataReviewsItem(val raw: JSONObject) {
    val actorId: String
        get() = raw.getString("actorId")
    val assessmentId: String
        get() = raw.getString("assessmentId")
    val revision: String
        get() = raw.getString("revision")
    val contentHash: String
        get() = raw.getString("contentHash")
    val at: Long
        get() = raw.getLong("at")
    val decision: String
        get() = raw.getString("decision")
}

data class VastuPropertiesCollaborationReviewDataReview(val raw: JSONObject) {
    val actorId: String
        get() = raw.getString("actorId")
    val assessmentId: String
        get() = raw.getString("assessmentId")
    val revision: String
        get() = raw.getString("revision")
    val contentHash: String
        get() = raw.getString("contentHash")
    val at: Long
        get() = raw.getLong("at")
    val decision: String
        get() = raw.getString("decision")
}

data class VastuPropertiesCollaborationUpdateDataPropertyIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuPropertiesCollaborationUpdateDataPropertyArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuPropertiesCollaborationUpdateDataProperty(val raw: JSONObject) {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String
        get() = raw.getString("ownerId")
    val ids: VastuPropertiesCollaborationUpdateDataPropertyIds
        get() = VastuPropertiesCollaborationUpdateDataPropertyIds(raw.getJSONObject("ids"))
    val title: String
        get() = raw.getString("title")
    val data: JSONObject
        get() = raw.getJSONObject("data")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuPropertiesCollaborationUpdateDataPropertyArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuPropertiesCollaborationUpdateDataPropertyArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long
        get() = raw.getLong("updatedAtEpoch")
    val contentHash: String
        get() = raw.getString("contentHash")
}

data class VastuPropertiesCreateDataIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuPropertiesCreateDataArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuPropertiesDeleteDataErasureReceiptRemovedItem(val raw: JSONObject) {
    val kind: String
        get() = raw.getString("kind")
    val recordId: String?
        get() = if (!raw.has("recordId") || raw.isNull("recordId")) null else raw.getString("recordId")
    val artifactId: String?
        get() = if (!raw.has("artifactId") || raw.isNull("artifactId")) null else raw.getString("artifactId")
    val artifactHash: String?
        get() = if (!raw.has("artifactHash") || raw.isNull("artifactHash")) null else raw.getString("artifactHash")
    val versionHash: String?
        get() = if (!raw.has("versionHash") || raw.isNull("versionHash")) null else raw.getString("versionHash")
    val deleteMarker: Boolean?
        get() = if (!raw.has("deleteMarker") || raw.isNull("deleteMarker")) null else raw.getBoolean("deleteMarker")
    val versionCount: Int?
        get() = if (!raw.has("versionCount") || raw.isNull("versionCount")) null else raw.getInt("versionCount")
    val deleteMarkerCount: Int?
        get() = if (!raw.has("deleteMarkerCount") || raw.isNull("deleteMarkerCount")) null else raw.getInt("deleteMarkerCount")
    val versionsSha256: String?
        get() = if (!raw.has("versionsSha256") || raw.isNull("versionsSha256")) null else raw.getString("versionsSha256")
}

data class VastuPropertiesDeleteDataErasureReceiptRetainedItem(val raw: JSONObject) {
    val kind: String
        get() = raw.getString("kind")
    val purpose: String
        get() = raw.getString("purpose")
}

data class VastuPropertiesDeleteDataErasureReceipt(val raw: JSONObject) {
    val schemaVersion: Int
        get() = raw.getInt("schemaVersion")
    val propertyId: String
        get() = raw.getString("propertyId")
    val status: String
        get() = raw.getString("status")
    val scope: String
        get() = raw.getString("scope")
    val completedAt: String
        get() = raw.getString("completedAt")
    val removed: List<VastuPropertiesDeleteDataErasureReceiptRemovedItem>
        get() = List(raw.getJSONArray("removed").length()) { index0 -> VastuPropertiesDeleteDataErasureReceiptRemovedItem((raw.getJSONArray("removed").get(index0) as JSONObject)) }
    val retained: List<VastuPropertiesDeleteDataErasureReceiptRetainedItem>
        get() = List(raw.getJSONArray("retained").length()) { index0 -> VastuPropertiesDeleteDataErasureReceiptRetainedItem((raw.getJSONArray("retained").get(index0) as JSONObject)) }
    val backupRetentionDays: Int
        get() = raw.getInt("backupRetentionDays")
    val backupPolicy: String
        get() = raw.getString("backupPolicy")
    val hashAlgorithm: String
        get() = raw.getString("hashAlgorithm")
    val receiptHash: String
        get() = raw.getString("receiptHash")
    val linkedScanIds: List<String>?
        get() = if (!raw.has("linkedScanIds") || raw.isNull("linkedScanIds")) null else List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>?
        get() = if (!raw.has("linkedAssessmentIds") || raw.isNull("linkedAssessmentIds")) null else List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val deletedByAccountHash: String
        get() = raw.getString("deletedByAccountHash")
    val deletedAtEpoch: Long
        get() = raw.getLong("deletedAtEpoch")
    val revisionId: String?
        get() = if (!raw.has("revisionId") || raw.isNull("revisionId")) null else raw.getString("revisionId")
    val receiptId: String?
        get() = if (!raw.has("receiptId") || raw.isNull("receiptId")) null else raw.getString("receiptId")
}

data class VastuPropertiesGetDataIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuPropertiesGetDataArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuPropertiesLinkScanDataIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuPropertiesLinkScanDataArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuPropertiesListDataPropertiesItemIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuPropertiesListDataPropertiesItemArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuPropertiesListDataPropertiesItem(val raw: JSONObject) {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String
        get() = raw.getString("ownerId")
    val ids: VastuPropertiesListDataPropertiesItemIds
        get() = VastuPropertiesListDataPropertiesItemIds(raw.getJSONObject("ids"))
    val title: String
        get() = raw.getString("title")
    val data: JSONObject
        get() = raw.getJSONObject("data")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuPropertiesListDataPropertiesItemArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuPropertiesListDataPropertiesItemArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long
        get() = raw.getLong("updatedAtEpoch")
    val contentHash: String
        get() = raw.getString("contentHash")
}

data class VastuPropertiesUpdateDataIds(val raw: JSONObject) {
    val project: String
        get() = raw.getString("project")
    val building: String
        get() = raw.getString("building")
    val unit: String
        get() = raw.getString("unit")
    val floor: String
        get() = raw.getString("floor")
    val revision: String
        get() = raw.getString("revision")
}

data class VastuPropertiesUpdateDataArchiveTier(val raw: JSONObject) {
    val months: Int
        get() = raw.getInt("months")
    val storedBytes: Long
        get() = raw.getLong("storedBytes")
    val priceCents: Int
        get() = raw.getInt("priceCents")
    val setAtEpoch: Long
        get() = raw.getLong("setAtEpoch")
    val retainUntilEpoch: Long
        get() = raw.getLong("retainUntilEpoch")
}

data class VastuQuoteCalculateDataLineItemsItem(val raw: JSONObject) {
    val slug: String?
        get() = if (!raw.has("slug") || raw.isNull("slug")) null else raw.getString("slug")
    val label: String?
        get() = if (!raw.has("label") || raw.isNull("label")) null else raw.getString("label")
    val quantity: Int?
        get() = if (!raw.has("quantity") || raw.isNull("quantity")) null else raw.getInt("quantity")
    val baseCostUsd: String?
        get() = if (!raw.has("baseCostUsd") || raw.isNull("baseCostUsd")) null else raw.getString("baseCostUsd")
    val totalPriceUsd: String?
        get() = if (!raw.has("totalPriceUsd") || raw.isNull("totalPriceUsd")) null else raw.getString("totalPriceUsd")
    val category: String?
        get() = if (!raw.has("category") || raw.isNull("category")) null else raw.getString("category")
}

data class VastuReceiptVerifyDataReceipt(val raw: JSONObject) {
    val assessmentId: String
        get() = raw.getString("assessmentId")
    val inputHash: String
        get() = raw.getString("inputHash")
    val resultHash: String
        get() = raw.getString("resultHash")
    val rulesVersion: String
        get() = raw.getString("rulesVersion")
    val score: Int
        get() = raw.getInt("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
    val sourceLabels: List<String>
        get() = List(raw.getJSONArray("sourceLabels").length()) { index0 -> (raw.getJSONArray("sourceLabels").get(index0) as String) }
    val timestamp: Long
        get() = raw.getLong("timestamp")
}

data class VastuRemediationTaskDataEvidenceItem(val raw: JSONObject) {
    val reference: String
        get() = raw.getString("reference")
    val photoRef: String?
        get() = if (!raw.has("photoRef") || raw.isNull("photoRef")) null else raw.getString("photoRef")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
}

data class VastuRemediationTaskDataReassessmentLinkRequest(val raw: JSONObject) {
    val propertyId: String
        get() = raw.getString("propertyId")
}

data class VastuRemediationTaskDataReassessmentLink(val raw: JSONObject) {
    val operation: String
        get() = raw.getString("operation")
    val method: String
        get() = raw.getString("method")
    val request: VastuRemediationTaskDataReassessmentLinkRequest
        get() = VastuRemediationTaskDataReassessmentLinkRequest(raw.getJSONObject("request"))
    val resultPointer: String
        get() = raw.getString("resultPointer")
    val propertyId: String
        get() = raw.getString("propertyId")
    val taskId: String
        get() = raw.getString("taskId")
    val assessmentId: String
        get() = raw.getString("assessmentId")
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

data class VastuRemedyComparisonDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuScanStoredDataAuditStatus(val raw: JSONObject) {
    val status: String
        get() = raw.getString("status")
    val reasons: List<String>
        get() = List(raw.getJSONArray("reasons").length()) { index0 -> (raw.getJSONArray("reasons").get(index0) as String) }
}

data class VastuScansDeleteDataDeletionReceiptRemovedItem(val raw: JSONObject) {
    val kind: String
        get() = raw.getString("kind")
    val artifactId: String
        get() = raw.getString("artifactId")
}

data class VastuScansDeleteDataDeletionReceipt(val raw: JSONObject) {
    val schemaVersion: Int
        get() = raw.getInt("schemaVersion")
    val kind: String
        get() = raw.getString("kind")
    val scanId: String
        get() = raw.getString("scanId")
    val scope: String
        get() = raw.getString("scope")
    val removed: List<VastuScansDeleteDataDeletionReceiptRemovedItem>
        get() = List(raw.getJSONArray("removed").length()) { index0 -> VastuScansDeleteDataDeletionReceiptRemovedItem((raw.getJSONArray("removed").get(index0) as JSONObject)) }
    val scopeNote: String?
        get() = if (!raw.has("scopeNote") || raw.isNull("scopeNote")) null else raw.getString("scopeNote")
    val deletedByAccountHash: String
        get() = raw.getString("deletedByAccountHash")
    val deletedAtEpoch: Int
        get() = raw.getInt("deletedAtEpoch")
    val deletedAt: String
        get() = raw.getString("deletedAt")
    val hashAlgorithm: String
        get() = raw.getString("hashAlgorithm")
    val receiptId: String
        get() = raw.getString("receiptId")
    val receiptHash: String
        get() = raw.getString("receiptHash")
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

data class VastuSpecializedAuditDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuSpecializedAuditDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
}

data class VastuSunPathDataInput(val raw: JSONObject) {
    val lat: Double
        get() = raw.getDouble("lat")
    val lon: Double
        get() = raw.getDouble("lon")
    val date: String
        get() = raw.getString("date")
}

data class VastuWorkflowDataDataItemsItem(val raw: JSONObject) {
    val remedyKey: String
        get() = raw.getString("remedyKey")
    val itemId: String
        get() = raw.getString("itemId")
    val kind: String
        get() = raw.getString("kind")
    val label: String
        get() = raw.getString("label")
    val availability: String
        get() = raw.getString("availability")
    val link: String?
        get() = if (!raw.has("link") || raw.isNull("link")) null else raw.getString("link")
    val referralRef: String?
        get() = if (!raw.has("referralRef") || raw.isNull("referralRef")) null else raw.getString("referralRef")
}

data class VastuWorkflowDataData(val raw: JSONObject) {
    val propertyId: String?
        get() = if (!raw.has("propertyId") || raw.isNull("propertyId")) null else raw.getString("propertyId")
    val catalogId: String?
        get() = if (!raw.has("catalogId") || raw.isNull("catalogId")) null else raw.getString("catalogId")
    val tasks: Map<String, VastuRemediationTaskData>?
        get() = if (!raw.has("tasks") || raw.isNull("tasks")) null else raw.getJSONObject("tasks").keys().asSequence().associateWith { key -> VastuRemediationTaskData(raw.getJSONObject("tasks").getJSONObject(key)) }
    val items: List<VastuWorkflowDataDataItemsItem>?
        get() = if (!raw.has("items") || raw.isNull("items")) null else List(raw.getJSONArray("items").length()) { index0 -> VastuWorkflowDataDataItemsItem((raw.getJSONArray("items").get(index0) as JSONObject)) }
}

data class VastuWorkflowDataRemediesItemMappedItemsItem(val raw: JSONObject) {
    val remedyKey: String
        get() = raw.getString("remedyKey")
    val itemId: String
        get() = raw.getString("itemId")
    val kind: String
        get() = raw.getString("kind")
    val label: String
        get() = raw.getString("label")
    val availability: String
        get() = raw.getString("availability")
    val link: String?
        get() = if (!raw.has("link") || raw.isNull("link")) null else raw.getString("link")
    val referralRef: String?
        get() = if (!raw.has("referralRef") || raw.isNull("referralRef")) null else raw.getString("referralRef")
}

data class VastuWorkflowDataRemediesItem(val raw: JSONObject) {
    val remedyKey: String?
        get() = if (!raw.has("remedyKey") || raw.isNull("remedyKey")) null else raw.getString("remedyKey")
    val remedy: String?
        get() = if (!raw.has("remedy") || raw.isNull("remedy")) null else raw.getString("remedy")
    val classification: String?
        get() = if (!raw.has("classification") || raw.isNull("classification")) null else raw.getString("classification")
    val source: String?
        get() = if (!raw.has("source") || raw.isNull("source")) null else raw.getString("source")
    val mappedItems: List<VastuWorkflowDataRemediesItemMappedItemsItem>?
        get() = if (!raw.has("mappedItems") || raw.isNull("mappedItems")) null else List(raw.getJSONArray("mappedItems").length()) { index0 -> VastuWorkflowDataRemediesItemMappedItemsItem((raw.getJSONArray("mappedItems").get(index0) as JSONObject)) }
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

data class VastuZoneWiseScoreDataNotAssessedItem(val raw: JSONObject) {
    val room: String
        get() = raw.getString("room")
    val zone: String
        get() = raw.getString("zone")
    val reason: String
        get() = raw.getString("reason")
    val graded: Boolean
        get() = raw.getBoolean("graded")
}

data class VastuZoneWiseScoreDataReceipt(val raw: JSONObject) {
    val format: String
        get() = raw.getString("format")
    val token: String
        get() = raw.getString("token")
    val verifyUrl: String
        get() = raw.getString("verifyUrl")
    val keyUrl: String
        get() = raw.getString("keyUrl")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuArAttestationChallengeData(override val raw: JSONObject) : VastuData {
    val challenge: String?
        get() = if (!raw.has("challenge") || raw.isNull("challenge")) null else raw.getString("challenge")
    val expiresAtEpoch: Int?
        get() = if (!raw.has("expiresAtEpoch") || raw.isNull("expiresAtEpoch")) null else raw.getInt("expiresAtEpoch")
    val ttlSeconds: Int?
        get() = if (!raw.has("ttlSeconds") || raw.isNull("ttlSeconds")) null else raw.getInt("ttlSeconds")
    val singleUse: Boolean
        get() = raw.getBoolean("singleUse")
    val deviceAttestation: VastuArAttestationChallengeDataDeviceAttestation
        get() = VastuArAttestationChallengeDataDeviceAttestation(raw.getJSONObject("deviceAttestation"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuArCaptureMergeData(override val raw: JSONObject) : VastuData {
    val method: String
        get() = raw.getString("method")
    val frame: JSONObject
        get() = raw.getJSONObject("frame")
    val rooms: List<JSONObject>
        get() = List(raw.getJSONArray("rooms").length()) { index0 -> (raw.getJSONArray("rooms").get(index0) as JSONObject) }
    val outlines: List<JSONObject>
        get() = List(raw.getJSONArray("outlines").length()) { index0 -> (raw.getJSONArray("outlines").get(index0) as JSONObject) }
    val registrations: List<JSONObject>
        get() = List(raw.getJSONArray("registrations").length()) { index0 -> (raw.getJSONArray("registrations").get(index0) as JSONObject) }
    val pairResiduals: List<VastuArCaptureMergeDataPairResidualsItem>
        get() = List(raw.getJSONArray("pairResiduals").length()) { index0 -> VastuArCaptureMergeDataPairResidualsItem((raw.getJSONArray("pairResiduals").get(index0) as JSONObject)) }
    val unresolvedAlignmentErrors: List<String>
        get() = List(raw.getJSONArray("unresolvedAlignmentErrors").length()) { index0 -> (raw.getJSONArray("unresolvedAlignmentErrors").get(index0) as String) }
    val floorStack: List<JSONObject>
        get() = List(raw.getJSONArray("floorStack").length()) { index0 -> (raw.getJSONArray("floorStack").get(index0) as JSONObject) }
    val toleranceM: Double
        get() = raw.getDouble("toleranceM")
    val alignmentMethod: String
        get() = raw.getString("alignmentMethod")
    val pricing: VastuArCaptureMergeDataPricing?
        get() = if (!raw.has("pricing") || raw.isNull("pricing")) null else VastuArCaptureMergeDataPricing(raw.getJSONObject("pricing"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuArDeityIconsData(override val raw: JSONObject) : VastuData {
    val icons: List<VastuArDeityIconsDataIconsItem>
        get() = List(raw.getJSONArray("icons").length()) { index0 -> VastuArDeityIconsDataIconsItem((raw.getJSONArray("icons").get(index0) as JSONObject)) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val provenance: Any?
        get() = raw.get("provenance").takeUnless { it == JSONObject.NULL }
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val deviceAttestation: Any?
        get() = raw.get("deviceAttestation").takeUnless { it == JSONObject.NULL }
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val auditStatus: VastuArRoomCaptureDataAuditStatus?
        get() = if (!raw.has("auditStatus") || raw.isNull("auditStatus")) null else VastuArRoomCaptureDataAuditStatus(raw.getJSONObject("auditStatus"))
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
    val deviceAttestation: Any?
        get() = if (!raw.has("deviceAttestation") || raw.isNull("deviceAttestation")) null else raw.get("deviceAttestation").takeUnless { it == JSONObject.NULL }
    val deviceAttested: Boolean?
        get() = if (!raw.has("deviceAttested") || raw.isNull("deviceAttested")) null else raw.getBoolean("deviceAttested")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuArScanQualityDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuArScanQualityDataReceipt(raw.getJSONObject("receipt"))
    val gradeStatus: String?
        get() = if (!raw.has("gradeStatus") || raw.isNull("gradeStatus")) null else raw.getString("gradeStatus")
    val gradeReasons: List<String>?
        get() = if (!raw.has("gradeReasons") || raw.isNull("gradeReasons")) null else List(raw.getJSONArray("gradeReasons").length()) { index0 -> (raw.getJSONArray("gradeReasons").get(index0) as String) }
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuArchiveDeleteData(override val raw: JSONObject) : VastuData {
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val propertyId: String
        get() = raw.getString("propertyId")
    val deleted: Boolean
        get() = raw.getBoolean("deleted")
    val exportDeleted: Boolean
        get() = raw.getBoolean("exportDeleted")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val erasureStatus: String?
        get() = if (!raw.has("erasureStatus") || raw.isNull("erasureStatus")) null else raw.getString("erasureStatus")
    val erasureReceipt: VastuArchiveDeleteDataErasureReceipt?
        get() = if (!raw.has("erasureReceipt") || raw.isNull("erasureReceipt")) null else VastuArchiveDeleteDataErasureReceipt(raw.getJSONObject("erasureReceipt"))
    val retryAfterEpoch: Long?
        get() = if (!raw.has("retryAfterEpoch") || raw.isNull("retryAfterEpoch")) null else raw.getLong("retryAfterEpoch")
    val revisionId: String?
        get() = if (!raw.has("revisionId") || raw.isNull("revisionId")) null else raw.getString("revisionId")
    val replayed: Boolean?
        get() = if (!raw.has("replayed") || raw.isNull("replayed")) null else raw.getBoolean("replayed")
    val receiptId: String?
        get() = if (!raw.has("receiptId") || raw.isNull("receiptId")) null else raw.getString("receiptId")
    val receiptStatus: String?
        get() = if (!raw.has("receiptStatus") || raw.isNull("receiptStatus")) null else raw.getString("receiptStatus")
    val readReceipt: String?
        get() = if (!raw.has("readReceipt") || raw.isNull("readReceipt")) null else raw.getString("readReceipt")
}

data class VastuArchiveExportData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val downloadUrl: String
        get() = raw.getString("downloadUrl")
    val expiresInSeconds: Int
        get() = raw.getInt("expiresInSeconds")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val sizeBytes: Long
        get() = raw.getLong("sizeBytes")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuArchiveSummaryData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val archiveTier: VastuArchiveSummaryDataArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuArchiveSummaryDataArchiveTier(raw.getJSONObject("archiveTier"))
    val linkedScanCount: Int
        get() = raw.getInt("linkedScanCount")
    val linkedAssessmentCount: Int
        get() = raw.getInt("linkedAssessmentCount")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuArchiveTierData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String?
        get() = if (!raw.has("ownerId") || raw.isNull("ownerId")) null else raw.getString("ownerId")
    val ids: VastuArchiveTierDataIds?
        get() = if (!raw.has("ids") || raw.isNull("ids")) null else VastuArchiveTierDataIds(raw.getJSONObject("ids"))
    val title: String?
        get() = if (!raw.has("title") || raw.isNull("title")) null else raw.getString("title")
    val data: JSONObject?
        get() = if (!raw.has("data") || raw.isNull("data")) null else raw.getJSONObject("data")
    val retentionDays: Int?
        get() = if (!raw.has("retentionDays") || raw.isNull("retentionDays")) null else raw.getInt("retentionDays")
    val expiresAtEpoch: Long?
        get() = if (!raw.has("expiresAtEpoch") || raw.isNull("expiresAtEpoch")) null else raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>?
        get() = if (!raw.has("linkedScanIds") || raw.isNull("linkedScanIds")) null else List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>?
        get() = if (!raw.has("linkedAssessmentIds") || raw.isNull("linkedAssessmentIds")) null else List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuArchiveTierDataArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuArchiveTierDataArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long?
        get() = if (!raw.has("createdAtEpoch") || raw.isNull("createdAtEpoch")) null else raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long?
        get() = if (!raw.has("updatedAtEpoch") || raw.isNull("updatedAtEpoch")) null else raw.getLong("updatedAtEpoch")
    val contentHash: String?
        get() = if (!raw.has("contentHash") || raw.isNull("contentHash")) null else raw.getString("contentHash")
    val months: Int?
        get() = if (!raw.has("months") || raw.isNull("months")) null else raw.getInt("months")
    val storedBytes: Long?
        get() = if (!raw.has("storedBytes") || raw.isNull("storedBytes")) null else raw.getLong("storedBytes")
    val meterCents: Int?
        get() = if (!raw.has("meterCents") || raw.isNull("meterCents")) null else raw.getInt("meterCents")
    val actionCents: Int?
        get() = if (!raw.has("actionCents") || raw.isNull("actionCents")) null else raw.getInt("actionCents")
    val totalCents: Int?
        get() = if (!raw.has("totalCents") || raw.isNull("totalCents")) null else raw.getInt("totalCents")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val notAssessed: List<VastuAssessmentDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuAssessmentDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val warnings: List<JSONObject>?
        get() = if (!raw.has("warnings") || raw.isNull("warnings")) null else List(raw.getJSONArray("warnings").length()) { index0 -> (raw.getJSONArray("warnings").get(index0) as JSONObject) }
    val zoneCheck: JSONObject?
        get() = if (!raw.has("zoneCheck") || raw.isNull("zoneCheck")) null else raw.getJSONObject("zoneCheck")
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuAssessmentDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuAssessmentDataReceipt(raw.getJSONObject("receipt"))
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val centroidBasis: String?
        get() = if (!raw.has("centroidBasis") || raw.isNull("centroidBasis")) null else raw.getString("centroidBasis")
    val centerPolygonCentre: List<Double>?
        get() = if (!raw.has("centerPolygonCentre") || raw.isNull("centerPolygonCentre")) null else List(raw.getJSONArray("centerPolygonCentre").length()) { index0 -> (raw.getJSONArray("centerPolygonCentre").get(index0) as Number).toDouble() }
    val centreBasis: String?
        get() = if (!raw.has("centreBasis") || raw.isNull("centreBasis")) null else raw.getString("centreBasis")
    val centreOffset: Double?
        get() = if (!raw.has("centreOffset") || raw.isNull("centreOffset")) null else raw.getDouble("centreOffset")
    val centreNote: String?
        get() = if (!raw.has("centreNote") || raw.isNull("centreNote")) null else raw.getString("centreNote")
    val gridFrame: VastuBrahmasthanProjectionDataGridFrame?
        get() = if (!raw.has("gridFrame") || raw.isNull("gridFrame")) null else VastuBrahmasthanProjectionDataGridFrame(raw.getJSONObject("gridFrame"))
    val inPlotArea: Double?
        get() = if (!raw.has("inPlotArea") || raw.isNull("inPlotArea")) null else raw.getDouble("inPlotArea")
    val inPlotFraction: Double?
        get() = if (!raw.has("inPlotFraction") || raw.isNull("inPlotFraction")) null else raw.getDouble("inPlotFraction")
    val shareOfPlotArea: Double?
        get() = if (!raw.has("shareOfPlotArea") || raw.isNull("shareOfPlotArea")) null else raw.getDouble("shareOfPlotArea")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val zoneRemedies: List<VastuCatalogReferenceDataZoneRemediesItem>?
        get() = if (!raw.has("zoneRemedies") || raw.isNull("zoneRemedies")) null else List(raw.getJSONArray("zoneRemedies").length()) { index0 -> VastuCatalogReferenceDataZoneRemediesItem((raw.getJSONArray("zoneRemedies").get(index0) as JSONObject)) }
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuChatUploadData(override val raw: JSONObject) : VastuData {
    val success: Boolean
        get() = raw.getBoolean("success")
    val uploadId: String
        get() = raw.getString("uploadId")
    val pages: Int
        get() = raw.getInt("pages")
    val charsExtracted: Int
        get() = raw.getInt("charsExtracted")
    val expiresAt: String
        get() = raw.getString("expiresAt")
    val digestSha256: String
        get() = raw.getString("digestSha256")
    val pagesSkipped: Int?
        get() = if (!raw.has("pagesSkipped") || raw.isNull("pagesSkipped")) null else raw.getInt("pagesSkipped")
    val textTruncated: Boolean?
        get() = if (!raw.has("textTruncated") || raw.isNull("textTruncated")) null else raw.getBoolean("textTruncated")
    val replayed: Boolean?
        get() = if (!raw.has("replayed") || raw.isNull("replayed")) null else raw.getBoolean("replayed")
    val billing: VastuChatUploadDataBilling?
        get() = if (!raw.has("billing") || raw.isNull("billing")) null else VastuChatUploadDataBilling(raw.getJSONObject("billing"))
    val fileSha256: String
        get() = raw.getString("fileSha256")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuCompareVersionsData(override val raw: JSONObject) : VastuData {
    val inputHash: String
        get() = raw.getString("inputHash")
    val operation: String
        get() = raw.getString("operation")
    val fromVersion: String
        get() = raw.getString("fromVersion")
    val toVersion: String
        get() = raw.getString("toVersion")
    val fromAssessment: JSONObject
        get() = raw.getJSONObject("fromAssessment")
    val toAssessment: JSONObject
        get() = raw.getJSONObject("toAssessment")
    val changed: Boolean
        get() = raw.getBoolean("changed")
    val changes: List<VastuCompareVersionsDataChangesItem>
        get() = List(raw.getJSONArray("changes").length()) { index0 -> VastuCompareVersionsDataChangesItem((raw.getJSONArray("changes").get(index0) as JSONObject)) }
    val retainedVersions: Int
        get() = raw.getInt("retainedVersions")
    val scope: String
        get() = raw.getString("scope")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuComplianceIndexData(override val raw: JSONObject) : VastuData {
    val score: Double?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getDouble("score")
    val complianceIndex: String?
        get() = if (!raw.has("complianceIndex") || raw.isNull("complianceIndex")) null else raw.getString("complianceIndex")
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
    val notAssessed: List<VastuComplianceIndexDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuComplianceIndexDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val scoreNote: String?
        get() = if (!raw.has("scoreNote") || raw.isNull("scoreNote")) null else raw.getString("scoreNote")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuComplianceIndexDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuComplianceIndexDataReceipt(raw.getJSONObject("receipt"))
}

data class VastuDetailedFloorPlanAuditData(override val raw: JSONObject) : VastuData {
    val score: Double?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getDouble("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
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
    val notAssessed: List<VastuDetailedFloorPlanAuditDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuDetailedFloorPlanAuditDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val scoreNote: String?
        get() = if (!raw.has("scoreNote") || raw.isNull("scoreNote")) null else raw.getString("scoreNote")
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuDetailedFloorPlanAuditDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuDetailedFloorPlanAuditDataReceipt(raw.getJSONObject("receipt"))
    val gradeStatus: String?
        get() = if (!raw.has("gradeStatus") || raw.isNull("gradeStatus")) null else raw.getString("gradeStatus")
    val gradeReasons: List<String>?
        get() = if (!raw.has("gradeReasons") || raw.isNull("gradeReasons")) null else List(raw.getJSONArray("gradeReasons").length()) { index0 -> (raw.getJSONArray("gradeReasons").get(index0) as String) }
}

data class VastuDirectionCorrectData(override val raw: JSONObject) : VastuData {
    val input: JSONObject
        get() = raw.getJSONObject("input")
    val magneticBearingDeg: Double?
        get() = if (!raw.has("magneticBearingDeg") || raw.isNull("magneticBearingDeg")) null else raw.getDouble("magneticBearingDeg")
    val declinationDeg: Double?
        get() = if (!raw.has("declinationDeg") || raw.isNull("declinationDeg")) null else raw.getDouble("declinationDeg")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuDirectionDeclinationData(override val raw: JSONObject) : VastuData {
    val lat: Double
        get() = raw.getDouble("lat")
    val lon: Double
        get() = raw.getDouble("lon")
    val date: String
        get() = raw.getString("date")
    val declinationDeg: Double?
        get() = if (!raw.has("declinationDeg") || raw.isNull("declinationDeg")) null else raw.getDouble("declinationDeg")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuDrawingSheetData(override val raw: JSONObject) : VastuData {
    val html: String
        get() = raw.getString("html")
    val svg: String
        get() = raw.getString("svg")
    val paperSize: String
        get() = raw.getString("paperSize")
    val paperWidthMm: Double
        get() = raw.getDouble("paperWidthMm")
    val paperHeightMm: Double
        get() = raw.getDouble("paperHeightMm")
    val scaleDenominator: Int
        get() = raw.getInt("scaleDenominator")
    val metresToPaperMm: Double
        get() = raw.getDouble("metresToPaperMm")
    val planWidthMm: Double
        get() = raw.getDouble("planWidthMm")
    val planHeightMm: Double
        get() = raw.getDouble("planHeightMm")
    val trueNorthDeg: Double
        get() = raw.getDouble("trueNorthDeg")
    val fieldEvidenceCount: Int
        get() = raw.getInt("fieldEvidenceCount")
    val contentType: String?
        get() = if (!raw.has("contentType") || raw.isNull("contentType")) null else raw.getString("contentType")
    val pdfBase64: String?
        get() = if (!raw.has("pdfBase64") || raw.isNull("pdfBase64")) null else raw.getString("pdfBase64")
    val inputUnits: String
        get() = raw.getString("inputUnits")
    val units: String
        get() = raw.getString("units")
    val metresPerInputUnit: Double
        get() = raw.getDouble("metresPerInputUnit")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuEntranceRecommendData(override val raw: JSONObject) : VastuData {
    val facing: JSONObject
        get() = raw.getJSONObject("facing")
    val bestEntrancePada: JSONObject
        get() = raw.getJSONObject("bestEntrancePada")
    val bestEntranceIsUnfavourable: Boolean?
        get() = if (!raw.has("bestEntranceIsUnfavourable") || raw.isNull("bestEntranceIsUnfavourable")) null else raw.getBoolean("bestEntranceIsUnfavourable")
    val bestEntranceNote: String?
        get() = if (!raw.has("bestEntranceNote") || raw.isNull("bestEntranceNote")) null else raw.getString("bestEntranceNote")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuFeedListingsData(override val raw: JSONObject) : VastuData {
    val accepted: Int
        get() = raw.getInt("accepted")
    val rejected: Int
        get() = raw.getInt("rejected")
    val skipped: Int
        get() = raw.getInt("skipped")
    val results: List<JSONObject>
        get() = List(raw.getJSONArray("results").length()) { index0 -> (raw.getJSONArray("results").get(index0) as JSONObject) }
    val dryRun: Boolean?
        get() = if (!raw.has("dryRun") || raw.isNull("dryRun")) null else raw.getBoolean("dryRun")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuFloorPlanAuditData(override val raw: JSONObject) : VastuData {
    val score: Double?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getDouble("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
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
    val notAssessed: List<VastuFloorPlanAuditDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuFloorPlanAuditDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val scoreNote: String?
        get() = if (!raw.has("scoreNote") || raw.isNull("scoreNote")) null else raw.getString("scoreNote")
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuFloorPlanAuditDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuFloorPlanAuditDataReceipt(raw.getJSONObject("receipt"))
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuFusionChartData(override val raw: JSONObject) : VastuData {
    val ascendant: JSONObject
        get() = raw.getJSONObject("ascendant")
    val grahaDirections: List<JSONObject>
        get() = List(raw.getJSONArray("grahaDirections").length()) { index0 -> (raw.getJSONArray("grahaDirections").get(index0) as JSONObject) }
    val favourableDirections: List<JSONObject>
        get() = List(raw.getJSONArray("favourableDirections").length()) { index0 -> (raw.getJSONArray("favourableDirections").get(index0) as JSONObject) }
    val cautionDirections: List<VastuFusionChartDataCautionDirectionsItem>
        get() = List(raw.getJSONArray("cautionDirections").length()) { index0 -> VastuFusionChartDataCautionDirectionsItem((raw.getJSONArray("cautionDirections").get(index0) as JSONObject)) }
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuJobResultsData(override val raw: JSONObject) : VastuData {
    val jobId: String
        get() = raw.getString("jobId")
    val jobStatus: String
        get() = raw.getString("jobStatus")
    val results: List<Any?>
        get() = List(raw.getJSONArray("results").length()) { index0 -> raw.getJSONArray("results").get(index0).takeUnless { it == JSONObject.NULL } }
    val nextCursor: String?
        get() = if (!raw.has("nextCursor") || raw.isNull("nextCursor")) null else raw.getString("nextCursor")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuJobStatusData(override val raw: JSONObject) : VastuData {
    val jobId: String
        get() = raw.getString("jobId")
    val status: String
        get() = raw.getString("status")
    val operation: String
        get() = raw.getString("operation")
    val itemCount: Int
        get() = raw.getInt("itemCount")
    val counts: VastuJobStatusDataCounts
        get() = VastuJobStatusDataCounts(raw.getJSONObject("counts"))
    val billing: VastuJobStatusDataBilling
        get() = VastuJobStatusDataBilling(raw.getJSONObject("billing"))
    val cancelRequested: Boolean
        get() = raw.getBoolean("cancelRequested")
    val webhookId: String?
        get() = if (!raw.has("webhookId") || raw.isNull("webhookId")) null else raw.getString("webhookId")
    val createdAt: Int
        get() = raw.getInt("createdAt")
    val updatedAt: Int
        get() = raw.getInt("updatedAt")
    val finishedAt: Int?
        get() = if (!raw.has("finishedAt") || raw.isNull("finishedAt")) null else raw.getInt("finishedAt")
    val expiresAt: Int
        get() = raw.getInt("expiresAt")
    val resultsUrl: String
        get() = raw.getString("resultsUrl")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuJobSubmitData(override val raw: JSONObject) : VastuData {
    val jobId: String
        get() = raw.getString("jobId")
    val status: String
        get() = raw.getString("status")
    val itemCount: Int
        get() = raw.getInt("itemCount")
    val maxCharge: Double
        get() = raw.getDouble("maxCharge")
    val replayed: Boolean
        get() = raw.getBoolean("replayed")
    val preview: List<Any?>?
        get() = if (!raw.has("preview") || raw.isNull("preview")) null else List(raw.getJSONArray("preview").length()) { index0 -> raw.getJSONArray("preview").get(index0).takeUnless { it == JSONObject.NULL } }
    val previewNote: String?
        get() = if (!raw.has("previewNote") || raw.isNull("previewNote")) null else raw.getString("previewNote")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuMainGateData(override val raw: JSONObject) : VastuData {
    val facing: String
        get() = raw.getString("facing")
    val padaScheme: String
        get() = raw.getString("padaScheme")
    val prescribedPadas: List<Int>
        get() = List(raw.getJSONArray("prescribedPadas").length()) { index0 -> (raw.getJSONArray("prescribedPadas").get(index0) as Number).toInt() }
    val facingAffectsPrescribedPadas: Boolean?
        get() = if (!raw.has("facingAffectsPrescribedPadas") || raw.isNull("facingAffectsPrescribedPadas")) null else raw.getBoolean("facingAffectsPrescribedPadas")
    val facingNote: String?
        get() = if (!raw.has("facingNote") || raw.isNull("facingNote")) null else raw.getString("facingNote")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val gridFrame: VastuMandalaProjectionDataGridFrame?
        get() = if (!raw.has("gridFrame") || raw.isNull("gridFrame")) null else VastuMandalaProjectionDataGridFrame(raw.getJSONObject("gridFrame"))
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuMeasurementUncertainty(override val raw: JSONObject) : VastuData {
    val pointResult: JSONObject
        get() = raw.getJSONObject("pointResult")
    val results: List<VastuMeasurementUncertaintyResultsItem>
        get() = List(raw.getJSONArray("results").length()) { index0 -> VastuMeasurementUncertaintyResultsItem((raw.getJSONArray("results").get(index0) as JSONObject)) }
    val stable: Boolean
        get() = raw.getBoolean("stable")
    val bounds: VastuMeasurementUncertaintyBounds
        get() = VastuMeasurementUncertaintyBounds(raw.getJSONObject("bounds"))
    val coordinateUnits: String?
        get() = if (!raw.has("coordinateUnits") || raw.isNull("coordinateUnits")) null else raw.getString("coordinateUnits")
    val rulesChanged: Boolean
        get() = raw.getBoolean("rulesChanged")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuOverallScoreData(override val raw: JSONObject) : VastuData {
    val score: Double?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getDouble("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
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
    val notAssessed: List<VastuOverallScoreDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuOverallScoreDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val scoreNote: String?
        get() = if (!raw.has("scoreNote") || raw.isNull("scoreNote")) null else raw.getString("scoreNote")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuOverallScoreDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuOverallScoreDataReceipt(raw.getJSONObject("receipt"))
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val plotShape: JSONObject?
        get() = if (!raw.has("plotShape") || raw.isNull("plotShape")) null else raw.getJSONObject("plotShape")
    val overallScore: Double?
        get() = if (!raw.has("overallScore") || raw.isNull("overallScore")) null else raw.getDouble("overallScore")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
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
    val tracedGeometry: VastuPlanAuditDataTracedGeometry?
        get() = if (!raw.has("tracedGeometry") || raw.isNull("tracedGeometry")) null else VastuPlanAuditDataTracedGeometry(raw.getJSONObject("tracedGeometry"))
    val gradeLabel: String?
        get() = if (!raw.has("gradeLabel") || raw.isNull("gradeLabel")) null else raw.getString("gradeLabel")
    val scoreDisclaimer: String?
        get() = if (!raw.has("scoreDisclaimer") || raw.isNull("scoreDisclaimer")) null else raw.getString("scoreDisclaimer")
    val artifact: VastuPlanAuditDataArtifact?
        get() = if (!raw.has("artifact") || raw.isNull("artifact")) null else VastuPlanAuditDataArtifact(raw.getJSONObject("artifact"))
    val notAssessed: List<VastuPlanAuditDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuPlanAuditDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val scoreNote: String?
        get() = if (!raw.has("scoreNote") || raw.isNull("scoreNote")) null else raw.getString("scoreNote")
    val warnings: List<JSONObject>?
        get() = if (!raw.has("warnings") || raw.isNull("warnings")) null else List(raw.getJSONArray("warnings").length()) { index0 -> (raw.getJSONArray("warnings").get(index0) as JSONObject) }
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuPlanAuditDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuPlanAuditDataReceipt(raw.getJSONObject("receipt"))
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
    val gradeStatus: String?
        get() = if (!raw.has("gradeStatus") || raw.isNull("gradeStatus")) null else raw.getString("gradeStatus")
    val gradeReasons: List<String>?
        get() = if (!raw.has("gradeReasons") || raw.isNull("gradeReasons")) null else List(raw.getJSONArray("gradeReasons").length()) { index0 -> (raw.getJSONArray("gradeReasons").get(index0) as String) }
    val directionalScoreStatus: String?
        get() = if (!raw.has("directionalScoreStatus") || raw.isNull("directionalScoreStatus")) null else raw.getString("directionalScoreStatus")
    val directionalScoreMessage: String?
        get() = if (!raw.has("directionalScoreMessage") || raw.isNull("directionalScoreMessage")) null else raw.getString("directionalScoreMessage")
}

data class VastuPlanConvertUnitsData(override val raw: JSONObject) : VastuData {
    val plan: JSONObject
        get() = raw.getJSONObject("plan")
    val inputUnits: String
        get() = raw.getString("inputUnits")
    val outputUnits: String
        get() = raw.getString("outputUnits")
    val scaleFactor: Double
        get() = raw.getDouble("scaleFactor")
    val canonicalUnits: String
        get() = raw.getString("canonicalUnits")
    val pricing: JSONObject?
        get() = if (!raw.has("pricing") || raw.isNull("pricing")) null else raw.getJSONObject("pricing")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
}

data class VastuPlanExportDxfData(override val raw: JSONObject) : VastuData {
    val dxf: String
        get() = raw.getString("dxf")
    val contentType: String
        get() = raw.getString("contentType")
    val fileName: String
        get() = raw.getString("fileName")
    val version: String
        get() = raw.getString("version")
    val unitsCode: Int
        get() = raw.getInt("unitsCode")
    val trueNorthDeg: Double
        get() = raw.getDouble("trueNorthDeg")
    val zones: Int
        get() = raw.getInt("zones")
    val roomCount: Int
        get() = raw.getInt("roomCount")
    val openingCount: Int
        get() = raw.getInt("openingCount")
    val dimensionCount: Int
        get() = raw.getInt("dimensionCount")
    val findingCount: Int
        get() = raw.getInt("findingCount")
    val needsReview: Boolean
        get() = raw.getBoolean("needsReview")
    val pricing: JSONObject?
        get() = if (!raw.has("pricing") || raw.isNull("pricing")) null else raw.getJSONObject("pricing")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
    val canonicalUnits: String?
        get() = if (!raw.has("canonicalUnits") || raw.isNull("canonicalUnits")) null else raw.getString("canonicalUnits")
}

data class VastuPlanExportIfcData(override val raw: JSONObject) : VastuData {
    val ifc: String
        get() = raw.getString("ifc")
    val schema: String
        get() = raw.getString("schema")
    val contentType: String
        get() = raw.getString("contentType")
    val fileName: String
        get() = raw.getString("fileName")
    val outputUnits: String
        get() = raw.getString("outputUnits")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val roomCount: Int
        get() = raw.getInt("roomCount")
    val pricing: JSONObject?
        get() = if (!raw.has("pricing") || raw.isNull("pricing")) null else raw.getJSONObject("pricing")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
    val canonicalUnits: String
        get() = raw.getString("canonicalUnits")
    val storeyCount: Int?
        get() = if (!raw.has("storeyCount") || raw.isNull("storeyCount")) null else raw.getInt("storeyCount")
    val storeys: List<VastuPlanExportIfcDataStoreysItem>?
        get() = if (!raw.has("storeys") || raw.isNull("storeys")) null else List(raw.getJSONArray("storeys").length()) { index0 -> VastuPlanExportIfcDataStoreysItem((raw.getJSONArray("storeys").get(index0) as JSONObject)) }
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
    val floors: List<VastuPlanGenerateDataFloorsItem>?
        get() = if (!raw.has("floors") || raw.isNull("floors")) null else List(raw.getJSONArray("floors").length()) { index0 -> VastuPlanGenerateDataFloorsItem((raw.getJSONArray("floors").get(index0) as JSONObject)) }
    val core: JSONObject?
        get() = if (!raw.has("core") || raw.isNull("core")) null else raw.getJSONObject("core")
    val verticalChecks: List<JSONObject>?
        get() = if (!raw.has("verticalChecks") || raw.isNull("verticalChecks")) null else List(raw.getJSONArray("verticalChecks").length()) { index0 -> (raw.getJSONArray("verticalChecks").get(index0) as JSONObject) }
    val floorNote: String?
        get() = if (!raw.has("floorNote") || raw.isNull("floorNote")) null else raw.getString("floorNote")
    val warnings: List<VastuPlanGenerateDataWarningsItem>?
        get() = if (!raw.has("warnings") || raw.isNull("warnings")) null else List(raw.getJSONArray("warnings").length()) { index0 -> VastuPlanGenerateDataWarningsItem((raw.getJSONArray("warnings").get(index0) as JSONObject)) }
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
    val plotRegion: VastuPlanGenerateDataPlotRegion?
        get() = if (!raw.has("plotRegion") || raw.isNull("plotRegion")) null else VastuPlanGenerateDataPlotRegion(raw.getJSONObject("plotRegion"))
}

data class VastuPlanImportData(override val raw: JSONObject) : VastuData {
    val plan: VastuPlanImportDataPlan
        get() = VastuPlanImportDataPlan(raw.getJSONObject("plan"))
    val north: VastuPlanImportDataNorth
        get() = VastuPlanImportDataNorth(raw.getJSONObject("north"))
    val scale: VastuPlanImportDataScale
        get() = VastuPlanImportDataScale(raw.getJSONObject("scale"))
    val dimensions: List<VastuPlanImportDataDimensionsItem>
        get() = List(raw.getJSONArray("dimensions").length()) { index0 -> VastuPlanImportDataDimensionsItem((raw.getJSONArray("dimensions").get(index0) as JSONObject)) }
    val confidence: JSONObject
        get() = raw.getJSONObject("confidence")
    val needsReview: List<VastuPlanImportDataNeedsReviewItem>
        get() = List(raw.getJSONArray("needsReview").length()) { index0 -> VastuPlanImportDataNeedsReviewItem((raw.getJSONArray("needsReview").get(index0) as JSONObject)) }
    val analysisReady: Boolean
        get() = raw.getBoolean("analysisReady")
    val source: VastuPlanImportDataSource
        get() = VastuPlanImportDataSource(raw.getJSONObject("source"))
    val usage: VastuPlanImportDataUsage
        get() = VastuPlanImportDataUsage(raw.getJSONObject("usage"))
    val pricing: VastuPlanImportDataPricing
        get() = VastuPlanImportDataPricing(raw.getJSONObject("pricing"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
}

data class VastuPlanImportDxfData(override val raw: JSONObject) : VastuData {
    val plan: VastuPlanImportDxfDataPlan
        get() = VastuPlanImportDxfDataPlan(raw.getJSONObject("plan"))
    val mappingReport: List<VastuPlanImportDxfDataMappingReportItem>
        get() = List(raw.getJSONArray("mappingReport").length()) { index0 -> VastuPlanImportDxfDataMappingReportItem((raw.getJSONArray("mappingReport").get(index0) as JSONObject)) }
    val needsReview: Boolean
        get() = raw.getBoolean("needsReview")
    val reviewReasons: List<VastuPlanImportDxfDataReviewReasonsItem>
        get() = List(raw.getJSONArray("reviewReasons").length()) { index0 -> VastuPlanImportDxfDataReviewReasonsItem((raw.getJSONArray("reviewReasons").get(index0) as JSONObject)) }
    val pricing: JSONObject?
        get() = if (!raw.has("pricing") || raw.isNull("pricing")) null else raw.getJSONObject("pricing")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
    val canonicalUnits: String?
        get() = if (!raw.has("canonicalUnits") || raw.isNull("canonicalUnits")) null else raw.getString("canonicalUnits")
}

data class VastuPlanImportIfcData(override val raw: JSONObject) : VastuData {
    val schema: String
        get() = raw.getString("schema")
    val buildings: List<VastuPlanImportIfcDataBuildingsItem>
        get() = List(raw.getJSONArray("buildings").length()) { index0 -> VastuPlanImportIfcDataBuildingsItem((raw.getJSONArray("buildings").get(index0) as JSONObject)) }
    val storeys: List<VastuPlanImportIfcDataStoreysItem>
        get() = List(raw.getJSONArray("storeys").length()) { index0 -> VastuPlanImportIfcDataStoreysItem((raw.getJSONArray("storeys").get(index0) as JSONObject)) }
    val trueNorthDeg: Double
        get() = raw.getDouble("trueNorthDeg")
    val mappingReport: List<VastuPlanImportIfcDataMappingReportItem>
        get() = List(raw.getJSONArray("mappingReport").length()) { index0 -> VastuPlanImportIfcDataMappingReportItem((raw.getJSONArray("mappingReport").get(index0) as JSONObject)) }
    val needsReview: Boolean
        get() = raw.getBoolean("needsReview")
    val reviewReasons: List<VastuPlanImportIfcDataReviewReasonsItem>
        get() = List(raw.getJSONArray("reviewReasons").length()) { index0 -> VastuPlanImportIfcDataReviewReasonsItem((raw.getJSONArray("reviewReasons").get(index0) as JSONObject)) }
    val pricing: JSONObject?
        get() = if (!raw.has("pricing") || raw.isNull("pricing")) null else raw.getJSONObject("pricing")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
    val canonicalUnits: String?
        get() = if (!raw.has("canonicalUnits") || raw.isNull("canonicalUnits")) null else raw.getString("canonicalUnits")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val unmetConstraints: List<JSONObject>?
        get() = if (!raw.has("unmetConstraints") || raw.isNull("unmetConstraints")) null else List(raw.getJSONArray("unmetConstraints").length()) { index0 -> (raw.getJSONArray("unmetConstraints").get(index0) as JSONObject) }
    val initialConstraintViolations: List<JSONObject>?
        get() = if (!raw.has("initialConstraintViolations") || raw.isNull("initialConstraintViolations")) null else List(raw.getJSONArray("initialConstraintViolations").length()) { index0 -> (raw.getJSONArray("initialConstraintViolations").get(index0) as JSONObject) }
    val feasible: Boolean?
        get() = if (!raw.has("feasible") || raw.isNull("feasible")) null else raw.getBoolean("feasible")
    val search: JSONObject?
        get() = if (!raw.has("search") || raw.isNull("search")) null else raw.getJSONObject("search")
    val scoring: JSONObject?
        get() = if (!raw.has("scoring") || raw.isNull("scoring")) null else raw.getJSONObject("scoring")
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val units: String?
        get() = if (!raw.has("units") || raw.isNull("units")) null else raw.getString("units")
    val inputUnits: String?
        get() = if (!raw.has("inputUnits") || raw.isNull("inputUnits")) null else raw.getString("inputUnits")
    val metresPerInputUnit: Double?
        get() = if (!raw.has("metresPerInputUnit") || raw.isNull("metresPerInputUnit")) null else raw.getDouble("metresPerInputUnit")
    val plotRegion: VastuPlanOptimizeDataPlotRegion?
        get() = if (!raw.has("plotRegion") || raw.isNull("plotRegion")) null else VastuPlanOptimizeDataPlotRegion(raw.getJSONObject("plotRegion"))
}

data class VastuPlotExtensionsCutsData(override val raw: JSONObject) : VastuData {
    val directions: List<JSONObject>
        get() = List(raw.getJSONArray("directions").length()) { index0 -> (raw.getJSONArray("directions").get(index0) as JSONObject) }
    val extensions: List<String>
        get() = List(raw.getJSONArray("extensions").length()) { index0 -> (raw.getJSONArray("extensions").get(index0) as String) }
    val cuts: List<String>
        get() = List(raw.getJSONArray("cuts").length()) { index0 -> (raw.getJSONArray("cuts").get(index0) as String) }
    val severeCuts: List<String>
        get() = List(raw.getJSONArray("severeCuts").length()) { index0 -> (raw.getJSONArray("severeCuts").get(index0) as String) }
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuPlotFromSurveyData(override val raw: JSONObject) : VastuData {
    val method: String
        get() = raw.getString("method")
    val sourceCrs: String
        get() = raw.getString("sourceCrs")
    val sourceUnits: String
        get() = raw.getString("sourceUnits")
    val coordinateOrder: String
        get() = raw.getString("coordinateOrder")
    val frame: JSONObject
        get() = raw.getJSONObject("frame")
    val plotPolygon: List<List<Double>>
        get() = List(raw.getJSONArray("plotPolygon").length()) { index0 -> List((raw.getJSONArray("plotPolygon").get(index0) as org.json.JSONArray).length()) { index1 -> ((raw.getJSONArray("plotPolygon").get(index0) as org.json.JSONArray).get(index1) as Number).toDouble() } }
    val controlPoints: List<JSONObject>
        get() = List(raw.getJSONArray("controlPoints").length()) { index0 -> (raw.getJSONArray("controlPoints").get(index0) as JSONObject) }
    val areaM2: Double
        get() = raw.getDouble("areaM2")
    val gridConvergenceDeg: Double
        get() = raw.getDouble("gridConvergenceDeg")
    val boundaryGridConvergenceDeg: List<Double>
        get() = List(raw.getJSONArray("boundaryGridConvergenceDeg").length()) { index0 -> (raw.getJSONArray("boundaryGridConvergenceDeg").get(index0) as Number).toDouble() }
    val trueNorthGridBearingDeg: Double
        get() = raw.getDouble("trueNorthGridBearingDeg")
    val maxDistanceFromOriginM: Double
        get() = raw.getDouble("maxDistanceFromOriginM")
    val pricing: VastuPlotFromSurveyDataPricing?
        get() = if (!raw.has("pricing") || raw.isNull("pricing")) null else VastuPlotFromSurveyDataPricing(raw.getJSONObject("pricing"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rectangular: Boolean?
        get() = if (!raw.has("rectangular") || raw.isNull("rectangular")) null else raw.getBoolean("rectangular")
    val fillRatio: Double?
        get() = if (!raw.has("fillRatio") || raw.isNull("fillRatio")) null else raw.getDouble("fillRatio")
    val ratioScope: String?
        get() = if (!raw.has("ratioScope") || raw.isNull("ratioScope")) null else raw.getString("ratioScope")
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuPortfolioAnalyticsData(override val raw: JSONObject) : VastuData {
    val fromEpoch: Long
        get() = raw.getLong("fromEpoch")
    val toEpoch: Long
        get() = raw.getLong("toEpoch")
    val account: VastuPortfolioAnalyticsDataAccount
        get() = VastuPortfolioAnalyticsDataAccount(raw.getJSONObject("account"))
    val byTag: JSONObject
        get() = raw.getJSONObject("byTag")
}

data class VastuPortfolioBudgetsGetData(override val raw: JSONObject) : VastuData {
    val dimensions: JSONObject
        get() = raw.getJSONObject("dimensions")
    val period: String
        get() = raw.getString("period")
}

data class VastuPortfolioBudgetsSetData(override val raw: JSONObject) : VastuData {
    val scope: VastuPortfolioBudgetsSetDataScope
        get() = VastuPortfolioBudgetsSetDataScope(raw.getJSONObject("scope"))
    val capUsd: String?
        get() = if (!raw.has("capUsd") || raw.isNull("capUsd")) null else raw.getString("capUsd")
    val period: String
        get() = raw.getString("period")
}

data class VastuPortfolioCompareData(override val raw: JSONObject) : VastuData {
    val properties: List<VastuPortfolioCompareDataPropertiesItem>
        get() = List(raw.getJSONArray("properties").length()) { index0 -> VastuPortfolioCompareDataPropertiesItem((raw.getJSONArray("properties").get(index0) as JSONObject)) }
    val comparableGroups: JSONObject
        get() = raw.getJSONObject("comparableGroups")
    val crossRulesetRanking: Boolean
        get() = raw.getBoolean("crossRulesetRanking")
}

data class VastuPortfolioSearchData(override val raw: JSONObject) : VastuData {
    val properties: List<VastuPortfolioSearchDataPropertiesItem>
        get() = List(raw.getJSONArray("properties").length()) { index0 -> VastuPortfolioSearchDataPropertiesItem((raw.getJSONArray("properties").get(index0) as JSONObject)) }
    val total: Int
        get() = raw.getInt("total")
    val nextCursor: String?
        get() = if (!raw.has("nextCursor") || raw.isNull("nextCursor")) null else raw.getString("nextCursor")
}

data class VastuPortfolioUsageData(override val raw: JSONObject) : VastuData {
    val fromEpoch: Long
        get() = raw.getLong("fromEpoch")
    val toEpoch: Long
        get() = raw.getLong("toEpoch")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val groups: List<VastuPortfolioUsageDataGroupsItem>
        get() = List(raw.getJSONArray("groups").length()) { index0 -> VastuPortfolioUsageDataGroupsItem((raw.getJSONArray("groups").get(index0) as JSONObject)) }
}

data class VastuPortfolioUsageExportData(override val raw: JSONObject) : VastuData {
    val csv: String
        get() = raw.getString("csv")
    val filename: String
        get() = raw.getString("filename")
    val contentType: String
        get() = raw.getString("contentType")
}

data class VastuPropertiesActivityExportData(override val raw: JSONObject) : VastuData {
    val events: List<VastuPropertiesActivityExportDataEventsItem>
        get() = List(raw.getJSONArray("events").length()) { index0 -> VastuPropertiesActivityExportDataEventsItem((raw.getJSONArray("events").get(index0) as JSONObject)) }
    val nextCursor: Long?
        get() = if (!raw.has("nextCursor") || raw.isNull("nextCursor")) null else raw.getLong("nextCursor")
    val format: String
        get() = raw.getString("format")
    val content: String
        get() = raw.getString("content")
}

data class VastuPropertiesActivityListData(override val raw: JSONObject) : VastuData {
    val events: List<VastuPropertiesActivityListDataEventsItem>
        get() = List(raw.getJSONArray("events").length()) { index0 -> VastuPropertiesActivityListDataEventsItem((raw.getJSONArray("events").get(index0) as JSONObject)) }
    val nextCursor: Long?
        get() = if (!raw.has("nextCursor") || raw.isNull("nextCursor")) null else raw.getLong("nextCursor")
}

data class VastuPropertiesCollaborationCommentData(override val raw: JSONObject) : VastuData {
    val comment: VastuPropertiesCollaborationCommentDataComment
        get() = VastuPropertiesCollaborationCommentDataComment(raw.getJSONObject("comment"))
}

data class VastuPropertiesCollaborationGetData(override val raw: JSONObject) : VastuData {
    val property: VastuPropertiesCollaborationGetDataProperty
        get() = VastuPropertiesCollaborationGetDataProperty(raw.getJSONObject("property"))
    val comments: List<VastuPropertiesCollaborationGetDataCommentsItem>
        get() = List(raw.getJSONArray("comments").length()) { index0 -> VastuPropertiesCollaborationGetDataCommentsItem((raw.getJSONArray("comments").get(index0) as JSONObject)) }
    val reviews: List<VastuPropertiesCollaborationGetDataReviewsItem>
        get() = List(raw.getJSONArray("reviews").length()) { index0 -> VastuPropertiesCollaborationGetDataReviewsItem((raw.getJSONArray("reviews").get(index0) as JSONObject)) }
}

data class VastuPropertiesCollaborationInviteData(override val raw: JSONObject) : VastuData {
    val invitationId: String
        get() = raw.getString("invitationId")
    val status: String
        get() = raw.getString("status")
}

data class VastuPropertiesCollaborationMembersData(override val raw: JSONObject) : VastuData {
    val members: JSONObject
        get() = raw.getJSONObject("members")
}

data class VastuPropertiesCollaborationReviewData(override val raw: JSONObject) : VastuData {
    val review: VastuPropertiesCollaborationReviewDataReview
        get() = VastuPropertiesCollaborationReviewDataReview(raw.getJSONObject("review"))
}

data class VastuPropertiesCollaborationRevokeData(override val raw: JSONObject) : VastuData {
    val accountId: String?
        get() = if (!raw.has("accountId") || raw.isNull("accountId")) null else raw.getString("accountId")
    val revoked: Boolean
        get() = raw.getBoolean("revoked")
    val invitationId: String?
        get() = if (!raw.has("invitationId") || raw.isNull("invitationId")) null else raw.getString("invitationId")
}

data class VastuPropertiesCollaborationUpdateData(override val raw: JSONObject) : VastuData {
    val property: VastuPropertiesCollaborationUpdateDataProperty
        get() = VastuPropertiesCollaborationUpdateDataProperty(raw.getJSONObject("property"))
}

data class VastuPropertiesCreateData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String
        get() = raw.getString("ownerId")
    val ids: VastuPropertiesCreateDataIds
        get() = VastuPropertiesCreateDataIds(raw.getJSONObject("ids"))
    val title: String
        get() = raw.getString("title")
    val data: JSONObject
        get() = raw.getJSONObject("data")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuPropertiesCreateDataArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuPropertiesCreateDataArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long
        get() = raw.getLong("updatedAtEpoch")
    val contentHash: String
        get() = raw.getString("contentHash")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuPropertiesDeleteData(override val raw: JSONObject) : VastuData {
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val propertyId: String
        get() = raw.getString("propertyId")
    val deleted: Boolean
        get() = raw.getBoolean("deleted")
    val erasureStatus: String?
        get() = if (!raw.has("erasureStatus") || raw.isNull("erasureStatus")) null else raw.getString("erasureStatus")
    val erasureReceipt: VastuPropertiesDeleteDataErasureReceipt?
        get() = if (!raw.has("erasureReceipt") || raw.isNull("erasureReceipt")) null else VastuPropertiesDeleteDataErasureReceipt(raw.getJSONObject("erasureReceipt"))
    val retryAfterEpoch: Long?
        get() = if (!raw.has("retryAfterEpoch") || raw.isNull("retryAfterEpoch")) null else raw.getLong("retryAfterEpoch")
    val exportDeleted: Boolean?
        get() = if (!raw.has("exportDeleted") || raw.isNull("exportDeleted")) null else raw.getBoolean("exportDeleted")
    val linkedScanIds: List<String>?
        get() = if (!raw.has("linkedScanIds") || raw.isNull("linkedScanIds")) null else List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>?
        get() = if (!raw.has("linkedAssessmentIds") || raw.isNull("linkedAssessmentIds")) null else List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val revisionId: String?
        get() = if (!raw.has("revisionId") || raw.isNull("revisionId")) null else raw.getString("revisionId")
    val replayed: Boolean?
        get() = if (!raw.has("replayed") || raw.isNull("replayed")) null else raw.getBoolean("replayed")
    val receiptId: String?
        get() = if (!raw.has("receiptId") || raw.isNull("receiptId")) null else raw.getString("receiptId")
    val receiptStatus: String?
        get() = if (!raw.has("receiptStatus") || raw.isNull("receiptStatus")) null else raw.getString("receiptStatus")
    val readReceipt: String?
        get() = if (!raw.has("readReceipt") || raw.isNull("readReceipt")) null else raw.getString("readReceipt")
}

data class VastuPropertiesGetData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String
        get() = raw.getString("ownerId")
    val ids: VastuPropertiesGetDataIds
        get() = VastuPropertiesGetDataIds(raw.getJSONObject("ids"))
    val title: String
        get() = raw.getString("title")
    val data: JSONObject
        get() = raw.getJSONObject("data")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuPropertiesGetDataArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuPropertiesGetDataArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long
        get() = raw.getLong("updatedAtEpoch")
    val contentHash: String
        get() = raw.getString("contentHash")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuPropertiesLinkScanData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String
        get() = raw.getString("ownerId")
    val ids: VastuPropertiesLinkScanDataIds
        get() = VastuPropertiesLinkScanDataIds(raw.getJSONObject("ids"))
    val title: String
        get() = raw.getString("title")
    val data: JSONObject
        get() = raw.getJSONObject("data")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuPropertiesLinkScanDataArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuPropertiesLinkScanDataArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long
        get() = raw.getLong("updatedAtEpoch")
    val contentHash: String
        get() = raw.getString("contentHash")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuPropertiesListData(override val raw: JSONObject) : VastuData {
    val properties: List<VastuPropertiesListDataPropertiesItem>
        get() = List(raw.getJSONArray("properties").length()) { index0 -> VastuPropertiesListDataPropertiesItem((raw.getJSONArray("properties").get(index0) as JSONObject)) }
    val nextCursor: String?
        get() = if (!raw.has("nextCursor") || raw.isNull("nextCursor")) null else raw.getString("nextCursor")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuPropertiesUpdateData(override val raw: JSONObject) : VastuData {
    val propertyId: String
        get() = raw.getString("propertyId")
    val ownerId: String
        get() = raw.getString("ownerId")
    val ids: VastuPropertiesUpdateDataIds
        get() = VastuPropertiesUpdateDataIds(raw.getJSONObject("ids"))
    val title: String
        get() = raw.getString("title")
    val data: JSONObject
        get() = raw.getJSONObject("data")
    val retentionDays: Int
        get() = raw.getInt("retentionDays")
    val expiresAtEpoch: Long
        get() = raw.getLong("expiresAtEpoch")
    val linkedScanIds: List<String>
        get() = List(raw.getJSONArray("linkedScanIds").length()) { index0 -> (raw.getJSONArray("linkedScanIds").get(index0) as String) }
    val linkedAssessmentIds: List<String>
        get() = List(raw.getJSONArray("linkedAssessmentIds").length()) { index0 -> (raw.getJSONArray("linkedAssessmentIds").get(index0) as String) }
    val archiveTier: VastuPropertiesUpdateDataArchiveTier?
        get() = if (!raw.has("archiveTier") || raw.isNull("archiveTier")) null else VastuPropertiesUpdateDataArchiveTier(raw.getJSONObject("archiveTier"))
    val externalId: String?
        get() = if (!raw.has("externalId") || raw.isNull("externalId")) null else raw.getString("externalId")
    val createdAtEpoch: Long
        get() = raw.getLong("createdAtEpoch")
    val updatedAtEpoch: Long
        get() = raw.getLong("updatedAtEpoch")
    val contentHash: String
        get() = raw.getString("contentHash")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuQuoteCalculateData(override val raw: JSONObject) : VastuData {
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val workflowId: String
        get() = raw.getString("workflowId")
    val currency: String
        get() = raw.getString("currency")
    val subtotal: String
        get() = raw.getString("subtotal")
    val total: String
        get() = raw.getString("total")
    val lineItems: List<VastuQuoteCalculateDataLineItemsItem>
        get() = List(raw.getJSONArray("lineItems").length()) { index0 -> VastuQuoteCalculateDataLineItemsItem((raw.getJSONArray("lineItems").get(index0) as JSONObject)) }
    val explanation: String
        get() = raw.getString("explanation")
}

data class VastuReceiptVerifyData(override val raw: JSONObject) : VastuData {
    val valid: Boolean
        get() = raw.getBoolean("valid")
    val receipt: VastuReceiptVerifyDataReceipt
        get() = VastuReceiptVerifyDataReceipt(raw.getJSONObject("receipt"))
    val inputMatched: Boolean?
        get() = if (!raw.has("inputMatched") || raw.isNull("inputMatched")) null else raw.getBoolean("inputMatched")
    val charged: Double
        get() = raw.getDouble("charged")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuRemediationTaskData(override val raw: JSONObject) : VastuData {
    val taskId: String
        get() = raw.getString("taskId")
    val reportRef: String
        get() = raw.getString("reportRef")
    val findingRef: String
        get() = raw.getString("findingRef")
    val remedyKey: String
        get() = raw.getString("remedyKey")
    val title: String
        get() = raw.getString("title")
    val status: String
        get() = raw.getString("status")
    val assignee: String?
        get() = if (!raw.has("assignee") || raw.isNull("assignee")) null else raw.getString("assignee")
    val dueDate: String?
        get() = if (!raw.has("dueDate") || raw.isNull("dueDate")) null else raw.getString("dueDate")
    val evidence: List<VastuRemediationTaskDataEvidenceItem>?
        get() = if (!raw.has("evidence") || raw.isNull("evidence")) null else List(raw.getJSONArray("evidence").length()) { index0 -> VastuRemediationTaskDataEvidenceItem((raw.getJSONArray("evidence").get(index0) as JSONObject)) }
    val createdAt: Long?
        get() = if (!raw.has("createdAt") || raw.isNull("createdAt")) null else raw.getLong("createdAt")
    val updatedAt: Long?
        get() = if (!raw.has("updatedAt") || raw.isNull("updatedAt")) null else raw.getLong("updatedAt")
    val completedAt: Long?
        get() = if (!raw.has("completedAt") || raw.isNull("completedAt")) null else raw.getLong("completedAt")
    val reassessment: JSONObject?
        get() = if (!raw.has("reassessment") || raw.isNull("reassessment")) null else raw.getJSONObject("reassessment")
    val reassessmentLink: VastuRemediationTaskDataReassessmentLink?
        get() = if (!raw.has("reassessmentLink") || raw.isNull("reassessmentLink")) null else VastuRemediationTaskDataReassessmentLink(raw.getJSONObject("reassessmentLink"))
    val history: List<JSONObject>?
        get() = if (!raw.has("history") || raw.isNull("history")) null else List(raw.getJSONArray("history").length()) { index0 -> (raw.getJSONArray("history").get(index0) as JSONObject) }
}

data class VastuRemedyComparisonData(override val raw: JSONObject) : VastuData {
    val before: VastuRemedyComparisonDataBefore
        get() = VastuRemedyComparisonDataBefore(raw.getJSONObject("before"))
    val after: VastuRemedyComparisonDataAfter
        get() = VastuRemedyComparisonDataAfter(raw.getJSONObject("after"))
    val scoreDelta: Double?
        get() = if (!raw.has("scoreDelta") || raw.isNull("scoreDelta")) null else raw.getDouble("scoreDelta")
    val scoring: JSONObject
        get() = raw.getJSONObject("scoring")
    val verdict: String?
        get() = if (!raw.has("verdict") || raw.isNull("verdict")) null else raw.getString("verdict")
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
    val notAssessed: List<VastuRemedyComparisonDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuRemedyComparisonDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val roomTypeApplied: String?
        get() = if (!raw.has("roomTypeApplied") || raw.isNull("roomTypeApplied")) null else raw.getString("roomTypeApplied")
    val roomTypeDefaulted: Boolean?
        get() = if (!raw.has("roomTypeDefaulted") || raw.isNull("roomTypeDefaulted")) null else raw.getBoolean("roomTypeDefaulted")
    val roomTypeNote: String?
        get() = if (!raw.has("roomTypeNote") || raw.isNull("roomTypeNote")) null else raw.getString("roomTypeNote")
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuRuleVersionsData(override val raw: JSONObject) : VastuData {
    val currentVersion: String
        get() = raw.getString("currentVersion")
    val retainedVersions: Int
        get() = raw.getInt("retainedVersions")
    val versions: List<String>
        get() = List(raw.getJSONArray("versions").length()) { index0 -> (raw.getJSONArray("versions").get(index0) as String) }
    val scope: String
        get() = raw.getString("scope")
    val scoringVersion: String
        get() = raw.getString("scoringVersion")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val auditStatus: VastuScanStoredDataAuditStatus?
        get() = if (!raw.has("auditStatus") || raw.isNull("auditStatus")) null else VastuScanStoredDataAuditStatus(raw.getJSONObject("auditStatus"))
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val propertyId: String?
        get() = if (!raw.has("propertyId") || raw.isNull("propertyId")) null else raw.getString("propertyId")
    val settlement: String?
        get() = if (!raw.has("settlement") || raw.isNull("settlement")) null else raw.getString("settlement")
    val alreadyDeleted: Boolean?
        get() = if (!raw.has("alreadyDeleted") || raw.isNull("alreadyDeleted")) null else raw.getBoolean("alreadyDeleted")
    val receiptId: String?
        get() = if (!raw.has("receiptId") || raw.isNull("receiptId")) null else raw.getString("receiptId")
    val deletionReceipt: VastuScansDeleteDataDeletionReceipt?
        get() = if (!raw.has("deletionReceipt") || raw.isNull("deletionReceipt")) null else VastuScansDeleteDataDeletionReceipt(raw.getJSONObject("deletionReceipt"))
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuScansRetrieveData(override val raw: JSONObject) : VastuData {
    val scan: Any?
        get() = raw.get("scan").takeUnless { it == JSONObject.NULL }
    val persistence: String
        get() = raw.getString("persistence")
    val previewNote: String?
        get() = if (!raw.has("previewNote") || raw.isNull("previewNote")) null else raw.getString("previewNote")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val deviceAttestation: Any?
        get() = if (!raw.has("deviceAttestation") || raw.isNull("deviceAttestation")) null else raw.get("deviceAttestation").takeUnless { it == JSONObject.NULL }
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuSpecializedAuditData(override val raw: JSONObject) : VastuData {
    val system: String
        get() = raw.getString("system")
    val method: String
        get() = raw.getString("method")
    val buildingType: String
        get() = raw.getString("buildingType")
    val score: Double?
        get() = if (!raw.has("score") || raw.isNull("score")) null else raw.getDouble("score")
    val grade: String?
        get() = if (!raw.has("grade") || raw.isNull("grade")) null else raw.getString("grade")
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
    val notAssessed: List<VastuSpecializedAuditDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuSpecializedAuditDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val scoreNote: String?
        get() = if (!raw.has("scoreNote") || raw.isNull("scoreNote")) null else raw.getString("scoreNote")
    val uncertainty: VastuMeasurementUncertainty?
        get() = if (!raw.has("uncertainty") || raw.isNull("uncertainty")) null else VastuMeasurementUncertainty(raw.getJSONObject("uncertainty"))
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuSpecializedAuditDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuSpecializedAuditDataReceipt(raw.getJSONObject("receipt"))
}

data class VastuSunPathData(override val raw: JSONObject) : VastuData {
    val input: VastuSunPathDataInput
        get() = VastuSunPathDataInput(raw.getJSONObject("input"))
    val sunriseUtc: String?
        get() = if (!raw.has("sunriseUtc") || raw.isNull("sunriseUtc")) null else raw.getString("sunriseUtc")
    val sunriseAzimuthDeg: Double?
        get() = if (!raw.has("sunriseAzimuthDeg") || raw.isNull("sunriseAzimuthDeg")) null else raw.getDouble("sunriseAzimuthDeg")
    val solarNoonUtc: String?
        get() = if (!raw.has("solarNoonUtc") || raw.isNull("solarNoonUtc")) null else raw.getString("solarNoonUtc")
    val solarNoonAzimuthDeg: Double?
        get() = if (!raw.has("solarNoonAzimuthDeg") || raw.isNull("solarNoonAzimuthDeg")) null else raw.getDouble("solarNoonAzimuthDeg")
    val solarNoonElevationDeg: Double?
        get() = if (!raw.has("solarNoonElevationDeg") || raw.isNull("solarNoonElevationDeg")) null else raw.getDouble("solarNoonElevationDeg")
    val sunsetUtc: String?
        get() = if (!raw.has("sunsetUtc") || raw.isNull("sunsetUtc")) null else raw.getString("sunsetUtc")
    val sunsetAzimuthDeg: Double?
        get() = if (!raw.has("sunsetAzimuthDeg") || raw.isNull("sunsetAzimuthDeg")) null else raw.getDouble("sunsetAzimuthDeg")
    val declinationDeg: Double?
        get() = if (!raw.has("declinationDeg") || raw.isNull("declinationDeg")) null else raw.getDouble("declinationDeg")
    val arc: List<JSONObject>
        get() = List(raw.getJSONArray("arc").length()) { index0 -> (raw.getJSONArray("arc").get(index0) as JSONObject) }
    val sources: List<String>
        get() = List(raw.getJSONArray("sources").length()) { index0 -> (raw.getJSONArray("sources").get(index0) as String) }
    val verified: Boolean
        get() = raw.getBoolean("verified")
    val dayStatus: String?
        get() = if (!raw.has("dayStatus") || raw.isNull("dayStatus")) null else raw.getString("dayStatus")
    val note: String?
        get() = if (!raw.has("note") || raw.isNull("note")) null else raw.getString("note")
    val noonUtc: String?
        get() = if (!raw.has("noonUtc") || raw.isNull("noonUtc")) null else raw.getString("noonUtc")
    val noonElevationDeg: Double?
        get() = if (!raw.has("noonElevationDeg") || raw.isNull("noonElevationDeg")) null else raw.getDouble("noonElevationDeg")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
}

data class VastuWorkflowData(override val raw: JSONObject) : VastuData {
    val revision: Long
        get() = raw.getLong("revision")
    val mutationId: String?
        get() = if (!raw.has("mutationId") || raw.isNull("mutationId")) null else raw.getString("mutationId")
    val contentHash: String?
        get() = if (!raw.has("contentHash") || raw.isNull("contentHash")) null else raw.getString("contentHash")
    val expiresAt: Long?
        get() = if (!raw.has("expiresAt") || raw.isNull("expiresAt")) null else raw.getLong("expiresAt")
    val updatedAt: Long?
        get() = if (!raw.has("updatedAt") || raw.isNull("updatedAt")) null else raw.getLong("updatedAt")
    val deleted: Boolean?
        get() = if (!raw.has("deleted") || raw.isNull("deleted")) null else raw.getBoolean("deleted")
    val data: VastuWorkflowDataData?
        get() = if (!raw.has("data") || raw.isNull("data")) null else VastuWorkflowDataData(raw.getJSONObject("data"))
    val remedies: List<VastuWorkflowDataRemediesItem>?
        get() = if (!raw.has("remedies") || raw.isNull("remedies")) null else List(raw.getJSONArray("remedies").length()) { index0 -> VastuWorkflowDataRemediesItem((raw.getJSONArray("remedies").get(index0) as JSONObject)) }
    val merchantCatalogId: String?
        get() = if (!raw.has("merchantCatalogId") || raw.isNull("merchantCatalogId")) null else raw.getString("merchantCatalogId")
    val merchantCatalogRevision: Long?
        get() = if (!raw.has("merchantCatalogRevision") || raw.isNull("merchantCatalogRevision")) null else raw.getLong("merchantCatalogRevision")
}

data class VastuWorkspaceData(override val raw: JSONObject) : VastuData {
    val record: JSONObject?
        get() = if (!raw.has("record") || raw.isNull("record")) null else raw.getJSONObject("record")
    val replayed: Boolean?
        get() = if (!raw.has("replayed") || raw.isNull("replayed")) null else raw.getBoolean("replayed")
    val records: List<JSONObject>?
        get() = if (!raw.has("records") || raw.isNull("records")) null else List(raw.getJSONArray("records").length()) { index0 -> (raw.getJSONArray("records").get(index0) as JSONObject) }
    val id: String?
        get() = if (!raw.has("id") || raw.isNull("id")) null else raw.getString("id")
    val title: String?
        get() = if (!raw.has("title") || raw.isNull("title")) null else raw.getString("title")
    val expiresAt: Long?
        get() = if (!raw.has("expiresAt") || raw.isNull("expiresAt")) null else raw.getLong("expiresAt")
    val reset: Boolean?
        get() = if (!raw.has("reset") || raw.isNull("reset")) null else raw.getBoolean("reset")
    val deletedRecords: Int?
        get() = if (!raw.has("deletedRecords") || raw.isNull("deletedRecords")) null else raw.getInt("deletedRecords")
    val payload: String?
        get() = if (!raw.has("payload") || raw.isNull("payload")) null else raw.getString("payload")
    val headers: JSONObject?
        get() = if (!raw.has("headers") || raw.isNull("headers")) null else raw.getJSONObject("headers")
    val deliveryMode: String?
        get() = if (!raw.has("deliveryMode") || raw.isNull("deliveryMode")) null else raw.getString("deliveryMode")
    val retentionDays: Int?
        get() = if (!raw.has("retentionDays") || raw.isNull("retentionDays")) null else raw.getInt("retentionDays")
    val maxRecords: Int?
        get() = if (!raw.has("maxRecords") || raw.isNull("maxRecords")) null else raw.getInt("maxRecords")
    val html: String?
        get() = if (!raw.has("html") || raw.isNull("html")) null else raw.getString("html")
    val svg: String?
        get() = if (!raw.has("svg") || raw.isNull("svg")) null else raw.getString("svg")
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
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
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
    val overallGrade: String?
        get() = if (!raw.has("overallGrade") || raw.isNull("overallGrade")) null else raw.getString("overallGrade")
    val overallScore: Double?
        get() = if (!raw.has("overallScore") || raw.isNull("overallScore")) null else raw.getDouble("overallScore")
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
    val notAssessed: List<VastuZoneWiseScoreDataNotAssessedItem>?
        get() = if (!raw.has("notAssessed") || raw.isNull("notAssessed")) null else List(raw.getJSONArray("notAssessed").length()) { index0 -> VastuZoneWiseScoreDataNotAssessedItem((raw.getJSONArray("notAssessed").get(index0) as JSONObject)) }
    val scoreNote: String?
        get() = if (!raw.has("scoreNote") || raw.isNull("scoreNote")) null else raw.getString("scoreNote")
    val rulesVersion: String?
        get() = if (!raw.has("rulesVersion") || raw.isNull("rulesVersion")) null else raw.getString("rulesVersion")
    val receipt: VastuZoneWiseScoreDataReceipt?
        get() = if (!raw.has("receipt") || raw.isNull("receipt")) null else VastuZoneWiseScoreDataReceipt(raw.getJSONObject("receipt"))
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
    val portfolioSearch: VastuContract<VastuPortfolioSearchRequest, VastuPortfolioSearchData> = VastuContract(VastuOperation.PortfolioSearch, ::VastuPortfolioSearchData)
    val portfolioCompare: VastuContract<VastuPortfolioCompareRequest, VastuPortfolioCompareData> = VastuContract(VastuOperation.PortfolioCompare, ::VastuPortfolioCompareData)
    val portfolioAnalytics: VastuContract<VastuPortfolioAnalyticsRequest, VastuPortfolioAnalyticsData> = VastuContract(VastuOperation.PortfolioAnalytics, ::VastuPortfolioAnalyticsData)
    val portfolioUsage: VastuContract<VastuPortfolioUsageRequest, VastuPortfolioUsageData> = VastuContract(VastuOperation.PortfolioUsage, ::VastuPortfolioUsageData)
    val portfolioUsageExport: VastuContract<VastuPortfolioUsageExportRequest, VastuPortfolioUsageExportData> = VastuContract(VastuOperation.PortfolioUsageExport, ::VastuPortfolioUsageExportData)
    val portfolioBudgetsSet: VastuContract<VastuPortfolioBudgetsSetRequest, VastuPortfolioBudgetsSetData> = VastuContract(VastuOperation.PortfolioBudgetsSet, ::VastuPortfolioBudgetsSetData)
    val portfolioBudgetsGet: VastuContract<VastuPortfolioBudgetsGetRequest, VastuPortfolioBudgetsGetData> = VastuContract(VastuOperation.PortfolioBudgetsGet, ::VastuPortfolioBudgetsGetData)

    val drawingSheet: VastuContract<VastuDrawingSheetRequest, VastuDrawingSheetData> = VastuContract(VastuOperation.DrawingSheet, ::VastuDrawingSheetData)
    val propertiesCreate: VastuContract<VastuPropertiesCreateRequest, VastuPropertiesCreateData> = VastuContract(VastuOperation.PropertiesCreate, ::VastuPropertiesCreateData)
    val propertiesUpdate: VastuContract<VastuPropertiesUpdateRequest, VastuPropertiesUpdateData> = VastuContract(VastuOperation.PropertiesUpdate, ::VastuPropertiesUpdateData)
    val propertiesCollaborationGet: VastuContract<VastuPropertiesCollaborationGetRequest, VastuPropertiesCollaborationGetData> = VastuContract(VastuOperation.PropertiesCollaborationGet, ::VastuPropertiesCollaborationGetData)
    val propertiesCollaborationInvite: VastuContract<VastuPropertiesCollaborationInviteRequest, VastuPropertiesCollaborationInviteData> = VastuContract(VastuOperation.PropertiesCollaborationInvite, ::VastuPropertiesCollaborationInviteData)
    val propertiesCollaborationRevoke: VastuContract<VastuPropertiesCollaborationRevokeRequest, VastuPropertiesCollaborationRevokeData> = VastuContract(VastuOperation.PropertiesCollaborationRevoke, ::VastuPropertiesCollaborationRevokeData)
    val propertiesCollaborationMembers: VastuContract<VastuPropertiesCollaborationMembersRequest, VastuPropertiesCollaborationMembersData> = VastuContract(VastuOperation.PropertiesCollaborationMembers, ::VastuPropertiesCollaborationMembersData)
    val propertiesCollaborationComment: VastuContract<VastuPropertiesCollaborationCommentRequest, VastuPropertiesCollaborationCommentData> = VastuContract(VastuOperation.PropertiesCollaborationComment, ::VastuPropertiesCollaborationCommentData)
    val propertiesCollaborationReview: VastuContract<VastuPropertiesCollaborationReviewRequest, VastuPropertiesCollaborationReviewData> = VastuContract(VastuOperation.PropertiesCollaborationReview, ::VastuPropertiesCollaborationReviewData)
    val propertiesCollaborationUpdate: VastuContract<VastuPropertiesCollaborationUpdateRequest, VastuPropertiesCollaborationUpdateData> = VastuContract(VastuOperation.PropertiesCollaborationUpdate, ::VastuPropertiesCollaborationUpdateData)
    val propertiesActivityList: VastuContract<VastuPropertiesActivityListRequest, VastuPropertiesActivityListData> = VastuContract(VastuOperation.PropertiesActivityList, ::VastuPropertiesActivityListData)
    val propertiesActivityExport: VastuContract<VastuPropertiesActivityExportRequest, VastuPropertiesActivityExportData> = VastuContract(VastuOperation.PropertiesActivityExport, ::VastuPropertiesActivityExportData)
    val propertiesGet: VastuContract<VastuPropertiesGetRequest, VastuPropertiesGetData> = VastuContract(VastuOperation.PropertiesGet, ::VastuPropertiesGetData)
    val propertiesList: VastuContract<VastuPropertiesListRequest, VastuPropertiesListData> = VastuContract(VastuOperation.PropertiesList, ::VastuPropertiesListData)
    val propertiesDelete: VastuContract<VastuPropertiesDeleteRequest, VastuPropertiesDeleteData> = VastuContract(VastuOperation.PropertiesDelete, ::VastuPropertiesDeleteData)
    val propertiesLinkScan: VastuContract<VastuPropertiesLinkScanRequest, VastuPropertiesLinkScanData> = VastuContract(VastuOperation.PropertiesLinkScan, ::VastuPropertiesLinkScanData)
    val archiveTier: VastuContract<VastuArchiveTierRequest, VastuArchiveTierData> = VastuContract(VastuOperation.ArchiveTier, ::VastuArchiveTierData)
    val archiveExport: VastuContract<VastuArchiveExportRequest, VastuArchiveExportData> = VastuContract(VastuOperation.ArchiveExport, ::VastuArchiveExportData)
    val archiveDelete: VastuContract<VastuArchiveDeleteRequest, VastuArchiveDeleteData> = VastuContract(VastuOperation.ArchiveDelete, ::VastuArchiveDeleteData)
    val archiveSummary: VastuContract<VastuArchiveSummaryRequest, VastuArchiveSummaryData> = VastuContract(VastuOperation.ArchiveSummary, ::VastuArchiveSummaryData)
    val feedListings: VastuContract<VastuFeedListingsRequest, VastuFeedListingsData> = VastuContract(VastuOperation.FeedListings, ::VastuFeedListingsData)
    val quoteCalculate: VastuContract<VastuQuoteCalculateRequest, VastuQuoteCalculateData> = VastuContract(VastuOperation.QuoteCalculate, ::VastuQuoteCalculateData)

    val arCountedScanQuality: VastuContract<VastuArCountedScanQualityRequest, VastuArScanQualityData> = VastuContract(VastuOperation.ArScanQuality, ::VastuArScanQualityData)






    val arDeityIcons: VastuContract<VastuArDeityIconsRequest, VastuArDeityIconsData> = VastuContract(VastuOperation.ArDeityIcons, ::VastuArDeityIconsData)

    val arCaptureMerge: VastuContract<VastuArCaptureMergeRequest, VastuArCaptureMergeData> = VastuContract(VastuOperation.ArCaptureMerge, ::VastuArCaptureMergeData)
    val plotFromSurvey: VastuContract<VastuPlotFromSurveyRequest, VastuPlotFromSurveyData> = VastuContract(VastuOperation.PlotFromSurvey, ::VastuPlotFromSurveyData)
    val arRoomCapture: VastuContract<VastuArRoomCaptureRequest, VastuArRoomCaptureData> = VastuContract(VastuOperation.ArRoomCapture, ::VastuArRoomCaptureData)

    val arAttestationChallenge: VastuContract<VastuArAttestationChallengeRequest, VastuArAttestationChallengeData> = VastuContract(VastuOperation.ArAttestationChallenge, ::VastuArAttestationChallengeData)

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

    val planImportDxf: VastuContract<VastuPlanImportDxfRequest, VastuPlanImportDxfData> = VastuContract(VastuOperation.PlanImportDxf, ::VastuPlanImportDxfData)
    val planExportDxf: VastuContract<VastuPlanExportDxfRequest, VastuPlanExportDxfData> = VastuContract(VastuOperation.PlanExportDxf, ::VastuPlanExportDxfData)
    val planExportIfc: VastuContract<VastuPlanExportIfcRequest, VastuPlanExportIfcData> = VastuContract(VastuOperation.PlanExportIfc, ::VastuPlanExportIfcData)
    val planConvertUnits: VastuContract<VastuPlanConvertUnitsRequest, VastuPlanConvertUnitsData> = VastuContract(VastuOperation.PlanConvertUnits, ::VastuPlanConvertUnitsData)
    val planImportIfc: VastuContract<VastuPlanImportIfcRequest, VastuPlanImportIfcData> = VastuContract(VastuOperation.PlanImportIfc, ::VastuPlanImportIfcData)
    val planImportImage: VastuContract<VastuPlanImportImageRequest, VastuPlanImportData> = VastuContract(VastuOperation.PlanImportImage, ::VastuPlanImportData)

    val planImportPdf: VastuContract<VastuPlanImportPdfRequest, VastuPlanImportData> = VastuContract(VastuOperation.PlanImportPdf, ::VastuPlanImportData)

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
 * rules, compliance audits, scoring, and floor-plan generation (98 logical backend
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
    suspend fun vastuCompareVersions(fromVersion: String, toVersion: String, input: JSONObject, operation: String = "plan/analyze", idempotencyKey: String? = null): VastuTypedResponse<VastuCompareVersionsData> {
        val raw = client.post("$BASE/plan/compare-versions", mapOf("fromVersion" to fromVersion, "toVersion" to toVersion, "input" to input, "operation" to operation), idempotencyKey = idempotencyKey)
        return VastuTypedResponse(raw.getBoolean("success"), VastuCompareVersionsData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuVerifyReceipt(token: String, input: JSONObject? = null): VastuTypedResponse<VastuReceiptVerifyData> {
        val raw = client.post("$BASE/receipt/verify", mapOf("token" to token, "input" to input).filterValues { it != null })
        return VastuTypedResponse(raw.getBoolean("success"), VastuReceiptVerifyData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuRuleVersions(): VastuTypedResponse<VastuRuleVersionsData> {
        val raw = client.get("$BASE/rules/versions")
        return VastuTypedResponse(raw.getBoolean("success"), VastuRuleVersionsData(raw.getJSONObject("data")), raw)
    }


    private companion object {
        const val BASE = "/v2/astrology/vastu"
    }

    /** Exact request and result types for one of the 100 mounted operations. */
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
     * everything else is POST, matching the server's route table.
     */
    suspend fun vastu(op: String, params: Map<String, Any?> = emptyMap(), idempotencyKey: String? = null): JSONObject {
        val path = stripLeadingSlash(op)
        if (path.startsWith("jobs/")) {
            val job = requireNotNull(VASTU_JOB_PATH.matchEntire(path)) {
                "Job paths are jobs/{jobId}, jobs/{jobId}/results and jobs/{jobId}/cancel"
            }
            requireVastuJobId(job.groupValues[1])
        }
        return if (isGetOp(path)) {
            client.get("$BASE/$path", stringifyParams(params), idempotencyKey = idempotencyKey)
        } else {
            client.post("$BASE/$path", params, idempotencyKey = idempotencyKey)
        }
    }

    suspend fun vastuPortfolioSearch(request: VastuPortfolioSearchRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPortfolioSearchData> {
        val raw = client.post("$BASE/portfolio/search", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPortfolioSearchData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPortfolioCompare(request: VastuPortfolioCompareRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPortfolioCompareData> {
        val raw = client.post("$BASE/portfolio/compare", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPortfolioCompareData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPortfolioAnalytics(request: VastuPortfolioAnalyticsRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPortfolioAnalyticsData> {
        val raw = client.post("$BASE/portfolio/analytics", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPortfolioAnalyticsData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPortfolioUsage(request: VastuPortfolioUsageRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPortfolioUsageData> {
        val raw = client.post("$BASE/portfolio/usage", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPortfolioUsageData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPortfolioUsageExport(request: VastuPortfolioUsageExportRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPortfolioUsageExportData> {
        val raw = client.post("$BASE/portfolio/usage/export", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPortfolioUsageExportData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPortfolioBudgetsSet(request: VastuPortfolioBudgetsSetRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPortfolioBudgetsSetData> {
        val raw = client.post("$BASE/portfolio/budgets/set", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPortfolioBudgetsSetData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPortfolioBudgetsGet(request: VastuPortfolioBudgetsGetRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPortfolioBudgetsGetData> {
        val raw = client.post("$BASE/portfolio/budgets/get", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPortfolioBudgetsGetData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuDrawingSheet(request: VastuDrawingSheetRequest, idempotencyKey: String? = null): VastuScanResponse<VastuDrawingSheetData> {
        val raw = client.post("$BASE/report/drawing-sheet", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuDrawingSheetData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuWorkspace(operation: VastuWorkspaceOperation, request: VastuWorkspaceRequest = VastuWorkspaceRequest()): VastuScanResponse<VastuWorkspaceData> {
        val raw = client.post("/sandbox/v2/vastu/workspace/${operation.path}", request.toMap())
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkspaceData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesCreate(request: VastuPropertiesCreateRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCreateData> {
        val raw = client.post("$BASE/properties/create", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCreateData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPropertiesUpdate(request: VastuPropertiesUpdateRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesUpdateData> {
        val raw = client.post("$BASE/properties/update", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesUpdateData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPropertiesCollaborationGet(request: VastuPropertiesCollaborationGetRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCollaborationGetData> {
        val raw = client.post("$BASE/properties/collaboration/get", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCollaborationGetData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesCollaborationInvite(request: VastuPropertiesCollaborationInviteRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCollaborationInviteData> {
        val raw = client.post("$BASE/properties/collaboration/invite", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCollaborationInviteData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesCollaborationRevoke(request: VastuPropertiesCollaborationRevokeRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCollaborationRevokeData> {
        val raw = client.post("$BASE/properties/collaboration/revoke", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCollaborationRevokeData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesCollaborationMembers(request: VastuPropertiesCollaborationMembersRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCollaborationMembersData> {
        val raw = client.post("$BASE/properties/collaboration/members", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCollaborationMembersData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesCollaborationComment(request: VastuPropertiesCollaborationCommentRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCollaborationCommentData> {
        val raw = client.post("$BASE/properties/collaboration/comment", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCollaborationCommentData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesCollaborationReview(request: VastuPropertiesCollaborationReviewRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCollaborationReviewData> {
        val raw = client.post("$BASE/properties/collaboration/review", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCollaborationReviewData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesCollaborationUpdate(request: VastuPropertiesCollaborationUpdateRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesCollaborationUpdateData> {
        val raw = client.post("$BASE/properties/collaboration/update", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesCollaborationUpdateData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesActivityList(request: VastuPropertiesActivityListRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesActivityListData> {
        val raw = client.post("$BASE/properties/activity/list", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesActivityListData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesActivityExport(request: VastuPropertiesActivityExportRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesActivityExportData> {
        val raw = client.post("$BASE/properties/activity/export", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesActivityExportData(raw.getJSONObject("data")), raw)
    }
    suspend fun vastuPropertiesGet(request: VastuPropertiesGetRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesGetData> {
        val raw = client.post("$BASE/properties/get", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesGetData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPropertiesList(request: VastuPropertiesListRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesListData> {
        val raw = client.post("$BASE/properties/list", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesListData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPropertiesDelete(request: VastuPropertiesDeleteRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesDeleteData> {
        val raw = client.post("$BASE/properties/delete", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesDeleteData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuPropertiesLinkScan(request: VastuPropertiesLinkScanRequest, idempotencyKey: String? = null): VastuScanResponse<VastuPropertiesLinkScanData> {
        val raw = client.post("$BASE/properties/link-scan", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuPropertiesLinkScanData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuArchiveTier(request: VastuArchiveTierRequest, idempotencyKey: String? = null): VastuScanResponse<VastuArchiveTierData> {
        val raw = client.post("$BASE/archive/tier", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuArchiveTierData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuArchiveExport(request: VastuArchiveExportRequest, idempotencyKey: String? = null): VastuScanResponse<VastuArchiveExportData> {
        val raw = client.post("$BASE/archive/export", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuArchiveExportData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuArchiveDelete(request: VastuArchiveDeleteRequest, idempotencyKey: String? = null): VastuScanResponse<VastuArchiveDeleteData> {
        val raw = client.post("$BASE/archive/delete", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuArchiveDeleteData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuArchiveSummary(request: VastuArchiveSummaryRequest, idempotencyKey: String? = null): VastuScanResponse<VastuArchiveSummaryData> {
        val raw = client.post("$BASE/archive/summary", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuArchiveSummaryData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuFeedListings(request: VastuFeedListingsRequest, idempotencyKey: String? = null): VastuScanResponse<VastuFeedListingsData> {
        val raw = client.post("$BASE/feed/listings", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuFeedListingsData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuQuoteCalculate(request: VastuQuoteCalculateRequest, idempotencyKey: String? = null): VastuScanResponse<VastuQuoteCalculateData> {
        val raw = client.post("$BASE/quote/calculate", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuQuoteCalculateData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuRemediationTasksUpsert(request: VastuRemediationTasksUpsertRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/remediation/tasks/upsert", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuRemediationTasksList(request: VastuRemediationTasksListRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/remediation/tasks/list", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuRemediationTasksDelete(request: VastuRemediationTasksDeleteRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/remediation/tasks/delete", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuRemediationReassess(request: VastuRemediationReassessRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/remediation/reassess", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuMerchantCatalogUpload(request: VastuMerchantCatalogUploadRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/merchant/catalog/upload", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuMerchantCatalogGet(request: VastuMerchantCatalogGetRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/merchant/catalog/get", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuMerchantCatalogDelete(request: VastuMerchantCatalogDeleteRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/merchant/catalog/delete", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
    }

    suspend fun vastuMerchantRemedies(request: VastuMerchantRemediesRequest, idempotencyKey: String? = null): VastuScanResponse<VastuWorkflowData> {
        val raw = client.post("$BASE/merchant/remedies", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuScanResponse(raw.getBoolean("success"), VastuWorkflowData(raw.getJSONObject("data")), raw)
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
        require(!operation.path.contains("{id}")) {
            "${operation.path} carries a job id; use vastuJobStatus, vastuJobResults or vastuJobCancel"
        }
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
     * Queue 1 to 1,000 assessments and get a jobId back at once (HTTP 202).
     *
     * [idempotencyKey] is mandatory and must be retained by the caller. Save
     * it with this exact request: after a lost response or a restart, submit
     * the same body with the same key and the original job comes back
     * (`data.replayed` is true) instead of a second paid job. Each item is
     * charged only after it succeeds.
     */
    suspend fun vastuJobSubmit(request: VastuJobsRequest, idempotencyKey: String): VastuJobSubmitResponse {
        val raw = client.post("$BASE/jobs", request.toMap(), idempotencyKey = idempotencyKey)
        return VastuJobSubmitResponse(raw.getBoolean("success"), VastuJobSubmitData(raw.getJSONObject("data")), raw)
    }

    /** Status, per-state counts and billing of a job. Free. */
    suspend fun vastuJobStatus(jobId: String): VastuJobStatusResponse {
        val raw = client.get("$BASE/jobs/${requireVastuJobId(jobId)}")
        return VastuJobStatusResponse(raw.getBoolean("success"), VastuJobStatusData(raw.getJSONObject("data")), raw)
    }

    /**
     * One page (up to 50) of finished item results, in item order. Free.
     * Cursor pagination only: pass the previous page's `data.nextCursor`; it
     * is null on the last page. See [vastuJobResultItems] to walk them all.
     */
    suspend fun vastuJobResults(jobId: String, cursor: String? = null): VastuJobResultsResponse {
        require(cursor == null || cursor.length in 1..32) { "cursor must be the nextCursor of the previous page (1 to 32 characters)" }
        val query = if (cursor == null) emptyMap() else mapOf("cursor" to cursor)
        val raw = client.get("$BASE/jobs/${requireVastuJobId(jobId)}/results", query)
        return VastuJobResultsResponse(raw.getBoolean("success"), VastuJobResultsData(raw.getJSONObject("data")), raw)
    }

    /** Every finished item of a job, following `nextCursor` until it is null. */
    fun vastuJobResultItems(jobId: String): kotlinx.coroutines.flow.Flow<VastuJobResultItem> = kotlinx.coroutines.flow.flow {
        var cursor: String? = null
        while (true) {
            val page = vastuJobResults(jobId, cursor).data
            page.items.forEach { emit(it) }
            val next = page.nextCursor ?: break
            check(next != cursor) { "The server returned the same results cursor twice" }
            cursor = next
        }
    }

    /** Stop a queued or running job. Items already charged stay charged; the rest are not run. Free. */
    suspend fun vastuJobCancel(jobId: String): VastuJobStatusResponse {
        val raw = client.post("$BASE/jobs/${requireVastuJobId(jobId)}/cancel", emptyMap())
        return VastuJobStatusResponse(raw.getBoolean("success"), VastuJobStatusData(raw.getJSONObject("data")), raw)
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
    suspend fun vastuPlanImportDxf(request: VastuPlanImportDxfRequest, idempotencyKey: String? = null): VastuTypedResponse<VastuPlanImportDxfData> =
        vastuOperation(VastuContracts.planImportDxf, request, idempotencyKey = idempotencyKey)

    suspend fun vastuPlanExportDxf(request: VastuPlanExportDxfRequest, idempotencyKey: String? = null): VastuTypedResponse<VastuPlanExportDxfData> =
        vastuOperation(VastuContracts.planExportDxf, request, idempotencyKey = idempotencyKey)

    suspend fun vastuPlanExportIfc(request: VastuPlanExportIfcRequest, idempotencyKey: String? = null): VastuTypedResponse<VastuPlanExportIfcData> =
        vastuOperation(VastuContracts.planExportIfc, request, idempotencyKey = idempotencyKey)
    suspend fun vastuPlanConvertUnits(request: VastuPlanConvertUnitsRequest, idempotencyKey: String? = null): VastuTypedResponse<VastuPlanConvertUnitsData> =
        vastuOperation(VastuContracts.planConvertUnits, request, idempotencyKey = idempotencyKey)
    suspend fun vastuPlanImportIfc(request: VastuPlanImportIfcRequest, idempotencyKey: String? = null): VastuTypedResponse<VastuPlanImportIfcData> =
        vastuOperation(VastuContracts.planImportIfc, request, idempotencyKey = idempotencyKey)

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

    private fun isGetOp(path: String): Boolean {
        if ((path == "rules/versions" || path.startsWith("reference/")) || path == "direction/declination") return true
        val job = VASTU_JOB_PATH.matchEntire(path) ?: return false
        return job.groupValues[2] != "/cancel"
    }

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

data class VastuPlanImportDxfRequest(
    val dxf: String,
    val fileName: String? = null,
    val contentType: String? = null,
    val trueNorthDeg: Double? = null,
    val unitsOverride: Int? = null,
    val layerRoles: Map<String, String>? = null
,
    val maxChargeUsd: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "dxf" to dxf,
        "fileName" to fileName,
        "contentType" to contentType,
        "trueNorthDeg" to trueNorthDeg,
        "unitsOverride" to unitsOverride,
        "layerRoles" to layerRoles
,
        "maxChargeUsd" to maxChargeUsd
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanExportDxfRequest(
    val plan: JSONObject,
    val analysis: JSONObject? = null,
    val zones: Int? = null,
    val unitsCode: Int? = null,
    val trueNorthDeg: Double? = null
,
    val maxChargeUsd: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val units: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,
        "units" to units,
        "plan" to plan,
        "analysis" to analysis,
        "zones" to zones,
        "unitsCode" to unitsCode,
        "trueNorthDeg" to trueNorthDeg
,
        "maxChargeUsd" to maxChargeUsd
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPlanImportIfcRequest(
    val ifc: String,
    val trueNorthDeg: Double? = null
,
    val maxChargeUsd: String? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null,
) : VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf(
        "propertyId" to propertyId,
        "tenantRef" to tenantRef,

        "ifc" to ifc,
        "trueNorthDeg" to trueNorthDeg
,
        "maxChargeUsd" to maxChargeUsd
    ).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}

data class VastuPortfolioSearchRequest(
    val city: String? = null,
    val tags: List<String>? = null,
    val minScore: Int? = null,
    val maxScore: Int? = null,
    val zoneDefects: List<String>? = null,
    val ruleset: String? = null,
    val inputSource: String? = null,
    val sort: String? = null,
    val limit: Int? = null,
    val cursor: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        city?.let { result["city"] = it }
        tags?.let { result["tags"] = it }
        minScore?.let { result["minScore"] = it }
        maxScore?.let { result["maxScore"] = it }
        zoneDefects?.let { result["zoneDefects"] = it }
        ruleset?.let { result["ruleset"] = it }
        inputSource?.let { result["inputSource"] = it }
        sort?.let { result["sort"] = it }
        limit?.let { result["limit"] = it }
        cursor?.let { result["cursor"] = it }
        return result
    }
}
data class VastuPortfolioCompareRequest(
    val propertyIds: List<String>
) : VastuRequest {
    override fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result["propertyIds"] = propertyIds
        return result
    }
}
data class VastuPortfolioAnalyticsRequest(
    val fromEpoch: Long? = null,
    val toEpoch: Long? = null,
    val tag: String? = null,
    val propertyId: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        fromEpoch?.let { result["fromEpoch"] = it }
        toEpoch?.let { result["toEpoch"] = it }
        tag?.let { result["tag"] = it }
        propertyId?.let { result["propertyId"] = it }
        return result
    }
}
data class VastuPortfolioUsageRequest(
    val fromEpoch: Long? = null,
    val toEpoch: Long? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        fromEpoch?.let { result["fromEpoch"] = it }
        toEpoch?.let { result["toEpoch"] = it }
        propertyId?.let { result["propertyId"] = it }
        tenantRef?.let { result["tenantRef"] = it }
        return result
    }
}
data class VastuPortfolioUsageExportRequest(
    val fromEpoch: Long? = null,
    val toEpoch: Long? = null,
    val propertyId: String? = null,
    val tenantRef: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        fromEpoch?.let { result["fromEpoch"] = it }
        toEpoch?.let { result["toEpoch"] = it }
        propertyId?.let { result["propertyId"] = it }
        tenantRef?.let { result["tenantRef"] = it }
        return result
    }
}
data class VastuPortfolioBudgetsSetRequest(
    val propertyId: String? = null,
    val tenantRef: String? = null,
    val capUsd: String?
) : VastuRequest {
    override fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        propertyId?.let { result["propertyId"] = it }
        tenantRef?.let { result["tenantRef"] = it }
        result["capUsd"] = capUsd ?: JSONObject.NULL
        return result
    }
}
data class VastuPortfolioBudgetsGetRequest(
    val propertyId: String? = null,
    val tenantRef: String? = null
) : VastuRequest {
    override fun toMap(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        propertyId?.let { result["propertyId"] = it }
        tenantRef?.let { result["tenantRef"] = it }
        return result
    }
}

data class VastuPlanExportIfcRequest(val plan: JSONObject, val outputUnits: String? = null, val maxChargeUsd: String? = null): VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf("plan" to plan,"outputUnits" to outputUnits,"maxChargeUsd" to maxChargeUsd).filterValues { it != null }.mapValues { encodeVastu(it.value) }
}
data class VastuPlanConvertUnitsRequest(val plan: JSONObject, val inputUnits: String, val outputUnits: String): VastuRequest {
    override fun toMap(): Map<String, Any?> = mapOf("plan" to plan,"inputUnits" to inputUnits,"outputUnits" to outputUnits).mapValues { encodeVastu(it.value) }
}
enum class VastuWorkspaceOperation(val path: String) { Properties("properties"), Jobs("jobs"), Get("get"), List("list"), Reset("reset"), Webhook("webhook"), Report("report") }
