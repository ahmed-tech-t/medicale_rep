package com.example.medicalrep.ui.screens.map_rep_tracking.main

sealed interface MapRepTrackingEvents {
    data object Back : MapRepTrackingEvents
    data class MarkerClicked(val visitId: String) : MapRepTrackingEvents
    data class OpenVisitDetails(val visitId: String) : MapRepTrackingEvents
}
