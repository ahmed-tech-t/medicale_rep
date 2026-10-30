package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
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

    /** Scheduled visits whose day passed without the visit. */
    fun missedVisits(shift: Shift? = null, classCategory: String? = null): Flow<PagingData<Visit>>
    fun missedCount(shift: Shift? = null, classCategory: String? = null): Flow<Int>

    /** Scheduled visits dated before [beforeMillis] (a day start) become Missed; returns how many. */
    suspend fun markMissed(beforeMillis: Long): Int

    /** Plan requests the admin has not answered yet. */
    fun pendingRequests(shift: Shift? = null, classCategory: String? = null): Flow<PagingData<Visit>>

    /** How many requests [pendingRequests] lists for the same filters. */
    fun pendingRequestCount(shift: Shift? = null, classCategory: String? = null): Flow<Int>
    fun pendingCount(): Flow<Int>

    /** Plan requests the admin refused. */
    fun rejectedRequests(shift: Shift? = null, classCategory: String? = null): Flow<PagingData<Visit>>
    fun rejectedRequestCount(shift: Shift? = null, classCategory: String? = null): Flow<Int>

    /** The visits still to do in the range (from included, to not included), soonest first, at most [limit]. */
    fun todayVisits(fromMillis: Long, toMillis: Long, limit: Int): Flow<List<Visit>>

    /** The visits the rep finished in the range (from included, to not included), in time order. */
    fun doneOnDay(repId: String, fromMillis: Long, toMillis: Long): Flow<List<Visit>>

    /** How many visits wait for upload. */
    fun notSyncedCount(): Flow<Int>

    /** Only planned visits can be started. */
    fun startableVisits(): Flow<PagingData<Visit>>

    suspend fun get(id: String): Visit?

    /**
     * Saves the visit with its samples and gifts; a [Visit.voiceNote] is written to a file and its path stored,
     * and each picked [Visit.imageNotes] is copied into the app's own files so it does not depend on the source Uri.
     */
    suspend fun save(visit: Visit)
    suspend fun delete(id: String)

    /** The admin approved ([approved]) or refused a request; false when no pending request has that [backendId]. */
    suspend fun applyDecision(backendId: String, approved: Boolean, decidedAtMillis: Long): Boolean
    suspend fun dismissRejected(id: String): Boolean
    suspend fun cancelRequest(id: String): Boolean

    suspend fun notSynced(): List<Visit>
    suspend fun markSynced(id: String, backendId: String?)
}
