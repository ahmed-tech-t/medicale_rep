package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.RepDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.repo.RepRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RepRepoImpl @Inject constructor(private val dao: RepDao) : RepRepo {

    override fun reps(query: String) = pagedFlow({ dao.search(normalizeSearch(query)) }) { it.toDomain() }

    override fun byRole(role: RepRole): Flow<List<Rep>> = dao.getByRole(role).map { list -> list.map { it.toDomain() } }

    override fun repsOf(supervisorId: String): Flow<List<Rep>> = dao.getRepsOf(supervisorId).map { list -> list.map { it.toDomain() } }

    override suspend fun get(id: String): Rep? = dao.getById(id)?.toDomain()

    override suspend fun save(rep: Rep) = dao.save(rep.toEntity())

    override suspend fun delete(id: String) {
        dao.deleteById(id)
    }
}
