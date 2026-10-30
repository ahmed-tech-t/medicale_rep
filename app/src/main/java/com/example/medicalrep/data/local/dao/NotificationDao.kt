package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao : BaseDao<NotificationEntity> {
    @Query("SELECT * FROM notifications WHERE repId = :repId ORDER BY createdAtMillis DESC")
    fun ofRep(repId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE repId = :repId AND isRead = 0")
    fun unreadCount(repId: String): Flow<Int>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String): Int
}
