package com.example.medicalrep.ui.screens.visitplanning.main

import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.ui.components.widgets.filter.FilterEvents
import com.example.medicalrep.ui.models.VisitUi

sealed interface VisitPlanningEvents {
    data object Back : VisitPlanningEvents
    data class SelectTab(val tab: VisitPlanningTab) : VisitPlanningEvents
    data class OnSearchChange(val text: String) : VisitPlanningEvents
    data object OpenFilter : VisitPlanningEvents
    data class OnFilterEvent(val event: FilterEvents) : VisitPlanningEvents
    data object ApplyFilter : VisitPlanningEvents
    data class SelectApproval(val approval: ApprovalStatus?) : VisitPlanningEvents
    data class ToggleDay(val visitId: String, val dayMillis: Long) : VisitPlanningEvents

    /** Request every visit for the days picked on its card. */
    data object ScheduleSelectedDays : VisitPlanningEvents
    data class PickAnotherTime(val visit: VisitUi) : VisitPlanningEvents
    data object DismissOverlay : VisitPlanningEvents

    /** The schedule sheet's button: request the rejected visit again on each chosen day. */
    data object RequestVisit : VisitPlanningEvents
}
