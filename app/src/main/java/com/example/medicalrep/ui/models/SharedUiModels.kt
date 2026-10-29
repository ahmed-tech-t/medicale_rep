package com.example.medicalrep.ui.models

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.components.widgets.SearchableItem




/** Gift or product/sample line for visit custody UI. */
@Immutable
data class ProductUi(
    val id: String,
    override val name: String,
    val quantity: Int,
    val kind: ProductType,
    val remaining: Int,
    val description: String,
    val price: Int = 0,
    val discountPercent: Int = 0,
) : SearchableItem

@Immutable
data class StatMetricUiModel(
    val id: String,
    val title: String,
    val titleBlank: Boolean,
    val value: String,
    val hint: String?,
)

@Immutable
data class ConnectionStatusUiModel(
    @StringRes val labelRes: Int,
    val isOnline: Boolean,
    val pendingCount: Int?,
    @StringRes val titleRes: Int = R.string.offline_sync_title,
    @StringRes val subtitleRes: Int = R.string.offline_sync_subtitle,
)

@Immutable
data class AttachmentPillUiModel(
    val id: String,
    val label: String,
    val labelBlank: Boolean,
    @StringRes val fallbackLabelRes: Int = R.string.attachment_fallback,
    @StringRes val requiredMarkRes: Int?,
)

enum class StatusTone {
    Neutral,
    Success,
    Warning,
    Danger,
}

@Immutable
data class UserUi(
    val id: String? = null,
    val displayName: String = "",
    val roleTitle: String = "",
    val specialty: String = "",
    val employeeCode: String = "",
    val deviceId: String = "",
    val initials: String = "",
)
