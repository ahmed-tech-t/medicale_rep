package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.UserDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.domain.models.UserRole
import com.example.medicalrep.domain.repo.UserRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepoImpl @Inject constructor(private val dao: UserDao) : UserRepo {

    override fun users(query: String) = pagedFlow({ dao.search(normalizeSearch(query)) }) { it.toDomain() }

    override fun byRole(role: UserRole): Flow<List<User>> = dao.getByRole(role).map { list -> list.map { it.toDomain() } }

    override fun repsOf(supervisorId: String): Flow<List<User>> = dao.getRepsOf(supervisorId).map { list -> list.map { it.toDomain() } }

    override suspend fun get(id: String): User? = dao.getById(id)?.toDomain()

    override suspend fun save(user: User) = dao.save(user.toEntity())

    override suspend fun delete(id: String) {
        dao.deleteById(id)
    }
}
