package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.Doctor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory doctors for ViewModel tests: the doctors of each account, set with [setDoctors]. */
class FakeDoctorRepo : DoctorRepo {
    private val byAccount = MutableStateFlow<Map<String, List<Doctor>>>(emptyMap())

    fun setDoctors(accountId: String, doctors: List<Doctor>) {
        byAccount.value = byAccount.value + (accountId to doctors)
    }

    override fun specialties(): Flow<List<BasicModel>> = byAccount.map { map ->
        map.values.flatten().mapNotNull { it.specialty?.name }.filter { it.isNotBlank() }.distinct().sorted().map { BasicModel(it, it) }
    }

    override fun doctorsOf(accountId: String): Flow<List<Doctor>> = byAccount.map { it[accountId].orEmpty() }

    override suspend fun save(doctor: Doctor, accountIds: List<String>) {
        accountIds.forEach { id -> setDoctors(id, byAccount.value[id].orEmpty().filter { it.id != doctor.id } + doctor) }
    }
}
