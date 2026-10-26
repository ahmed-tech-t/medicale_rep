package com.example.medicalrep.ui.screens.visitplanning.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.models.VisitUi

enum class VisitPlanningTab { Visits, Pending }

sealed interface VisitPlanningOverlay {
    data object None : VisitPlanningOverlay
    data object ScheduleVisit : VisitPlanningOverlay
    data object Filter : VisitPlanningOverlay
}

@Immutable
data class VisitPlanningState(
    val selectedTab: VisitPlanningTab = VisitPlanningTab.Visits,
    val pendingVisits: List<VisitUi> = emptyList(),
    val visits: List<VisitUi> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val overlay: VisitPlanningOverlay = VisitPlanningOverlay.None,
    val scheduleDateMillis: Long = 0L,
    val filter: VisitFilter = VisitFilter.All,
) {
    val visibleVisits: List<VisitUi>
        get() = when (selectedTab) {
            VisitPlanningTab.Visits -> visits
            VisitPlanningTab.Pending -> pendingVisits
        }.filter(filter.matches)

    val resultCount: Int get() = visibleVisits.size
    val hasSelection: Boolean get() = selectedIds.isNotEmpty()
    val isFiltered: Boolean get() = filter != VisitFilter.All
}
