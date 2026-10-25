package com.example.medicalrep.ui.screens.map_rep_tracking.utils

import com.example.medicalrep.ui.screens.map_rep_tracking.main.MapRepTrackingState
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.screens.reptracking.utils.RepTrackingFakes

/** Sample data shown until the rep's visits and GPS track are loaded. */
object MapRepTrackingFakes {

    /** 10-second interval tracking points: north, east, north, east, then a diagonal main street. */
    fun route(): List<TrackUserMapMarker> = listOf(
        30.0444 to 31.2357, 30.0448 to 31.2358, 30.0453 to 31.2359,
        30.0454 to 31.2365, 30.0455 to 31.2371, 30.0456 to 31.2378,
        30.0463 to 31.2379, 30.0470 to 31.2381, 30.0478 to 31.2383,
        30.0480 to 31.2392, 30.0482 to 31.2401, 30.0488 to 31.2408,
        30.0495 to 31.2414, 30.0502 to 31.2421, 30.0510 to 31.2428,
        30.0518 to 31.2435, 30.0526 to 31.2442, 30.0535 to 31.2446,
        30.0543 to 31.2448, 30.0550 to 31.2450,
    ).mapIndexed { index, (latitude, longitude) ->
        TrackUserMapMarker(id = index, latitude = latitude, longitude = longitude, visitId = "t$index")
    }

    fun state(): MapRepTrackingState {
        val route = route()
        // Visits sit on the route: first at the start, second at the end.
        val visits = RepTrackingFakes.visits().zip(listOf(route.first(), route.last())) { visit, point ->
            visit.copy(visitLatitude = point.latitude, visitLongitude = point.longitude)
        }
        return MapRepTrackingState(
            visits = visits,
            routePoints = route,
            selectedVisitId = visits.firstOrNull()?.id,
        )
    }
}
