package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.TrackPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TrackPointDao : BaseDao<TrackPointEntity> {

    @Query("SELECT * FROM track_points WHERE id = :id")
    abstract suspend fun getById(id: Long): TrackPointEntity?

    @Query("SELECT * FROM track_points ORDER BY timeMillis")
    abstract fun getAll(): Flow<List<TrackPointEntity>>

    @Query("SELECT * FROM track_points WHERE visitId = :visitId ORDER BY timeMillis")
    abstract fun getByVisit(visitId: String): Flow<List<TrackPointEntity>>

    /** One rep's points between [fromMillis] and [toMillis], in time order (for the map). */
    @Query("SELECT * FROM track_points WHERE repId = :repId AND timeMillis BETWEEN :fromMillis AND :toMillis ORDER BY timeMillis")
    abstract fun getByRepAndDay(repId: String, fromMillis: Long, toMillis: Long): Flow<List<TrackPointEntity>>

    /** Search by the visit the point belongs to. */
    @Query("SELECT * FROM track_points WHERE visitId LIKE '%' || :query || '%' ORDER BY timeMillis")
    abstract fun search(query: String): PagingSource<Int, TrackPointEntity>

    @Query("DELETE FROM track_points WHERE id = :id")
    abstract suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM track_points")
    abstract suspend fun clear()

    @Query("SELECT * FROM track_points WHERE syncStatus = 'NotSynced' ORDER BY timeMillis")
    abstract suspend fun getNotSynced(): List<TrackPointEntity>

    @Query("UPDATE track_points SET syncStatus = 'Synced', backendId = :backendId WHERE id = :localId")
    abstract suspend fun markSynced(localId: Long, backendId: String?): Int

    /** A point for a rep and time that is already stored replaces it, so no point is ever stored twice. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrReplace(points: List<TrackPointEntity>): List<Long>

    @Query("DELETE FROM track_points WHERE repId = :repId AND syncStatus = 'Synced' AND timeMillis BETWEEN :fromMillis AND :toMillis")
    abstract suspend fun deleteSyncedOfRepBetween(repId: String, fromMillis: Long, toMillis: Long)

    /** A supervisor opened a rep's day: the downloaded points replace the ones kept for it (they are stored as synced). */
    @Transaction
    open suspend fun replaceForRepAndDay(repId: String, fromMillis: Long, toMillis: Long, points: List<TrackPointEntity>) {
        deleteSyncedOfRepBetween(repId, fromMillis, toMillis)
        insertOrReplace(points)
    }

    /** Deletes uploaded (or downloaded) points older than [beforeMillis]; points not uploaded yet are never deleted. */
    @Query("DELETE FROM track_points WHERE syncStatus = 'Synced' AND timeMillis < :beforeMillis")
    abstract suspend fun purgeOlderThan(beforeMillis: Long): Int
}
