package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory visits for ViewModel tests; the list queries record the filters they were asked for. */
class FakeVisitRepo(initial: List<Visit> = emptyList()) : VisitRepo {
    val current = MutableStateFlow(initial)

    /** The (shift, class) of every `requiredVisits` call, in order. */
    val requiredRequests = mutableListOf<Pair<Shift?, String?>>()

    /** The (shift, doubleOnly, syncStatus) of every `doneVisits` call, in order. */
    val doneRequests = mutableListOf<Triple<Shift?, Boolean, SyncStatus?>>()

    /** The class of every `doneVisits` call, in order. */
    val doneClassRequests = mutableListOf<String?>()

    /** The (shift, class) of every `pendingRequests` call, in order. */
    val pendingRequestsAsked = mutableListOf<Pair<Shift?, String?>>()

    private fun required(shift: Shift?, classCategory: String?) = current.map { list ->
        list.filter {
            it.type == VisitType.Planned && it.completedAtMillis == null &&
                (shift == null || it.shift == shift) && (classCategory == null || it.classCategory == classCategory)
        }
    }

    private fun done(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?) = current.map { list ->
        list.filter {
            it.completedAtMillis != null && (shift == null || it.shift == shift) &&
                (!doubleOnly || it.visitMode == VisitMode.Double) && (syncStatus == null || it.syncStatus == syncStatus) &&
                (classCategory == null || it.classCategory == classCategory)
        }
    }

    private fun pending(shift: Shift?, classCategory: String?) = current.map { list ->
        list.filter {
            it.type == VisitType.Planning && it.approvalStatus != ApprovalStatus.Approved &&
                (shift == null || it.shift == shift) && (classCategory == null || it.classCategory == classCategory)
        }
    }

    override fun requiredVisits(shift: Shift?, classCategory: String?): Flow<PagingData<Visit>> {
        requiredRequests += shift to classCategory
        return required(shift, classCategory).map { PagingData.from(it) }
    }

    override fun requiredCount(shift: Shift?, classCategory: String?): Flow<Int> = required(shift, classCategory).map { it.size }

    override fun doneVisits(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?): Flow<PagingData<Visit>> {
        doneRequests += Triple(shift, doubleOnly, syncStatus)
        doneClassRequests += classCategory
        return done(shift, doubleOnly, syncStatus, classCategory).map { PagingData.from(it) }
    }

    override fun doneCount(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?): Flow<Int> =
        done(shift, doubleOnly, syncStatus, classCategory).map { it.size }

    override fun visits(query: String): Flow<PagingData<Visit>> = current.map { PagingData.from(it) }

    override fun visitsFiltered(type: VisitType?, syncStatus: SyncStatus?, repId: String?, fromMillis: Long, toMillis: Long): Flow<PagingData<Visit>> =
        current.map { PagingData.from(it) }

    override fun pendingRequests(shift: Shift?, classCategory: String?): Flow<PagingData<Visit>> {
        pendingRequestsAsked += shift to classCategory
        return pending(shift, classCategory).map { PagingData.from(it) }
    }

    override fun pendingRequestCount(shift: Shift?, classCategory: String?): Flow<Int> = pending(shift, classCategory).map { it.size }

    override fun pendingCount(): Flow<Int> = current.map { list -> list.count { it.type == VisitType.Planning && it.approvalStatus == ApprovalStatus.Pending } }

    override fun startableVisits(): Flow<PagingData<Visit>> = required(null, null).map { PagingData.from(it) }

    override suspend fun get(id: String): Visit? = current.value.firstOrNull { it.id == id }

    override suspend fun save(visit: Visit) {
        current.value = current.value.filter { it.id != visit.id } + visit
    }

    override suspend fun delete(id: String) {
        current.value = current.value.filter { it.id != id }
    }

    override suspend fun applyDecision(backendId: String, status: ApprovalStatus, note: String?, decidedAtMillis: Long): Boolean = false
    override suspend fun dismissRejected(id: String): Boolean = false
    override suspend fun cancelRequest(id: String): Boolean = false
    override suspend fun notSynced(): List<Visit> = current.value.filter { it.syncStatus == SyncStatus.NotSynced }
    override suspend fun markSynced(id: String, backendId: String?) = Unit
}
