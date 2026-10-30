package com.example.medicalrep.ui.screens.visitlog.utils

import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.ui.components.widgets.filter.ClassFilter
import com.example.medicalrep.ui.components.widgets.filter.FilterState
import com.example.medicalrep.ui.components.widgets.filter.SyncFilter
import com.example.medicalrep.ui.components.widgets.filter.VisitTypeFilter
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogTab
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.startOfNextDay

/** What the form asks the visit list for; the sync status and visit type only exist on the Done tab. */
fun FilterState.toVisitListFilter(tab: VisitLogTab): VisitListFilter {
    val isDone = tab == VisitLogTab.Done
    return VisitListFilter(
        classCategory = when (selectedClass) {
            ClassFilter.A -> "A"
            ClassFilter.B -> "B"
            ClassFilter.C -> "C"
            ClassFilter.All -> null
        },
        cityId = selectedCity?.id,
        districtId = selectedRegion?.id,
        specialty = selectedSpecialty?.name,
        fromMillis = if (selectedStartDate.isBlank()) null else startOfDay(startDateMillis),
        toMillis = if (selectedEndDate.isBlank()) null else startOfNextDay(endDateMillis),
        syncStatus = if (!isDone) null else when (syncFilter) {
            SyncFilter.All -> null
            SyncFilter.Synced -> SyncStatus.Synced
            SyncFilter.Waiting -> SyncStatus.NotSynced
            SyncFilter.Failed -> SyncStatus.Failed
        },
        visitMode = if (!isDone) null else when (visitTypeFilter) {
            VisitTypeFilter.All -> null
            VisitTypeFilter.Single -> VisitMode.Single
            VisitTypeFilter.Double -> VisitMode.Double
        },
    )
}
