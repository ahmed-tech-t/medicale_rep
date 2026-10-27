package com.example.medicalrep.ui.screens.visitplanning.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.VisitFilter

enum class VisitPlanningTab { Visits, Pending }

sealed interface VisitPlanningOverlay {
    data object None : VisitPlanningOverlay
    data object ScheduleVisit : VisitPlanningOverlay
    data object Filter : VisitPlanningOverlay
}

/** The list is paged and comes from the ViewModel's `visits` flow; this holds what surrounds it. */
@Immutable
data class VisitPlanningState(
    val selectedTab: VisitPlanningTab = VisitPlanningTab.Visits,
    val filter: VisitFilter = VisitFilter.All,
    val resultCount: Int = 0,
    val selectedIds: Set<String> = emptySet(),
    val overlay: VisitPlanningOverlay = VisitPlanningOverlay.None,
    val scheduleDateMillis: Long = 0L,
) {
    val hasSelection: Boolean get() = selectedIds.isNotEmpty()
    val isFiltered: Boolean get() = filter != VisitFilter.All
}
