package com.example.medicalrep.ui.screens.addaccount.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.utils.vectorToBitmapDescriptor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private const val INITIAL_ZOOM = 15f

/** Map where a tap drops (or moves) the single pin; the camera starts at [userLocation] and follows the pin. */
@Composable
fun PinPickerMap(
    userLocation: MapCameraTarget,
    pin: MapCameraTarget?,
    onPinPlaced: (latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(userLocation.latitude, userLocation.longitude), INITIAL_ZOOM)
    }
    val pinIcon = remember(context) { context.vectorToBitmapDescriptor(R.drawable.ic_map_marker) }

    LaunchedEffect(pin) {
        pin?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)), 400)
        }
    }

    GoogleMap(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(radii.large)),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
        ),
        onMapClick = { onPinPlaced(it.latitude, it.longitude) },
    ) {
        pin?.let {
            // New key per position so the marker follows each tap.
            key(it) {
                Marker(
                    state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                    icon = pinIcon,
                    // The icon is a circle, so center it on the tapped point (the default anchor is the bottom edge).
                    anchor = Offset(0.5f, 0.5f),
                )
            }
        }
    }
}
