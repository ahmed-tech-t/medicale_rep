package com.example.medicalrep.domain.models

/** A road route: its line, length in meters and driving time in seconds. */
data class RouteResult(
    val points: List<GeoPoint>,
    val distanceMeters: Int,
    val durationSeconds: Int,
)
