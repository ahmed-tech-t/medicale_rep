package com.example.medicalrep.ui.screens.visitplanning.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.ui.components.widgets.filter.FilterState
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.utils.PlanDay

enum class VisitPlanningTab {Visits , Pending}

sealed interface VisitPlanningOverlay {
    data object None : VisitPlanningOverlay

    /** The schedule sheet of a rejected request the rep picks another time for. */
    data object ScheduleVisit : VisitPlanningOverlay
    data object Filter : VisitPlanningOverlay
}

/** The list is paged and comes from the ViewModel's `visits` flow; this holds what surrounds it. */
@Immutable
data class VisitPlanningState(
    val selectedTab: VisitPlanningTab = VisitPlanningTab.Visits,
    /** What the Visits search box holds; the list uses it trimmed, after the rep stops typing. */
    val query: String = "",
    /** What narrows the Visits tab; it comes from the filter sheet's form [sheet] when the rep applies it. */
    val filter: VisitListFilter = VisitListFilter(),
    val sheet: FilterState = FilterState(),
    /** The admin's answer the Pending tab lists; null lists every request. */
    val approval: ApprovalStatus? = null,
    val resultCount: Int = 0,
    /** The days a visit card offers. */
    val days: List<PlanDay> = emptyList(),
    /** The days picked for each visit (by visit id); a visit with none picked is not in the map. */
    val selectedDays: Map<String, Set<Long>> = emptyMap(),
    val overlay: VisitPlanningOverlay = VisitPlanningOverlay.None,
    /** The rejected request the rep is choosing another time for; the schedule sheet requests it again. */
    val rescheduleId: String? = null,
    /** The visit shown in the schedule sheet's PlanVisitCard. */
    val rescheduleVisit: VisitUi? = null,
    /** Days already booked (Pending or Approved plan request exists), keyed by accountId. Booked tiles are greyed out. */
    val bookedDays: Map<String, Set<Long>> = emptyMap(),
) {
    val hasSelection: Boolean get() = selectedDays.isNotEmpty()
    val isFiltered: Boolean get() = filter.isActive
}
