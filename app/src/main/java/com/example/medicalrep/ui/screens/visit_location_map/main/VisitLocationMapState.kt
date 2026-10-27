package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.ui.common.travel.travelModeOf
import com.example.medicalrep.ui.screens.visitdetails.utils.distanceMeters
import com.example.medicalrep.ui.screens.visitdetails.utils.isAtAccountLocation

/** Where the rep did the visit ([visitLocation]), the account, and how long the rep took to get to the visit. */
@Immutable
data class VisitLocationMapState(
    val visitLocation: GeoPoint,
    val accountLocation: GeoPoint,
    val travelSeconds: Int = 0,
    /** The road route from the rep to the account; null while loading, offline or failed. */
    val route: RouteResult? = null,
) {
    val distanceMeters: Int
        get() = distanceMeters(
            visitLocation.latitude, visitLocation.longitude, accountLocation.latitude, accountLocation.longitude,
        )

    val isAtAccount: Boolean get() = isAtAccountLocation(distanceMeters)

    /** The road route when loaded, otherwise a straight line from the rep to the account; none when he was at the account. */
    val line: List<GeoPoint>
        get() = when {
            isAtAccount -> emptyList()
            route != null -> route.points
            else -> listOf(visitLocation, accountLocation)
        }

    val showTravel: Boolean get() = travelSeconds > 0 && !isAtAccount

    /** The road length when the route is loaded, otherwise the straight-line one, over the time the rep took. */
    val mode get() = travelModeOf(route?.distanceMeters ?: distanceMeters, travelSeconds)
}
