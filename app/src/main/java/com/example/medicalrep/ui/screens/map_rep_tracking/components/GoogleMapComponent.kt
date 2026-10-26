package com.example.medicalrep.ui.screens.map_rep_tracking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.screens.map_rep_tracking.utils.TravelSegment
import com.example.medicalrep.ui.screens.map_rep_tracking.utils.MapRepTrackingFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.utils.vectorToBitmapDescriptor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState



@Composable
fun GoogleMapComponent(
    modifier: Modifier = Modifier,
    visitss: List<TrackUserMapMarker>,
    repTrack: List<TrackUserMapMarker>,
    segments: List<TravelSegment> = emptyList(),
    roadRoute: List<RoutePoint> = emptyList(),
    onMarkerClick: (String) -> Unit,
    selectedVisitId: String? = null,
    mapType: MapType = MapType.NORMAL,
) {
    val cameraPositionState =  rememberCameraPositionState()
    var isMapReady by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val markerIcon = remember(context) {
        context.vectorToBitmapDescriptor(R.drawable.ic_map_marker)
    }
    val startMarkerIcon = remember(context) {
        context.vectorToBitmapDescriptor(R.drawable.ic_map_maeker_start)
    }
    val endMarkerIcon = remember(context) {
        context.vectorToBitmapDescriptor(R.drawable.ic_map_marker_end)
    }
    val haloIcon = remember(context) {
        context.vectorToBitmapDescriptor(R.drawable.ic_map_marker_halo)
    }

    LaunchedEffect(visitss) {
        val target = visitss.firstOrNull() ?: return@LaunchedEffect
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                LatLng(target.latitude, target.longitude),
                15f,
            ),
            durationMs = 500,
        )

    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isBuildingEnabled = false, mapType = mapType),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
        ),
        onMapLoaded = { isMapReady = true },
    ) {
        if (isMapReady) {
            // 1. Extract the coordinates into a list of LatLng
            val routePoints = repTrack.map { LatLng(it.latitude, it.longitude) }
            val dashedPattern = remember { listOf(Dash(30f), Gap(20f)) }
            val roadPoints = remember(roadRoute) { roadRoute.map { LatLng(it.latitude, it.longitude) } }
            // 2. Draw the real road route when loaded, otherwise the dashed line through the GPS points
            if (roadPoints.size > 1) {
                Polyline(
                    points = roadPoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 12f,
                    pattern = dashedPattern,
                )
            } else if (routePoints.size > 1) {
                Polyline(
                    points = routePoints,
                    color = MaterialTheme.colorScheme.primary, // Adjust road color
                    width = 12f,
                    pattern = dashedPattern,// Adjust road thickness
                    geodesic = true     // Optional: curves the line to match the earth's surface
                )
            }

            segments.forEach { segment ->
                key(segment.position.id) {
                    MarkerComposable(
                        segment,
                        state = rememberMarkerState(position = LatLng(segment.position.latitude, segment.position.longitude)),
                        zIndex = 0.5f,
                    ) {
                        TravelSegmentBadge(mode = segment.mode, seconds = segment.seconds)
                    }
                }
            }

            visitss.forEachIndexed { index, marker ->
                key(marker.id) {
                    val position = LatLng(marker.latitude, marker.longitude)
                    // Highlight only the selected marker: a halo drawn just below it.
                    if (marker.visitId == selectedVisitId) {
                        Marker(
                            state = rememberMarkerState(position = position),
                            icon = haloIcon,
                            zIndex = 0f,
                            onClick = {
                                onMarkerClick(marker.visitId)
                                true
                            }
                        )
                    }
                    Marker(
                        state = rememberMarkerState(position = position),
                        zIndex = 1f,
                        icon = if (index == 0) startMarkerIcon else if (index == visitss.lastIndex) endMarkerIcon else markerIcon,
                        onClick = {
                            onMarkerClick(marker.visitId)
                            true
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GoogleMapComponentPreview() {
    val route = MapRepTrackingFakes.route()
    MedicalRepTheme {
        GoogleMapComponent(
            visitss = listOf(route.first(), route[route.size / 2], route.last()),
            repTrack = route,
            onMarkerClick = {},
        )
    }
}
