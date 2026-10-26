package com.example.medicalrep.domain.models

data class TrackPoint(
    val id: Long = 0L,
    val backendId: String? = null,
    val repId: String,
    val visitId: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timeMillis: Long,
    val syncStatus: SyncStatus = SyncStatus.NotSynced,
)
