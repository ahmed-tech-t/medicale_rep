package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.map.MapCameraTarget

@Immutable
data class VisitLocationMapState(
    val userLocation: MapCameraTarget? = null,
    val visitLocation: MapCameraTarget,
)
