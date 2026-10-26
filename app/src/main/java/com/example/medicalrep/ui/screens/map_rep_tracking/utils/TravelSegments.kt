package com.example.medicalrep.ui.screens.map_rep_tracking.utils

import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.screens.visitdetails.utils.distanceMeters

/** At most this average speed (km/h) between two visits means the rep walked. */
const val MAX_WALKING_SPEED_KMH = 6.0

private const val SECONDS_PER_HOUR = 3600.0
private const val METERS_PER_KM = 1000.0

enum class TravelMode { Car, Foot }

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
        val speedKmh = meters / METERS_PER_KM / (seconds / SECONDS_PER_HOUR)
        TravelSegment(
            position = track[(from + to) / 2],
            seconds = seconds,
            mode = if (speedKmh <= MAX_WALKING_SPEED_KMH) TravelMode.Foot else TravelMode.Car,
        )
    }
}
