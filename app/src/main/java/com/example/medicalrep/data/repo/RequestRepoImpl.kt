package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.RepRequestDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.RepRequest
import com.example.medicalrep.domain.repo.RequestRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RequestRepoImpl @Inject constructor(private val dao: RepRequestDao) : RequestRepo {

    override fun requestsOf(repId: String): Flow<List<RepRequest>> = dao.ofRep(repId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(request: RepRequest) {
        dao.upsert(request.toEntity())
    }

    override suspend fun decide(id: String, approved: Boolean, decidedAtMillis: Long) = dao.decide(id, approved, decidedAtMillis) > 0
}
