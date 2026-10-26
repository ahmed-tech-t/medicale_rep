package com.example.medicalrep.ui.screens.map_rep_tracking.models

data class TrackUserMapMarker(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val visitId: String,
    /** When the rep was at this point (epoch millis); not needed for visit markers. */
    val timeMillis: Long = 0L,
)