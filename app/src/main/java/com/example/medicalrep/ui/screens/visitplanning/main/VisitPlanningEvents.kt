package com.example.medicalrep.ui.screens.visitplanning.main

import com.example.medicalrep.ui.models.VisitFilter

sealed interface VisitPlanningEvents {
    data object Back : VisitPlanningEvents
    data class SelectTab(val tab: VisitPlanningTab) : VisitPlanningEvents
    data object OpenFilter : VisitPlanningEvents

    data class SelectFilter(val filter: VisitFilter) : VisitPlanningEvents
    data class ToggleSelect(val visitId: String) : VisitPlanningEvents
    data object OpenSchedule : VisitPlanningEvents
    data object DismissOverlay : VisitPlanningEvents
    data class DateChanged(val millis: Long) : VisitPlanningEvents
    data object RequestVisit : VisitPlanningEvents
}
