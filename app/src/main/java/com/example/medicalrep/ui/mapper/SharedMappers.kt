package com.example.medicalrep.ui.mapper

import android.net.Uri
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.AttachmentPill
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.StatMetric
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.models.AttachmentPillUiModel
import com.example.medicalrep.ui.models.ConnectionStatusUiModel
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.RepUi
import com.example.medicalrep.ui.models.StatMetricUiModel
import com.example.medicalrep.ui.models.UserUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.utils.formatClockTime
import com.example.medicalrep.ui.utils.formatDate
import kotlin.collections.map

fun Visit.toUi(): VisitUi {

    return VisitUi(
        id = id.orEmpty(),
        backendId = backendId.orEmpty(),
        accountId = accountId.orEmpty(),
        date = date ?: dateTimeMillis?.let(::formatDate).orEmpty(),
        time = time ?: dateTimeMillis?.let(::formatClockTime).orEmpty(),
        city = city?.toUi(),
        region = region?.toUi(),
        address = address.orEmpty(),
        account = account?.toUi(),
        placeName = placeName.orEmpty(),
        specialty = specialty?.toUi(),
        shift = shift ?: Shift.Am,
        visitLatitude = visitLatitude,
        visitLongitude = visitLongitude,
        accountLatitude = accountLatitude,
        accountLongitude = accountLongitude,
        travelSeconds = travelSeconds,
        noIssueFound = noIssueFound,
        imageNotes = imageNotes.map(Uri::parse),
        isDoubleVisit = visitMode == VisitMode.Double,
        type = type ?: VisitType.Planned,
        supervisor = supervisor?.toUi(),
        classCategory = classCategory?.toUi(),
        gifts = gifts?.map { it.toProductUi() }.orEmpty(),
        products = products?.map { it.toProductUi() }.orEmpty(),
        voiceNote = voiceNote,
        syncStatus = syncStatus ?: SyncStatus.NotSynced,
        durationLabel = durationLabel.orEmpty(),
    )
}



fun Rep.toUi(): RepUi = RepUi(id = id, displayName = displayName, employeeCode = employeeCode)

fun StatMetric.toUi(): StatMetricUiModel = StatMetricUiModel(
    id = id,
    title = title,
    titleBlank = title.isBlank(),
    value = value.ifBlank { "0" },
    hint = hint?.takeIf { it.isNotBlank() },
)



fun AttachmentPill.toUi(): AttachmentPillUiModel = AttachmentPillUiModel(
    id = id,
    label = label,
    labelBlank = label.isBlank(),
    requiredMarkRes = if (isRequired) R.string.attachment_required else null,
)


fun Product.toProductUi(): ProductUi {
    return ProductUi(
        id = id.orEmpty(),
        name = name.orEmpty(),
        quantity = quantity,
        kind = kind?:ProductType.Gift,
        description = description.orEmpty(),
        remaining = remaining,
        price = price,
        discountPercent = discountPercent,
    )
}

