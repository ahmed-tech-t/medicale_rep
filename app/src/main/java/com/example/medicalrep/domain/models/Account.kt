package com.example.medicalrep.domain.models

data class Account(
    val id: String,
    val backendId: String? = null,
    val name: String,

    val accountType:  BasicModel? = null,
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
    /** The doctors who work here; read from Room, never saved with the account. */
    val doctors: List<Doctor> = emptyList(),
) {
    /** The best class among its doctors (A before B before C); null when none has one. */
    val classCategory: BasicModel? get() = doctors.mapNotNull { it.classCategory }.minByOrNull { it.name }

    /** The specialty its doctors share; null when they differ or none has one. */
    val specialty: BasicModel? get() = doctors.mapNotNull { it.specialty }.distinct().singleOrNull()
}
