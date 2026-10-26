package com.example.medicalrep.ui.screens.map_rep_tracking.utils

import com.example.medicalrep.ui.common.travel.TravelMode
import com.example.medicalrep.ui.common.travel.travelModeOf
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.screens.visitdetails.utils.distanceMeters

/** The trip between two consecutive visits: [position] is where its badge is drawn on the map. */
data class TravelSegment(
    val position: TrackUserMapMarker,
    val seconds: Int,
    val mode: TravelMode,
)

/**
 * Travel time and mode between each pair of consecutive [visits], from the GPS timestamps and the distance
 * along the [track]. Each visit is matched to its nearest track point.
 */
fun travelSegments(visits: List<TrackUserMapMarker>, track: List<TrackUserMapMarker>): List<TravelSegment> {
    if (visits.size < 2 || track.size < 2) return emptyList()
    val visitIndexes = visits.map { visit ->
        track.indices.minBy { distanceMeters(visit.latitude, visit.longitude, track[it].latitude, track[it].longitude) }
    }
    return visitIndexes.zipWithNext().mapNotNull { (from, to) ->
        val seconds = ((track[to].timeMillis - track[from].timeMillis) / 1000).toInt()
        if (to <= from || seconds <= 0) return@mapNotNull null
        val meters = (from until to).sumOf {
            distanceMeters(track[it].latitude, track[it].longitude, track[it + 1].latitude, track[it + 1].longitude)
        }
        TravelSegment(
            position = track[(from + to) / 2],
            seconds = seconds,
            mode = travelModeOf(meters, seconds),
        )
    }
}
