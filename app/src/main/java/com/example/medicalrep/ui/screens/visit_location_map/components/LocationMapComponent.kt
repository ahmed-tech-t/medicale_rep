package com.example.medicalrep.ui.screens.visit_location_map.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.common.location.hasLocationPermission
import com.example.medicalrep.ui.utils.vectorToBitmapDescriptor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private const val BOUNDS_PADDING_PX = 160
private const val SINGLE_POINT_ZOOM = 16f

/** Map with the visit pin and, once known, the user's pin; the camera fits what is shown once the map is loaded. */
@Composable
fun LocationMapComponent(
    userLocation: MapCameraTarget?,
    visitLocation: MapCameraTarget,
    modifier: Modifier = Modifier,
    routePoints: List<RoutePoint> = emptyList(),
    showMyLocationDot: Boolean = false,
    destinationTitle: String = stringResource(R.string.visit_details_visit_location),
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var isMapReady by remember { mutableStateOf(false) }
    val userPosition = remember(userLocation) { userLocation?.let { LatLng(it.latitude, it.longitude) } }
    val visitPosition = remember(visitLocation) { LatLng(visitLocation.latitude, visitLocation.longitude) }
    val routeLine = remember(routePoints) { routePoints.map { LatLng(it.latitude, it.longitude) } }
    val userIcon = remember { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) }
    val visitIcon = remember(context) { context.vectorToBitmapDescriptor(R.drawable.ic_map_marker) }

    LaunchedEffect(isMapReady, userPosition, visitPosition) {
        if (!isMapReady) return@LaunchedEffect
        val update = if (userPosition == null || userPosition == visitPosition) {
            CameraUpdateFactory.newLatLngZoom(visitPosition, SINGLE_POINT_ZOOM)
        } else {
            val bounds = LatLngBounds.builder().include(userPosition).include(visitPosition).build()
            CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING_PX)
        }
        cameraPositionState.animate(update, durationMs = 500)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        // Google's own blue dot instead of a pin for the rep; it needs the location permission.
        properties = MapProperties(isMyLocationEnabled = showMyLocationDot && context.hasLocationPermission()),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
        ),
        onMapLoaded = { isMapReady = true },
    ) {
        if (userPosition != null && !showMyLocationDot) {
            Marker(
                state = rememberMarkerState(position = userPosition),
                title = stringResource(R.string.map_my_location),
                icon = userIcon,
            )
        }
        Marker(
            state = rememberMarkerState(position = visitPosition),
            title = destinationTitle,
            icon = visitIcon,
        )
        if (routeLine.size > 1) {
            Polyline(
                points = routeLine,
                color = MaterialTheme.colorScheme.primary,
                width = 12f,
                pattern = listOf(Dash(30f), Gap(20f)),
            )
        }
    }
}
