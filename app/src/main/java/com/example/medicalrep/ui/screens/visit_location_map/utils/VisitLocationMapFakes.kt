package com.example.medicalrep.ui.screens.visit_location_map.utils

import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.screens.visit_location_map.main.VisitLocationMapState

/** Sample data shown until the device's real location is used. */
object VisitLocationMapFakes {

    /** About 100 m from the sample visit, so both pins are visible and distinct. */
    val userLocation = MapCameraTarget(latitude = 30.0450, longitude = 31.2365)

    fun state(): VisitLocationMapState = VisitLocationMapState(
        userLocation = userLocation,
        visitLocation = MapCameraTarget(latitude = 30.0444, longitude = 31.2357),
    )
}
