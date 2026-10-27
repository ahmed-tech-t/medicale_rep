package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.CustodyEntity
import com.example.medicalrep.data.local.entity.VisitCustodyEntity
import com.example.medicalrep.data.local.entity.VisitEntity
import com.example.medicalrep.data.local.relation.VisitWithDetails
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType

/**
 * The account columns (city, address, place name, ...) and the supervisor name come from the linked rows.
 * Samples are the custody of kind [ProductType.Product], gifts the custody of kind [ProductType.Gift].
 */
fun VisitWithDetails.toDomain(): Visit {
    val quantities = lines.associate { it.custodyId to it.quantity }
    fun linesOf(kind: ProductType) = custody.filter { it.kind == kind }.map { it.toLine(quantities.getValue(it.id)) }
    return Visit(
        id = visit.id,
        backendId = visit.backendId,
        accountId = visit.accountId,
        repId = visit.repId,
        supervisorId = visit.supervisorId,
        dateTimeMillis = visit.dateTimeMillis.takeIf { it > 0 },
        city = account?.city,
        region = account?.district,
        address = account?.address,
        account = account?.entityType,
        placeName = account?.name,
        specialty = account?.specialty,
        classCategory = account?.classCategory,
        accountLatitude = account?.latitude ?: 0.0,
        accountLongitude = account?.longitude ?: 0.0,
        supervisorName = supervisor?.displayName,
        shift = visit.shift,
        type = visit.type,
        visitMode = visit.visitMode,
        gifts = linesOf(ProductType.Gift),
        products = linesOf(ProductType.Product),
        visitLatitude = visit.visitLatitude,
        visitLongitude = visit.visitLongitude,
        travelSeconds = visit.travelSeconds,
        noIssueFound = visit.noIssueFound,
        imageNote = visit.imageNote,
        voiceNotePath = visit.voiceNotePath,
        syncStatus = visit.syncStatus,
        durationLabel = visit.durationLabel,
        approvalStatus = visit.approvalStatus,
        requestedAtMillis = visit.requestedAtMillis,
        decidedAtMillis = visit.decidedAtMillis,
        decisionNote = visit.decisionNote,
        completedAtMillis = visit.completedAtMillis,
    )
}

private fun CustodyEntity.toLine(quantity: Int) = Product(
    id = id,
    name = name,
    quantity = quantity,
    kind = kind,
    description = description,
    remaining = received - dispatched,
)

/** The visit's own columns; its account text is added to `searchText` by the DAO when the visit is saved. */
fun Visit.toEntity() = VisitEntity(
    id = requireNotNull(id) { "A stored visit needs an id" },
    backendId = backendId,
    repId = repId,
    supervisorId = supervisorId,
    accountId = accountId,
    dateTimeMillis = dateTimeMillis ?: 0L,
    shift = shift ?: Shift.Am,
    type = type ?: VisitType.Unplanned,
    visitMode = visitMode ?: VisitMode.Single,
    visitLatitude = visitLatitude,
    visitLongitude = visitLongitude,
    travelSeconds = travelSeconds,
    noIssueFound = noIssueFound,
    imageNote = imageNote,
    voiceNotePath = voiceNotePath,
    syncStatus = syncStatus ?: SyncStatus.NotSynced,
    durationLabel = durationLabel.orEmpty(),
    approvalStatus = approvalStatus,
    requestedAtMillis = requestedAtMillis,
    decidedAtMillis = decidedAtMillis,
    decisionNote = decisionNote,
    completedAtMillis = completedAtMillis,
)

/** The samples and gifts given in this visit, with their quantities. */
fun Visit.toLines(): List<VisitCustodyEntity> =
    (gifts.orEmpty() + products.orEmpty()).map {
        VisitCustodyEntity(
            visitId = requireNotNull(id) { "A stored visit needs an id" },
            custodyId = requireNotNull(it.id) { "A visit line needs a custody id" },
            quantity = it.quantity,
        )
    }
