package com.example.medicalrep.ui.screens.reptracking.main

sealed interface RepTrackingEvents {
    data object Back : RepTrackingEvents
    data class SelectRep(val repId: String) : RepTrackingEvents
    data class DateChanged(val millis: Long) : RepTrackingEvents
    data object ViewRoute : RepTrackingEvents
    data class OpenVisit(val visitId: String) : RepTrackingEvents
}
