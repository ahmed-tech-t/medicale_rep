package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.DraftVisit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory draft for ViewModel tests. */
class FakeDraftVisitRepo(initial: DraftVisit? = null) : DraftVisitRepo {
    val current = MutableStateFlow(initial)
    var discardCount = 0

    override fun draft(): Flow<DraftVisit?> = current

    override suspend fun save(draft: DraftVisit) {
        current.value = draft
    }

    override suspend fun discard() {
        discardCount++
        current.value = null
    }
}
