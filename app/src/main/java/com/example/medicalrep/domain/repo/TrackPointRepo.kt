package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.TrackPoint
import kotlinx.coroutines.flow.Flow

interface TrackPointRepo {
    /** A point for a rep and time that is already stored replaces it. */
    suspend fun add(point: TrackPoint)
    fun byVisit(visitId: String): Flow<List<TrackPoint>>
    fun byRepAndDay(repId: String, fromMillis: Long, toMillis: Long): Flow<List<TrackPoint>>

    /** A supervisor opened a rep's day: the downloaded points replace the ones kept for it, unsent points stay. */
    suspend fun replaceForRepAndDay(repId: String, fromMillis: Long, toMillis: Long, points: List<TrackPoint>)

    suspend fun notSynced(): List<TrackPoint>
    suspend fun markSynced(id: Long, backendId: String?)

    /** Deletes uploaded points older than [beforeMillis]; points not uploaded yet are never deleted. */
    suspend fun purgeOlderThan(beforeMillis: Long): Int
}
