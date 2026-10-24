package com.example.medicalrep.ui.screens.datacenter.utils

import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.datacenter.main.DataCenterState
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_CLINICS
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_DOCTORS
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_FULL
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_PLANNED
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi.Companion.ID_UNPLANNED

/** Sample data shown until the real sync source exists. */
object DataCenterFakes {

    fun items(): List<DataCenterSyncItemUi> = listOf(
        DataCenterSyncItemUi(
            id = ID_FULL,
            titleRes = R.string.data_center_card_full_title,
            subtitleRes = R.string.data_center_card_full_subtitle,
            lastUpdatedLabel = "آخر تحديث: منذ 18 دقيقة",
        ),
        DataCenterSyncItemUi(
            id = ID_DOCTORS,
            titleRes = R.string.data_center_card_doctors_title,
            subtitleRes = R.string.data_center_card_doctors_subtitle,
            lastUpdatedLabel = "آخر تحديث: منذ 18 دقيقة",
        ),
        DataCenterSyncItemUi(
            id = ID_CLINICS,
            titleRes = R.string.data_center_card_clinics_title,
            subtitleRes = R.string.data_center_card_clinics_subtitle,
            lastUpdatedLabel = "آخر تحديث: منذ 25 دقيقة",
        ),
        DataCenterSyncItemUi(
            id = ID_PLANNED,
            titleRes = R.string.data_center_card_planned_title,
            subtitleRes = R.string.data_center_card_planned_subtitle,
            lastUpdatedLabel = "آخر تحديث: اليوم 08:30 ص",
        ),
        DataCenterSyncItemUi(
            id = ID_UNPLANNED,
            titleRes = R.string.data_center_card_unplanned_title,
            subtitleRes = R.string.data_center_card_unplanned_subtitle,
            lastUpdatedLabel = "آخر تحديث: اليوم 08:30 ص",
        ),
    )

    fun state(): DataCenterState = DataCenterState(items = items())
}
