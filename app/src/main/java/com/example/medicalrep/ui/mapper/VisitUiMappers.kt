package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.utils.formatDurationLabel

/** The form fields of a visit being filled in, ready to be stored as a draft. */
fun VisitUi.toDomain() = Visit(
    id = id.ifBlank { null },
    accountId = accountId.ifBlank { null },
    city = city?.toDomain(),
    region = region?.toDomain(),
    address = address,
    account = account?.toDomain(),
    placeName = placeName,
    specialty = specialty?.toDomain(),
    shift = shift,
    type = type,
    visitMode = if (isDoubleVisit) VisitMode.Double else VisitMode.Single,
    supervisor = supervisor?.toDomain(),
    classCategory = classCategory?.toDomain(),
    gifts = gifts.map { it.toDomain() },
    products = products.map { it.toDomain() },
    doctors = doctors.map { it.toDomain() },
    voiceNote = voiceNote,
    imageNotes = imageNotes.map { it.toString() },
    noIssueFound = noIssueFound,
    visitLatitude = visitLatitude,
    visitLongitude = visitLongitude,
    accountLatitude = accountLatitude,
    accountLongitude = accountLongitude,
    travelSeconds = travelSeconds,
)

/**
 * The finished visit as it is stored: done by the rep, not uploaded yet, with how long it took.
 * [stored] is the row the visit already had (a planned visit); its server id and request times are kept, since the form has none.
 */
fun VisitUi.toCompletedVisit(
    stored: Visit?,
    repId: String?,
    supervisorId: String?,
    startedAtMillis: Long,
    completedAtMillis: Long,
) = toDomain().copy(
    backendId = stored?.backendId,
    requestedAtMillis = stored?.requestedAtMillis,
    decidedAtMillis = stored?.decidedAtMillis,
    repId = repId,
    supervisorId = supervisorId,
    dateTimeMillis = startedAtMillis,
    completedAtMillis = completedAtMillis,
    durationLabel = formatDurationLabel(completedAtMillis - startedAtMillis),
    status = VisitStatus.Completed,
    syncStatus = SyncStatus.NotSynced,
)

private fun ProductUi.toDomain() = Product(
    id = id,
    name = name,
    quantity = quantity,
    kind = kind,
    description = description,
    remaining = remaining,
    price = price,
    discountPercent = discountPercent,
)
