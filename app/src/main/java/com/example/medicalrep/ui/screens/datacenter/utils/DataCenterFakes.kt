package com.example.medicalrep.ui.screens.datacenter.utils

import androidx.annotation.StringRes
import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.datacenter.main.DataCenterState
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_DOCTORS
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_FULL
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_GIFTS
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_PLANNED
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_PRODUCTS
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_SAMPLES
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_SETTINGS
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_UNPLANNED
import java.util.Calendar

/** Sample data shown until the real sync source exists. */
object DataCenterFakes {

    fun items(): List<DataCenterSyncItemUi> = listOf(
        item(ID_FULL, R.string.data_center_card_full_title, R.string.data_center_card_full_subtitle, minutesAgo = 18),
        item(ID_DOCTORS, R.string.data_center_card_doctors_title, R.string.data_center_card_doctors_subtitle, minutesAgo = 18),
        item(ID_PRODUCTS, R.string.data_center_card_products_title, R.string.data_center_card_products_subtitle, minutesAgo = 25),
        item(ID_SAMPLES, R.string.data_center_card_samples_title, R.string.data_center_card_samples_subtitle, minutesAgo = 25),
        item(ID_GIFTS, R.string.data_center_card_gifts_title, R.string.data_center_card_gifts_subtitle, minutesAgo = 25),
        item(ID_PLANNED, R.string.data_center_card_planned_title, R.string.data_center_card_planned_subtitle, todayMillis = today0830()),
        item(ID_UNPLANNED, R.string.data_center_card_unplanned_title, R.string.data_center_card_unplanned_subtitle, todayMillis = today0830()),
        item(ID_SETTINGS, R.string.data_center_card_settings_title, R.string.data_center_card_settings_subtitle, todayMillis = today0830()),
    )

    private fun today0830(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 30)
    }.timeInMillis

    private fun item(
        id: String,
        @StringRes title: Int,
        @StringRes subtitle: Int,
        minutesAgo: Int? = null,
        todayMillis: Long? = null,
    ) = DataCenterSyncItemUi(
        id = id,
        titleRes = title,
        subtitleRes = subtitle,
        lastUpdatedMinutesAgo = minutesAgo,
        lastUpdatedTodayMillis = todayMillis,
    )

    fun state(): DataCenterState = DataCenterState(items = items())
}
