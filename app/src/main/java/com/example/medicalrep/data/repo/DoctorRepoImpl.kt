package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.DoctorDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.domain.repo.DoctorRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DoctorRepoImpl @Inject constructor(private val dao: DoctorDao) : DoctorRepo {

    override fun doctorsOf(accountId: String): Flow<List<Doctor>> =
        dao.doctorsOf(accountId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(doctor: Doctor, accountIds: List<String>) = dao.save(doctor.toEntity(), accountIds)
}
