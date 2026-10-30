package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.RepRequest
import kotlinx.coroutines.flow.Flow

interface RequestRepo {
    /** The rep's requests, newest day first. */
    fun requestsOf(repId: String): Flow<List<RepRequest>>
    suspend fun save(request: RepRequest)
    /** Answers a pending request; false when it was answered already (or does not exist). */
    suspend fun decide(id: String, approved: Boolean, decidedAtMillis: Long): Boolean
}
