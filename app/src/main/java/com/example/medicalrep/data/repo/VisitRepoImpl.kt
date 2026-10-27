package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.VisitDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.mapper.toLines
import com.example.medicalrep.data.local.media.MediaFileStorage
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.domain.repo.VisitRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VisitRepoImpl @Inject constructor(
    private val dao: VisitDao,
    private val media: MediaFileStorage,
) : VisitRepo {

    override fun visits(query: String) = pagedFlow({ dao.search(normalizeSearch(query)) }) { it.toDomain() }

    override fun visitsFiltered(type: VisitType?, syncStatus: SyncStatus?, repId: String?, fromMillis: Long, toMillis: Long) =
        pagedFlow({ dao.pagedFiltered(type, syncStatus, repId, fromMillis, toMillis) }) { it.toDomain() }

    override fun requiredVisits(shift: Shift?, classCategory: String?) =
        pagedFlow({ dao.requiredPaged(shift, classCategory) }) { it.toDomain() }

    override fun requiredCount(shift: Shift?, classCategory: String?) = dao.requiredCount(shift, classCategory)

    override fun doneVisits(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?) =
        pagedFlow({ dao.donePaged(shift, doubleOnly, syncStatus, classCategory) }) { it.toDomain() }

    override fun doneCount(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?) =
        dao.doneCount(shift, doubleOnly, syncStatus, classCategory)

    override fun missedVisits(shift: Shift?, classCategory: String?) =
        pagedFlow({ dao.missedPaged(shift, classCategory) }) { it.toDomain() }

    override fun missedCount(shift: Shift?, classCategory: String?) = dao.missedCount(shift, classCategory)

    override suspend fun markMissed(beforeMillis: Long) = dao.markMissed(beforeMillis)

    override fun pendingRequests(shift: Shift?, classCategory: String?) =
        pagedFlow({ dao.pendingPaged(shift, classCategory) }) { it.toDomain() }

    override fun rejectedRequests(shift: Shift?, classCategory: String?) =
        pagedFlow({ dao.rejectedPaged(shift, classCategory) }) { it.toDomain() }

    override fun rejectedRequestCount(shift: Shift?, classCategory: String?) = dao.rejectedFilteredCount(shift, classCategory)

    override fun pendingRequestCount(shift: Shift?, classCategory: String?) = dao.pendingFilteredCount(shift, classCategory)

    override fun pendingCount(): Flow<Int> = dao.countPending()

    override fun todayVisits(fromMillis: Long, toMillis: Long, limit: Int): Flow<List<Visit>> =
        dao.scheduledBetween(fromMillis, toMillis, limit).map { list -> list.map { it.toDomain() } }

    override fun notSyncedCount(): Flow<Int> = dao.countNotSynced()

    override fun startableVisits() = pagedFlow({ dao.startablePaged() }) { it.toDomain() }

    override suspend fun get(id: String): Visit? = dao.getWithDetails(id)?.toDomain()

    override suspend fun save(visit: Visit) {
        val id = requireNotNull(visit.id) { "A stored visit needs an id" }
        val voiceNotePath = visit.voiceNote?.let { media.save("voice_$id.m4a", it) } ?: visit.voiceNotePath
        val imageNote = visit.imageNote?.let { media.copy("image_$id.jpg", it) }
        dao.save(visit.copy(voiceNotePath = voiceNotePath, imageNote = imageNote).toEntity(), visit.toLines())
    }

    override suspend fun delete(id: String) {
        dao.getById(id)?.let { saved ->
            saved.voiceNotePath?.let(media::delete)
            saved.imageNote?.let(media::deleteUri)
        }
        dao.deleteById(id)
    }

    override suspend fun applyDecision(backendId: String, approved: Boolean, decidedAtMillis: Long) =
        dao.applyDecision(backendId, approved, decidedAtMillis) > 0

    override suspend fun dismissRejected(id: String) = dao.dismissRejected(id) > 0

    override suspend fun cancelRequest(id: String) = dao.cancelRequest(id) > 0

    override suspend fun notSynced(): List<Visit> = dao.getNotSynced().mapNotNull { dao.getWithDetails(it.id)?.toDomain() }

    override suspend fun markSynced(id: String, backendId: String?) {
        dao.markSynced(id, backendId)
    }
}
