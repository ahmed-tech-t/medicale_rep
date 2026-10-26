package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.flow.Flow

/** Visits stored on the phone. List `query` is the raw search box text; blank means everything. */
interface VisitRepo {
    fun visits(query: String = ""): Flow<PagingData<Visit>>
    fun visitsFiltered(type: VisitType?, syncStatus: SyncStatus?, repId: String?, fromMillis: Long, toMillis: Long): Flow<PagingData<Visit>>

    /** Planned visits still to do. Every filter is optional (null = all); they run in the database. */
    fun requiredVisits(shift: Shift? = null, classCategory: String? = null): Flow<PagingData<Visit>>
    fun requiredCount(shift: Shift? = null, classCategory: String? = null): Flow<Int>

    /** Finished visits, newest first; [doubleOnly] keeps only double visits. */
    fun doneVisits(
        shift: Shift? = null,
        doubleOnly: Boolean = false,
        syncStatus: SyncStatus? = null,
        classCategory: String? = null,
    ): Flow<PagingData<Visit>>
    fun doneCount(
        shift: Shift? = null,
        doubleOnly: Boolean = false,
        syncStatus: SyncStatus? = null,
        classCategory: String? = null,
    ): Flow<Int>

    /** Plan requests that are Pending or Rejected. */
    fun pendingRequests(shift: Shift? = null, classCategory: String? = null): Flow<PagingData<Visit>>

    /** How many requests [pendingRequests] lists for the same filters. */
    fun pendingRequestCount(shift: Shift? = null, classCategory: String? = null): Flow<Int>
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
