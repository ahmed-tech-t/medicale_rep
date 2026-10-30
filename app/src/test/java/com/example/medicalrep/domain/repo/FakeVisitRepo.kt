package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory visits for ViewModel tests; the list queries record the filters they were asked for. */
class FakeVisitRepo(initial: List<Visit> = emptyList()) : VisitRepo {
    val current = MutableStateFlow(initial)

    /** The filter of every `requiredVisits` call, in order. */
    val requiredRequests = mutableListOf<VisitListFilter>()

    /** The filter of every `doneVisits` call, in order. */
    val doneRequests = mutableListOf<VisitListFilter>()

    /** The (shift, class) of every `missedVisits` call, in order. */
    val missedRequests = mutableListOf<Pair<Shift?, String?>>()

    /** The (shift, class) of every `pendingRequests` call, in order. */
    val pendingRequestsAsked = mutableListOf<Pair<Shift?, String?>>()

    /** The (shift, class) of every `rejectedRequests` call, in order. */
    val rejectedRequestsAsked = mutableListOf<Pair<Shift?, String?>>()

    /** The (id, reason) of every `cancelVisit` call, in order. */
    val cancelCalls = mutableListOf<Pair<String, String>>()

    /** The day start of every `markMissed` call, in order. */
    val markMissedCalls = mutableListOf<Long>()

    /** Makes `save` fail, like a refused insert. */
    var failOnSave = false

    /** While set, `save` waits for it: lets a test tap again while a save is running. */
    var saveGate: CompletableDeferred<Unit>? = null

    private fun withStatus(status: VisitStatus, shift: Shift?, classCategory: String?) = current.map { list ->
        list.filter {
            it.status == status && (shift == null || it.shift == shift) && (classCategory == null || it.classCategory?.name == classCategory)
        }
    }

    /** Applies the shift, class, double and sync parts of [filter]; city, district, specialty and period are only recorded. */
    private fun done(filter: VisitListFilter) =
        withStatus(VisitStatus.Completed, filter.shift, filter.classCategory).map { list ->
            list.filter {
                (filter.visitMode == null || it.visitMode == filter.visitMode) &&
                    (filter.syncStatus == null || it.syncStatus == filter.syncStatus)
            }
        }

    override fun requiredVisits(filter: VisitListFilter): Flow<PagingData<Visit>> {
        requiredRequests += filter
        return withStatus(VisitStatus.Scheduled, filter.shift, filter.classCategory).map { PagingData.from(it) }
    }

    override fun requiredCount(filter: VisitListFilter): Flow<Int> =
        withStatus(VisitStatus.Scheduled, filter.shift, filter.classCategory).map { it.size }

    override fun doneVisits(filter: VisitListFilter): Flow<PagingData<Visit>> {
        doneRequests += filter
        return done(filter).map { PagingData.from(it) }
    }

    override fun doneCount(filter: VisitListFilter): Flow<Int> = done(filter).map { it.size }

    override fun missedVisits(shift: Shift?, classCategory: String?): Flow<PagingData<Visit>> {
        missedRequests += shift to classCategory
        return withStatus(VisitStatus.Missed, shift, classCategory).map { PagingData.from(it) }
    }

    override fun missedCount(shift: Shift?, classCategory: String?): Flow<Int> =
        withStatus(VisitStatus.Missed, shift, classCategory).map { it.size }

    override suspend fun markMissed(beforeMillis: Long): Int {
        markMissedCalls += beforeMillis
        val late = current.value.filter { it.status == VisitStatus.Scheduled && (it.dateTimeMillis ?: 0L) in 1 until beforeMillis }
        current.value = current.value.map { if (it in late) it.copy(status = VisitStatus.Missed) else it }
        return late.size
    }

    /** The (from, near, limit) of every `scheduledFrom` call, in order. The order by distance is Room's job; this one is time order. */
    val scheduledRequests = mutableListOf<Triple<Long, GeoPoint?, Int>>()

    override fun scheduledFrom(fromMillis: Long, near: GeoPoint?, limit: Int): Flow<List<Visit>> {
        scheduledRequests += Triple(fromMillis, near, limit)
        return current.map { list ->
            list.filter { it.status == VisitStatus.Scheduled && (it.dateTimeMillis ?: 0L) >= fromMillis }.sortedBy { it.dateTimeMillis }.take(limit)
        }
    }

    /** Every `doneOnDay` call, in order. */
    val doneOnDayRequests = mutableListOf<RepDayRequest>()

    override fun doneOnDay(repId: String, fromMillis: Long, toMillis: Long): Flow<List<Visit>> {
        doneOnDayRequests += RepDayRequest(repId, fromMillis, toMillis)
        return current.map { list ->
            list.filter { it.status == VisitStatus.Completed && it.repId == repId && (it.dateTimeMillis ?: 0L) in fromMillis until toMillis }
                .sortedBy { it.dateTimeMillis }
        }
    }

    override fun visits(query: String): Flow<PagingData<Visit>> = current.map { PagingData.from(it) }

    override fun visitsFiltered(type: VisitType?, syncStatus: SyncStatus?, repId: String?, fromMillis: Long, toMillis: Long): Flow<PagingData<Visit>> =
        current.map { PagingData.from(it) }

    override fun pendingRequests(shift: Shift?, classCategory: String?): Flow<PagingData<Visit>> {
        pendingRequestsAsked += shift to classCategory
        return withStatus(VisitStatus.PendingApproval, shift, classCategory).map { PagingData.from(it) }
    }

    override fun pendingRequestCount(shift: Shift?, classCategory: String?): Flow<Int> =
        withStatus(VisitStatus.PendingApproval, shift, classCategory).map { it.size }

    override fun pendingCount(): Flow<Int> = current.map { list -> list.count { it.status == VisitStatus.PendingApproval } }

    override fun rejectedRequests(shift: Shift?, classCategory: String?): Flow<PagingData<Visit>> {
        rejectedRequestsAsked += shift to classCategory
        return withStatus(VisitStatus.Rejected, shift, classCategory).map { PagingData.from(it) }
    }

    override fun rejectedRequestCount(shift: Shift?, classCategory: String?): Flow<Int> =
        withStatus(VisitStatus.Rejected, shift, classCategory).map { it.size }

    override fun startableVisits(): Flow<PagingData<Visit>> = withStatus(VisitStatus.Scheduled, null, null).map { PagingData.from(it) }

    override suspend fun get(id: String): Visit? = current.value.firstOrNull { it.id == id }

    override suspend fun save(visit: Visit) {
        saveGate?.await()
        check(!failOnSave) { "insert refused" }
        current.value = current.value.filter { it.id != visit.id } + visit
    }

    override suspend fun delete(id: String) {
        current.value = current.value.filter { it.id != id }
    }

    override suspend fun applyDecision(backendId: String, approved: Boolean, decidedAtMillis: Long): Boolean = false
    override suspend fun dismissRejected(id: String): Boolean = false
    override suspend fun cancelRequest(id: String): Boolean = false

    override suspend fun cancelVisit(id: String, reason: String): Boolean {
        cancelCalls += id to reason
        val visit = current.value.firstOrNull { it.id == id && it.status == VisitStatus.Scheduled } ?: return false
        current.value = current.value.map { if (it === visit) it.copy(status = VisitStatus.Missed, cancelReason = reason) else it }
        return true
    }
    override suspend fun notSynced(): List<Visit> = current.value.filter { it.syncStatus != SyncStatus.Synced }
    override suspend fun markSynced(id: String, backendId: String?) = Unit

    override suspend fun markFailed(id: String) {
        current.value = current.value.map { if (it.id == id) it.copy(syncStatus = SyncStatus.Failed) else it }
    }
}
