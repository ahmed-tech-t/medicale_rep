package com.example.medicalrep.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Upsert

/**
 * Create, update and delete once for every table. [upsert] changes a row in place; it never deletes and
 * re-inserts, so rows that point at it (a visit's lines, a user's visits) are not removed by a download.
 */
interface BaseDao<T> {
    @Insert
    suspend fun insert(entity: T): Long

    @Insert
    suspend fun insertAll(entities: List<T>): List<Long>

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T): Int

    @Upsert
    suspend fun upsert(entity: T): Long

    @Upsert
    suspend fun upsertAll(entities: List<T>): List<Long>
}
