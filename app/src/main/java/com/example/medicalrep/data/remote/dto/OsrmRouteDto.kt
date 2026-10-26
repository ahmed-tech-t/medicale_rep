package com.example.medicalrep.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OsrmRouteDto(
    val code: String,
    val routes: List<OsrmRoute> = emptyList(),
) {
    @Serializable
    data class OsrmRoute(val geometry: Geometry, val distance: Double = 0.0, val duration: Double = 0.0)

    /** GeoJSON line: each coordinate is `[longitude, latitude]`. */
    @Serializable
    data class Geometry(val coordinates: List<List<Double>>)
}
