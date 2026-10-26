package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.SyncInfo
import kotlinx.coroutines.flow.Flow

interface SyncInfoRepo {
    fun all(): Flow<List<SyncInfo>>
    suspend fun get(key: String): SyncInfo?
    suspend fun set(key: String, lastUpdatedMillis: Long)
}
