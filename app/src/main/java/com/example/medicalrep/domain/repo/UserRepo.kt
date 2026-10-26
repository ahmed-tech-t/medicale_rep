package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.User
import com.example.medicalrep.domain.models.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    fun users(query: String = ""): Flow<PagingData<User>>
    fun byRole(role: UserRole): Flow<List<User>>

    /** The reps of one supervisor. */
    fun repsOf(supervisorId: String): Flow<List<User>>
    suspend fun get(id: String): User?
    suspend fun save(user: User)
    suspend fun delete(id: String)
}
