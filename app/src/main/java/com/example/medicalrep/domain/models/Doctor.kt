package com.example.medicalrep.domain.models

/** A doctor; one doctor can work at several accounts and an account can have several doctors. */
data class Doctor(
    val id: String,
    val backendId: String? = null,
    val name: String,
    val classCategory: BasicModel? = null,
    val specialty: BasicModel? = null,
    val syncStatus: SyncStatus = SyncStatus.NotSynced,
)
