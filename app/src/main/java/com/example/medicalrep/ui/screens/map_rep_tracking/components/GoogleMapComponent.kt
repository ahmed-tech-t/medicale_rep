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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.ui.components.widgets.TravelSegmentBadge
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.screens.map_rep_tracking.utils.TravelSegment
import com.example.medicalrep.ui.screens.map_rep_tracking.utils.MapRepTrackingFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.utils.vectorToBitmapDescriptor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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



/** Space kept around the day's points when the camera fits them (the visit card covers the bottom). */
private const val BOUNDS_PADDING_PX = 200

@Composable
fun GoogleMapComponent(
    modifier: Modifier = Modifier,
    visitss: List<TrackUserMapMarker>,
    repTrack: List<TrackUserMapMarker>,
    trackStart: GeoPoint?,
    trackEnd: GeoPoint?,
    segments: List<TravelSegment> = emptyList(),
    roadRoute: List<GeoPoint> = emptyList(),
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
    // The icons are circles, so their centre (not the bottom edge, the default) sits on the visit's place.
    val circleAnchor = Offset(0.5f, 0.5f)

    // Show the whole day: every visit and the track from its start to its end. The camera can only fit bounds once the map is laid out.
    LaunchedEffect(isMapReady, visitss, repTrack) {
        if (!isMapReady) return@LaunchedEffect
        val points = (visitss + repTrack).map { LatLng(it.latitude, it.longitude) }
        val first = points.firstOrNull() ?: return@LaunchedEffect
        val update = if (points.size == 1) {
            CameraUpdateFactory.newLatLngZoom(first, 15f)
        } else {
            val bounds = LatLngBounds.Builder().apply { points.forEach { include(it) } }.build()
            CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING_PX)
        }
        cameraPositionState.animate(update = update, durationMs = 500)
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

            // The start and end of the rep's track, under the visit pins.
            trackStart?.let { TrackEndMarker(it, startMarkerIcon, circleAnchor) }
            trackEnd?.let { TrackEndMarker(it, endMarkerIcon, circleAnchor) }

            visitss.forEach { marker ->
                key(marker.id) {
                    val position = LatLng(marker.latitude, marker.longitude)
                    // Highlight only the selected marker: a halo drawn just below it.
                    if (marker.visitId == selectedVisitId) {
                        Marker(
                            state = rememberMarkerState(position = position),
                            icon = haloIcon,
                            anchor = circleAnchor,
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
                        anchor = circleAnchor,
                        icon = markerIcon,
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

/** The start or the end of the rep's track: a round icon centred on the point, under the visit pins. */
@Composable
private fun TrackEndMarker(point: GeoPoint, icon: BitmapDescriptor?, anchor: Offset) {
    Marker(
        state = rememberMarkerState(position = LatLng(point.latitude, point.longitude)),
        icon = icon,
        anchor = anchor,
        zIndex = 0.8f,
    )
}

@Preview(showBackground = true)
@Composable
private fun GoogleMapComponentPreview() {
    val route = MapRepTrackingFakes.route()
    MedicalRepTheme {
        GoogleMapComponent(
            visitss = listOf(route.first(), route[route.size / 2], route.last()),
            repTrack = route,
            trackStart = route.first().let { GeoPoint(it.latitude, it.longitude) },
            trackEnd = route.last().let { GeoPoint(it.latitude, it.longitude) },
            onMarkerClick = {},
        )
    }
}
