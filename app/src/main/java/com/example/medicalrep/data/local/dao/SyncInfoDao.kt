package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.SyncInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncInfoDao : BaseDao<SyncInfoEntity> {

    @Query("SELECT * FROM sync_info WHERE `key` = :key")
    suspend fun getById(key: String): SyncInfoEntity?

    @Query("SELECT * FROM sync_info ORDER BY `key`")
    fun getAll(): Flow<List<SyncInfoEntity>>

    @Query("SELECT * FROM sync_info WHERE `key` LIKE '%' || :query || '%' ORDER BY `key`")
    fun search(query: String): PagingSource<Int, SyncInfoEntity>

    @Query("DELETE FROM sync_info WHERE `key` = :key")
    suspend fun deleteById(key: String): Int

    @Query("DELETE FROM sync_info")
    suspend fun clear()
}
