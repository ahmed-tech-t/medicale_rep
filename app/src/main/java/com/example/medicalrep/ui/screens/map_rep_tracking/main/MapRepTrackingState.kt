package com.example.medicalrep.ui.screens.map_rep_tracking.main

import androidx.compose.runtime.Immutable
import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.domain.models.RouteResult
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker

@Immutable
data class MapRepTrackingState(
    val visits: List<VisitUi> = emptyList(),
    val routePoints: List<TrackUserMapMarker> = emptyList(),
    val selectedVisitId: String? = null,
    val route: Resource<RouteResult> = Resource.Loading(),
) {
    val selectedVisit: VisitUi? get() = visits.firstOrNull { it.id == selectedVisitId }
}
