package com.example.medicalrep.domain.models.map

import androidx.compose.runtime.Immutable

@Immutable
data class MapCameraTarget(
    val latitude: Double,
    val longitude: Double,
    val zoom: Float = 15f,
)
