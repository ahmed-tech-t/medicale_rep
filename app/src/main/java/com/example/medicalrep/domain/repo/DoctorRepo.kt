package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.Doctor
import kotlinx.coroutines.flow.Flow

interface DoctorRepo {
    /** The doctors who work at the account, by name. */
    fun doctorsOf(accountId: String): Flow<List<Doctor>>

    /** Every specialty a doctor has, once each, by name; the id is the name. */
    fun specialties(): Flow<List<BasicModel>>

    /** Saves the doctor and links it to [accountIds] (links it already has are kept). */
    suspend fun save(doctor: Doctor, accountIds: List<String>)
}
