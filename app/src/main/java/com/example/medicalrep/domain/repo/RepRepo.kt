package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import kotlinx.coroutines.flow.Flow

interface RepRepo {
    fun reps(query: String = ""): Flow<PagingData<Rep>>
    fun byRole(role: RepRole): Flow<List<Rep>>

    /** The reps of one supervisor. */
    fun repsOf(supervisorId: String): Flow<List<Rep>>
    suspend fun get(id: String): Rep?
    suspend fun save(rep: Rep)
    suspend fun delete(id: String)
}
