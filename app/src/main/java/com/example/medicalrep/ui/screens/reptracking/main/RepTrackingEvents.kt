package com.example.medicalrep.ui.screens.reptracking.main

sealed interface RepTrackingEvents {
    data object Back : RepTrackingEvents
    data class SelectRep(val name: String) : RepTrackingEvents
    data class FromDateChanged(val millis: Long) : RepTrackingEvents
    data class ToDateChanged(val millis: Long) : RepTrackingEvents
    data object ViewRoute : RepTrackingEvents
    data class OpenVisit(val visitId: String) : RepTrackingEvents
}
