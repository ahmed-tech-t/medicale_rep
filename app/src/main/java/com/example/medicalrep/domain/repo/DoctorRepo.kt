package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.Doctor
import kotlinx.coroutines.flow.Flow

interface DoctorRepo {
    /** The doctors who work at the account, by name. */
    fun doctorsOf(accountId: String): Flow<List<Doctor>>

    /** Saves the doctor and links it to [accountIds] (links it already has are kept). */
    suspend fun save(doctor: Doctor, accountIds: List<String>)
}
