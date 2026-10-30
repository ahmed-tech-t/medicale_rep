package com.example.medicalrep.data.repo

import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.GeoPoint
import kotlin.math.cos
import kotlin.math.pow
import com.example.medicalrep.data.local.dao.VisitDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.mapper.toDoctorLinks
import com.example.medicalrep.data.local.mapper.toLines
import com.example.medicalrep.data.local.media.MediaFileStorage
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitListFilter
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

    override fun requiredVisits(filter: VisitListFilter) = pagedFlow({
        dao.requiredPaged(
            filter.shift, filter.classCategory, filter.cityId, filter.districtId, filter.specialty, filter.fromMillis, filter.toMillis,
            filter.query.trim(), filter.nearLatitude, filter.nearLongitude, longitudeScale(filter.nearLatitude),
        )
    }) { it.toDomain() }

    override fun requiredCount(filter: VisitListFilter) =
        dao.requiredCount(filter.shift, filter.classCategory, filter.cityId, filter.districtId, filter.specialty, filter.fromMillis, filter.toMillis, filter.query.trim())

    override fun doneVisits(filter: VisitListFilter) = pagedFlow({
        dao.donePaged(
            filter.shift, filter.visitMode, filter.syncStatus, filter.classCategory,
            filter.cityId, filter.districtId, filter.specialty, filter.fromMillis, filter.toMillis, filter.query.trim(),
        )
    }) { it.toDomain() }

    override fun doneCount(filter: VisitListFilter) = dao.doneCount(
        filter.shift, filter.visitMode, filter.syncStatus, filter.classCategory,
        filter.cityId, filter.districtId, filter.specialty, filter.fromMillis, filter.toMillis, filter.query.trim(),
    )

    override fun missedVisits(shift: Shift?, classCategory: String?) =
        pagedFlow({ dao.missedPaged(shift, classCategory) }) { it.toDomain() }

    override fun missedCount(shift: Shift?, classCategory: String?) = dao.missedCount(shift, classCategory)

    override suspend fun markMissed(beforeMillis: Long) = dao.markMissed(beforeMillis)

    override fun rejectedRequests(shift: Shift?, classCategory: String?) =
        pagedFlow({ dao.rejectedPaged(shift, classCategory) }) { it.toDomain() }

    override fun rejectedRequestCount(shift: Shift?, classCategory: String?) = dao.rejectedFilteredCount(shift, classCategory)

    override fun bookedDays(): Flow<Map<String, Set<Long>>> =
        dao.bookedDayRows().map { rows -> rows.groupBy({ it.accountId }, { it.dateTimeMillis }).mapValues { it.value.toSet() } }

    override fun requests(approval: ApprovalStatus?) =
        pagedFlow({ dao.requestsPaged(approval) }) { it.toDomain() }

    override fun requestCount(approval: ApprovalStatus?) = dao.requestsCount(approval)

    override fun pendingCount(): Flow<Int> = dao.countPending()

    override fun scheduledFrom(fromMillis: Long, near: GeoPoint?, limit: Int): Flow<List<Visit>> =
        dao.scheduledFrom(fromMillis, near?.latitude, near?.longitude, longitudeScale(near?.latitude), limit).map { list -> list.map { it.toDomain() } }

    override fun doneOnDay(repId: String, fromMillis: Long, toMillis: Long): Flow<List<Visit>> =
        dao.doneBetween(repId, fromMillis, toMillis).map { list -> list.map { it.toDomain() } }

    override fun startableVisits() = pagedFlow({ dao.startablePaged() }) { it.toDomain() }

    override suspend fun get(id: String): Visit? = dao.getWithDetails(id)?.toDomain()

    override suspend fun save(visit: Visit) {
        val id = requireNotNull(visit.id) { "A stored visit needs an id" }
        val voiceNotePath = visit.voiceNote?.let { media.save("voice_$id.m4a", it) } ?: visit.voiceNotePath
        val imageNotes = visit.imageNotes.mapIndexed { index, uri -> media.copy("image_${id}_$index.jpg", uri) }
        dao.save(visit.copy(voiceNotePath = voiceNotePath, imageNotes = imageNotes).toEntity(), visit.toLines(), visit.toDoctorLinks())
    }

    override suspend fun delete(id: String) {
        dao.getById(id)?.let { saved ->
            saved.voiceNotePath?.let(media::delete)
            saved.imageNotes.forEach(media::deleteUri)
        }
        dao.deleteById(id)
    }

    override suspend fun applyDecision(backendId: String, approved: Boolean, decidedAtMillis: Long) =
        dao.applyDecision(backendId, approved, decidedAtMillis) > 0

    override suspend fun dismissRejected(id: String) = dao.dismissRejected(id) > 0

    override suspend fun cancelRequest(id: String) = dao.cancelRequest(id) > 0

    override suspend fun cancelVisit(id: String, reason: String) = dao.cancelVisit(id, reason) > 0

    override suspend fun notSynced(): List<Visit> = dao.getNotSynced().mapNotNull { dao.getWithDetails(it.id)?.toDomain() }

    override suspend fun markSynced(id: String, backendId: String?) {
        dao.markSynced(id, backendId)
    }

    override suspend fun markFailed(id: String) {
        dao.markFailed(id)
    }
}

/** How much a degree of longitude weighs next to a degree of latitude at [latitude]: cos squared (1 when there is no point). */
private fun longitudeScale(latitude: Double?): Double = latitude?.let { cos(Math.toRadians(it)).pow(2) } ?: 1.0
