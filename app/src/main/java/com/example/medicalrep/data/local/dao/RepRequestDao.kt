package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.RepRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepRequestDao : BaseDao<RepRequestEntity> {
    @Query("SELECT * FROM rep_requests WHERE repId = :repId ORDER BY dateMillis DESC, createdAtMillis DESC")
    fun ofRep(repId: String): Flow<List<RepRequestEntity>>

    @Query("UPDATE rep_requests SET status = CASE WHEN :approved THEN 'Approved' ELSE 'Rejected' END, decidedAtMillis = :decidedAtMillis WHERE id = :id AND status = 'Pending'")
    suspend fun decide(id: String, approved: Boolean, decidedAtMillis: Long): Int
}
