package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.CatalogItemEntity
import com.example.medicalrep.data.local.entity.VisitDoctorEntity
import com.example.medicalrep.data.local.entity.VisitItemEntity
import com.example.medicalrep.data.local.entity.VisitEntity
import com.example.medicalrep.data.local.relation.VisitWithDetails
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.approval
import com.example.medicalrep.domain.models.execution
import com.example.medicalrep.domain.models.visitStatusOf
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType

/**
 * The account columns (city, address, place name, ...) and the supervisor name come from the linked rows.
 * Samples are the catalog items of type [ProductType.Sample], gifts the ones of type [ProductType.Gift].
 */
fun VisitWithDetails.toDomain(): Visit {
    val domainAccount = account?.toDomain()
    val quantities = lines.associate { it.itemId to it.quantity }
    fun linesOf(kind: ProductType) = items.filter { it.type == kind }.map { it.toLine(quantities.getValue(it.id)) }
    return Visit(
        id = visit.id,
        backendId = visit.backendId,
        accountId = visit.accountId,
        repId = visit.repId,
        supervisorId = visit.accompanyingRepId,
        dateTimeMillis = visit.dateTimeMillis.takeIf { it > 0 },
        city = domainAccount?.city,
        region = domainAccount?.district,
        address = domainAccount?.address,
        account = domainAccount?.accountType,
        placeName = domainAccount?.name,
        specialty = domainAccount?.specialty,
        classCategory = domainAccount?.classCategory,
        accountLatitude = domainAccount?.latitude ?: 0.0,
        accountLongitude = domainAccount?.longitude ?: 0.0,
        supervisor = supervisor?.let { BasicModel(it.id, it.displayName) },
        shift = visit.shift,
        type = visit.type,
        visitMode = visit.visitMode,
        gifts = linesOf(ProductType.Gift),
        products = linesOf(ProductType.Sample),
        visitLatitude = visit.visitLatitude,
        visitLongitude = visit.visitLongitude,
        travelSeconds = visit.travelSeconds,
        imageNotes = visit.imageNotes,
        doctors = doctors.map { it.toDomain() },
        voiceNotePath = visit.voiceNotePath,
        syncStatus = visit.syncStatus,
        durationLabel = visit.durationLabel,
        status = visitStatusOf(visit.approvalStatus, visit.executionStatus),
        cancelReason = visit.cancelReason,
        textNote = visit.textNote,
        requestId = visit.requestId,
        requestedAtMillis = visit.requestedAtMillis,
        decidedAtMillis = visit.decidedAtMillis,
        completedAtMillis = visit.completedAtMillis,
    )
}

/** The stock is not read with a visit, so a line can only be kept as it is: nothing more than its own quantity is left. */
private fun CatalogItemEntity.toLine(quantity: Int) = Product(
    id = id,
    name = name,
    quantity = quantity,
    kind = type,
    description = description,
    remaining = quantity,
)

/** The visit's own columns; its account text is added to `searchText` by the DAO when the visit is saved. */
fun Visit.toEntity() = VisitEntity(
    id = requireNotNull(id) { "A stored visit needs an id" },
    backendId = backendId,
    repId = repId,
    accompanyingRepId = supervisorId,
    accountId = accountId,
    dateTimeMillis = dateTimeMillis ?: 0L,
    shift = shift ?: Shift.Am,
    type = type ?: VisitType.Unplanned,
    visitMode = visitMode ?: VisitMode.Single,
    visitLatitude = visitLatitude,
    visitLongitude = visitLongitude,
    travelSeconds = travelSeconds,
    imageNotes = imageNotes,
    voiceNotePath = voiceNotePath,
    syncStatus = syncStatus ?: SyncStatus.NotSynced,
    durationLabel = durationLabel.orEmpty(),
    approvalStatus = requireNotNull(status) { "A stored visit needs a status" }.approval,
    executionStatus = status.execution,
    cancelReason = cancelReason,
    textNote = textNote,
    requestId = requestId,
    requestedAtMillis = requestedAtMillis,
    decidedAtMillis = decidedAtMillis,
    completedAtMillis = completedAtMillis,
)

/** The doctors seen in this visit. */
fun Visit.toDoctorLinks(): List<VisitDoctorEntity> =
    doctors.map { VisitDoctorEntity(requireNotNull(id) { "A stored visit needs an id" }, it.id) }

/** The samples and gifts given in this visit, with their quantities. */
fun Visit.toLines(): List<VisitItemEntity> =
    (gifts.orEmpty() + products.orEmpty()).map {
        VisitItemEntity(
            visitId = requireNotNull(id) { "A stored visit needs an id" },
            itemId = requireNotNull(it.id) { "A visit line needs an item id" },
            quantity = it.quantity,
        )
    }

