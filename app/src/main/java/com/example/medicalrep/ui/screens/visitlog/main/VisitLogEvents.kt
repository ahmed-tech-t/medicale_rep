package com.example.medicalrep.ui.screens.visitlog.main

import com.example.medicalrep.ui.models.VisitFilter

sealed interface VisitLogEvents {
    data object Back : VisitLogEvents
    data class SelectTab(val tab: VisitLogTab) : VisitLogEvents
    data class StartVisit(val visitId: String) : VisitLogEvents
    data class OpenVisit(val visitId: String) : VisitLogEvents
    data object OpenFilter : VisitLogEvents
    data object CloseFilter : VisitLogEvents
    data class SelectFilter(val filter: VisitFilter) : VisitLogEvents
}
