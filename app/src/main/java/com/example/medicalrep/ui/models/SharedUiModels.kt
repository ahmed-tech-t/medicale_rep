package com.example.medicalrep.ui.models

import com.example.medicalrep.ui.components.widgets.SearchableItem
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType

/**
 * Non-null UI visit. Domain nulls are mapped to empty defaults in [com.example.medicalrep.ui.mapper.toUi].
 */
@Immutable
data class VisitUi(
    val id: String = "",
    val backendId: String = "",
    val date: String = "",
    val time: String = "",
    val city: String = "",
    val region: String = "",
    val address: String = "",
    val account: String = "",
    val placeName: String = "",
    val specialty: String = "",
    val shift: Shift = Shift.Am,
    val visitLatitude: Double = 0.0,
    val visitLongitude: Double = 0.0,
    /** How long the rep took to reach the visit (from the GPS track); 0 when unknown. */
    val travelSeconds: Int = 0,
    val accountLatitude: Double = 0.0,
    val accountLongitude: Double = 0.0,
    val isDoubleVisit: Boolean = false,
    val type: VisitType = VisitType.Unplanned,
    val supervisorName: String = "",
    val classCategory: String = "",
    val gifts: List<ProductUi> = emptyList(),
    val products: List<ProductUi> = emptyList(),
    val voiceNote: ByteArray? = null,
    val imageNote: Uri? = null,
    val noIssueFound: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.NotSynced,
    val durationLabel: String = "",
)

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
