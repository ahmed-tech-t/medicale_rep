package com.example.medicalrep.ui.screens.map_rep_tracking.main

import androidx.compose.runtime.Immutable
import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker

@Immutable
data class MapRepTrackingState(
    /** The visits the rep finished that day. */
    val visits: List<VisitUi> = emptyList(),
    /** The rep's GPS points of the day, in time order. */
    val routePoints: List<TrackUserMapMarker> = emptyList(),
    val selectedVisitId: String? = null,
    val route: Resource<RouteResult> = Resource.Loading(),
) {
    val selectedVisit: VisitUi? get() = visits.firstOrNull { it.id == selectedVisitId }

    /**
     * Where the rep's day started and ended. The road line is snapped to the streets, so its ends are used once it is
     * loaded and the icons sit on the line; until then the first and last GPS point. A track of fewer than two points has neither.
     */
    val trackStart: GeoPoint?
        get() = if (routePoints.size < 2) null else roadPoints.firstOrNull() ?: routePoints.first().toGeoPoint()
    val trackEnd: GeoPoint?
        get() = if (routePoints.size < 2) null else roadPoints.lastOrNull() ?: routePoints.last().toGeoPoint()

    private val roadPoints: List<GeoPoint> get() = route.data?.points.orEmpty()

    private fun TrackUserMapMarker.toGeoPoint() = GeoPoint(latitude, longitude)

    /** A pin at the place of each visit's account; an account with no location gets none. */
    val visitMarkers: List<TrackUserMapMarker>
        get() = visits
            .filter { it.accountLatitude != 0.0 || it.accountLongitude != 0.0 }
            .mapIndexed { index, visit -> TrackUserMapMarker(index, visit.accountLatitude, visit.accountLongitude, visit.id) }
}
