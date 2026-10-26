package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.flow.Flow

/** Visits stored on the phone. List `query` is the raw search box text; blank means everything. */
interface VisitRepo {
    fun visits(query: String = ""): Flow<PagingData<Visit>>
    fun visitsFiltered(type: VisitType?, syncStatus: SyncStatus?, repId: String?, fromMillis: Long, toMillis: Long): Flow<PagingData<Visit>>

    /** Plan requests that are Pending or Rejected. */
    fun pendingRequests(): Flow<PagingData<Visit>>
    fun pendingCount(): Flow<Int>

    /** Only planned visits can be started. */
    fun startableVisits(): Flow<PagingData<Visit>>

    suspend fun get(id: String): Visit?

    /** Saves the visit with its samples and gifts; a [Visit.voiceNote] is written to a file and its path stored. */
    suspend fun save(visit: Visit)
    suspend fun delete(id: String)

    suspend fun applyDecision(backendId: String, status: ApprovalStatus, note: String?, decidedAtMillis: Long): Boolean
    suspend fun dismissRejected(id: String): Boolean
    suspend fun cancelRequest(id: String): Boolean

    suspend fun notSynced(): List<Visit>
    suspend fun markSynced(id: String, backendId: String?)
}
