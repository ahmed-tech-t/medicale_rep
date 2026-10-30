package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.flow.Flow

/** Visits stored on the phone. List `query` is the raw search box text; blank means everything. */
interface VisitRepo {
    fun visits(query: String = ""): Flow<PagingData<Visit>>
    fun visitsFiltered(type: VisitType?, syncStatus: SyncStatus?, repId: String?, fromMillis: Long, toMillis: Long): Flow<PagingData<Visit>>

    /** Planned visits still to do, by their date. Every part of [filter] is optional; it runs in the database. */
    fun requiredVisits(filter: VisitListFilter = VisitListFilter()): Flow<PagingData<Visit>>
    fun requiredCount(filter: VisitListFilter = VisitListFilter()): Flow<Int>

    /** Finished visits, newest first, by the day they were finished; [VisitListFilter.visitMode] keeps one kind of visit. */
    fun doneVisits(filter: VisitListFilter = VisitListFilter()): Flow<PagingData<Visit>>
    fun doneCount(filter: VisitListFilter = VisitListFilter()): Flow<Int>

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

    /** Cancels a visit that is still to do and keeps why; false when it is not (or no longer) to do. */
    suspend fun cancelVisit(id: String, reason: String): Boolean

    suspend fun notSynced(): List<Visit>
    suspend fun markSynced(id: String, backendId: String?)

    /** The upload of the visit was refused; it is listed as Failed and stays in [notSynced] to be sent again. */
    suspend fun markFailed(id: String)
}
