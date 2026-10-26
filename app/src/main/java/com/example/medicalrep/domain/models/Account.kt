package com.example.medicalrep.domain.models

data class Account(
    val id: String,
    val backendId: String? = null,
    val name: String,
    val entityType: String = "",
    val classCategory: String = "",
    val specialty: String = "",
    val city: String = "",
    val district: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val supervisorId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.NotSynced,
    val signPhoto: String? = null,
    val createdAt: Long = 0L,
)
