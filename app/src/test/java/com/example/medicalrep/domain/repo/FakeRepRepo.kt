package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory reps for ViewModel tests. */
class FakeRepRepo(initial: List<Rep> = emptyList()) : RepRepo {
    val current = MutableStateFlow(initial)

    override fun reps(query: String): Flow<PagingData<Rep>> =
        current.map { list -> PagingData.from(list.filter { it.displayName.contains(query, ignoreCase = true) }) }

    override fun byRole(role: RepRole): Flow<List<Rep>> = current.map { list -> list.filter { it.role == role } }

    override fun repsOf(supervisorId: String): Flow<List<Rep>> = current.map { list -> list.filter { it.supervisorId == supervisorId } }

    override suspend fun get(id: String): Rep? = current.value.firstOrNull { it.id == id }

    override suspend fun save(rep: Rep) {
        current.value = current.value.filter { it.id != rep.id } + rep
    }

    override suspend fun delete(id: String) {
        current.value = current.value.filter { it.id != id }
    }
}
