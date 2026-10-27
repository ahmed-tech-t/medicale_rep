package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.TrackPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Records the points added; set [failOnAdd] to make `add` throw, like a failed insert. */
class FakeTrackPointRepo : TrackPointRepo {
    val added = mutableListOf<TrackPoint>()
    var failOnAdd = false

    override suspend fun add(point: TrackPoint) {
        if (failOnAdd) error("insert failed")
        added += point
    }

    override fun byVisit(visitId: String): Flow<List<TrackPoint>> = flowOf(emptyList())
    override fun byRepAndDay(repId: String, fromMillis: Long, toMillis: Long): Flow<List<TrackPoint>> = flowOf(emptyList())
    override suspend fun replaceForRepAndDay(repId: String, fromMillis: Long, toMillis: Long, points: List<TrackPoint>) = Unit
    override suspend fun notSynced(): List<TrackPoint> = added.toList()
    override suspend fun markSynced(id: Long, backendId: String?) = Unit
    override suspend fun purgeOlderThan(beforeMillis: Long): Int = 0
}
