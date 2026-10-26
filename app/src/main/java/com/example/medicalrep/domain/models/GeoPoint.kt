package com.example.medicalrep.domain.models

import androidx.compose.runtime.Immutable

/** A point on the earth: where the user is, an account, a route stop or a map pin. */
@Immutable
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
)
