package com.example.medicalrep.ui.mapper

import com.example.medicalrep.R
import com.example.medicalrep.domain.models.AttachmentPill
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.StatMetric
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.models.AttachmentPillUiModel
import com.example.medicalrep.ui.models.ConnectionStatusUiModel
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.StatMetricUiModel
import com.example.medicalrep.ui.models.UserUi
import com.example.medicalrep.ui.models.VisitUi
import kotlin.collections.map

fun Visit.toUi(): VisitUi {

    return VisitUi(
        id = id.orEmpty(),
        date = date.orEmpty(),
        time = time.orEmpty(),
        city = city.orEmpty(),
        region = region.orEmpty(),
        address = address.orEmpty(),
        account = account.orEmpty(),
        placeName = placeName.orEmpty(),
        specialty = specialty.orEmpty(),
        shift = shift ?: Shift.Am,
        isDoubleVisit = visitMode == VisitMode.Double,
        type = type ?: VisitType.Planned,
        supervisorName = supervisorName.orEmpty(),
        classCategory = classCategory.orEmpty(),
        gifts = gifts?.map { it.toProductUi() }.orEmpty(),
        products = products?.map { it.toProductUi() }.orEmpty(),
        voiceNote = voiceNote.orEmpty(),
        syncStatus = syncStatus ?: SyncStatus.NotSynced,
        durationLabel = durationLabel.orEmpty(),
    )
}



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


fun User.toUi(): UserUi = UserUi(
    id = id,
    displayName = displayName,
    roleTitle = roleTitle,
    specialty = specialty,
    employeeCode = employeeCode,
    deviceId = deviceId,
    initials = initials,
)

private fun Product.toProductUi(): ProductUi {
    return ProductUi(
        id = id.orEmpty(),
        name = name.orEmpty(),
        quantity = quantity,
        kind = kind?:ProductType.Gift,
        description = description.orEmpty(),
        remaining = remaining
    )
}

