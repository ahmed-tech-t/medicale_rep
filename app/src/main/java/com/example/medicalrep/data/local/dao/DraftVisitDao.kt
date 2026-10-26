package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.DraftVisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftVisitDao : BaseDao<DraftVisitEntity> {

    @Query("SELECT * FROM visit_draft WHERE id = 1")
    fun get(): Flow<DraftVisitEntity?>

    @Query("SELECT * FROM visit_draft WHERE id = 1")
    suspend fun getOnce(): DraftVisitEntity?

    @Query("DELETE FROM visit_draft")
    suspend fun clear()
}
