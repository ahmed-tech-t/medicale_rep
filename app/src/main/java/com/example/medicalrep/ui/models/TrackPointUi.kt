package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class TrackPointUi(
    val id: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val date: String = "",
    val time: String = "",
    val isSynced: Boolean = false,
)
