package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.TrackPointDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.TrackPoint
import com.example.medicalrep.domain.repo.TrackPointRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackPointRepoImpl @Inject constructor(private val dao: TrackPointDao) : TrackPointRepo {

    override suspend fun add(point: TrackPoint) {
        dao.insertOrReplace(listOf(point.toEntity()))
    }

    override fun byVisit(visitId: String): Flow<List<TrackPoint>> =
        dao.getByVisit(visitId).map { list -> list.map { it.toDomain() } }

    override fun byRepAndDay(repId: String, fromMillis: Long, toMillis: Long): Flow<List<TrackPoint>> =
        dao.getByRepAndDay(repId, fromMillis, toMillis).map { list -> list.map { it.toDomain() } }

    override suspend fun replaceForRepAndDay(repId: String, fromMillis: Long, toMillis: Long, points: List<TrackPoint>) =
        dao.replaceForRepAndDay(repId, fromMillis, toMillis, points.map { it.copy(syncStatus = SyncStatus.Synced).toEntity() })

    override suspend fun notSynced(): List<TrackPoint> = dao.getNotSynced().map { it.toDomain() }

    override suspend fun markSynced(id: Long, backendId: String?) {
        dao.markSynced(id, backendId)
    }

    override suspend fun purgeOlderThan(beforeMillis: Long) = dao.purgeOlderThan(beforeMillis)
}
