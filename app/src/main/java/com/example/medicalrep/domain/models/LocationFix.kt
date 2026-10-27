package com.example.medicalrep.domain.models

/** One GPS reading: where the phone is, and how many metres it may be off (smaller is better). */
data class LocationFix(
    val point: GeoPoint,
    val accuracyMeters: Float,
)
