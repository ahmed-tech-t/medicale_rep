package com.example.medicalrep.ui.screens.map_rep_tracking.utils

import com.example.medicalrep.ui.screens.map_rep_tracking.main.MapRepTrackingState
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.screens.reptracking.utils.RepTrackingFakes

/** Sample data shown until the rep's visits and GPS track are loaded. */
object MapRepTrackingFakes {

    /** Rep trip across Cairo, west to east: Tahrir, Ramses, Abbasia, Nasr City, Heliopolis, New Cairo. */
    fun route(): List<TrackUserMapMarker> = listOf(
        30.0444 to 31.2357, 30.0520 to 31.2480, 30.0620 to 31.2650,
        30.0700 to 31.2800, 30.0730 to 31.3000, 30.0626 to 31.3400,
        30.0750 to 31.3300, 30.0910 to 31.3220, 30.0800 to 31.3600,
        30.0600 to 31.4000, 30.0300 to 31.4500, 30.0074 to 31.4913,
    ).mapIndexed { index, (latitude, longitude) ->
        TrackUserMapMarker(id = index, latitude = latitude, longitude = longitude, visitId = "t$index")
    }

    fun state(): MapRepTrackingState {
        val route = route()
        val baseVisits = RepTrackingFakes.visits()
        val visits = (baseVisits + listOf(
            baseVisits[0].copy(id = "rep-3", placeName = "د. أحمد فؤاد", time = "12:15 م"),
            baseVisits[1].copy(id = "rep-4", placeName = "د. منى السيد", time = "01:30 م"),
        ))
            // Visits sit on the route: Tahrir, Nasr City, Heliopolis, New Cairo.
            .zip(listOf(route[0], route[5], route[7], route.last())) { visit, point ->
                visit.copy(visitLatitude = point.latitude, visitLongitude = point.longitude)
            }
        return MapRepTrackingState(
            visits = visits,
            routePoints = route,
            selectedVisitId = visits.firstOrNull()?.id,
        )
    }
}
