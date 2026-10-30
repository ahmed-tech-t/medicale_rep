package com.example.medicalrep.domain.models

data class Account(
    val id: String,
    val backendId: String? = null,
    val name: String,

    val accountType:  BasicModel? = null,
    val classCategory:  BasicModel? = null, //A B C
    val specialty: BasicModel? = null,
    val city: BasicModel? = null,
    val district: BasicModel? = null,

    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val supervisorId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.NotSynced,
    val signPhoto: String? = null,
    val createdAt: Long = 0L,
    val workingTime: WorkingTime = WorkingTime.Both,
)
