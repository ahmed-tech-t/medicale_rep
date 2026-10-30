package com.example.medicalrep.ui.screens.visitlog.main

import com.example.medicalrep.ui.components.widgets.filter.FilterEvents

sealed interface VisitLogEvents {
    data object Back : VisitLogEvents
    data class SelectTab(val tab: VisitLogTab) : VisitLogEvents
    data class StartVisit(val visitId: String) : VisitLogEvents
    data class OpenVisit(val visitId: String) : VisitLogEvents
    data object OpenFilter : VisitLogEvents
    data class CancelVisit(val visitId: String) : VisitLogEvents
    data object ConfirmCancel : VisitLogEvents
    data object DismissOverlay : VisitLogEvents
    data object ConfirmPending : VisitLogEvents
    data class OnFilterEvent(val event: FilterEvents) : VisitLogEvents
    data object ApplyFilter : VisitLogEvents
    data class OnSearchChange(val text: String) : VisitLogEvents
}
