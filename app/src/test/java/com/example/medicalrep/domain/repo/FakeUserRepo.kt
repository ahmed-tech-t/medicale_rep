package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.domain.models.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory users for ViewModel tests. */
class FakeUserRepo(initial: List<User> = emptyList()) : UserRepo {
    val current = MutableStateFlow(initial)

    override fun users(query: String): Flow<PagingData<User>> =
        current.map { list -> PagingData.from(list.filter { it.displayName.contains(query, ignoreCase = true) }) }

    override fun byRole(role: UserRole): Flow<List<User>> = current.map { list -> list.filter { it.role == role } }

    override fun repsOf(supervisorId: String): Flow<List<User>> = current.map { list -> list.filter { it.supervisorId == supervisorId } }

    override suspend fun get(id: String): User? = current.value.firstOrNull { it.id == id }

    override suspend fun save(user: User) {
        current.value = current.value.filter { it.id != user.id } + user
    }

    override suspend fun delete(id: String) {
        current.value = current.value.filter { it.id != id }
    }
}
