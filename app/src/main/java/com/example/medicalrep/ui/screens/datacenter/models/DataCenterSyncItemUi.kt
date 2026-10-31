package com.example.medicalrep.ui.screens.datacenter.models

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class DataCenterSyncItemUi(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val lastUpdatedMinutesAgo: Int? = null,
    val lastUpdatedTodayMillis: Long? = null,
    val status: DataCenterSyncStatus = DataCenterSyncStatus.Idle,
) {
    /** The full-update card is the primary action and is drawn highlighted. */
    val isHighlighted: Boolean get() = id == ID_FULL

    companion object {
        const val ID_FULL = "full_master"
        const val ID_DOCTORS = "doctors"
        const val ID_PRODUCTS = "products"
        const val ID_SAMPLES = "samples"
        const val ID_GIFTS = "gifts"
        const val ID_PLANNED = "planned_visits"
        const val ID_UNPLANNED = "unplanned_visits"
        const val ID_SETTINGS = "settings"
    }
}
