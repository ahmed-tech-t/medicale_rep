package com.example.medicalrep.ui.screens.visitlog.main

sealed interface VisitLogEvents {
    data object Back : VisitLogEvents
    data class SelectTab(val tab: VisitLogTab) : VisitLogEvents
    data class StartVisit(val visitId: String) : VisitLogEvents
    data class OpenVisit(val visitId: String) : VisitLogEvents
    data object FilterClick : VisitLogEvents
}
