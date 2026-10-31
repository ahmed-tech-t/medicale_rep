package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao : BaseDao<NotificationEntity> {
    @Query("SELECT * FROM notifications WHERE repId = :repId ORDER BY createdAtMillis DESC")
    fun ofRep(repId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE repId = :repId")
    fun count(repId: String): Flow<Int>
}
