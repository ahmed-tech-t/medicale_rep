package com.example.medicalrep.ui.screens.visit_location_map.utils

import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.ui.screens.visit_location_map.main.VisitLocationMapState

/** Sample data for previews: the rep was about 11 km from the account and took 25 minutes to get there. */
object VisitLocationMapFakes {

    fun state(): VisitLocationMapState = VisitLocationMapState(
        visitLocation = GeoPoint(latitude = 30.0444, longitude = 31.2357),
        accountLocation = GeoPoint(latitude = 30.1444, longitude = 31.2357),
        travelSeconds = 25 * 60,
    )
}
