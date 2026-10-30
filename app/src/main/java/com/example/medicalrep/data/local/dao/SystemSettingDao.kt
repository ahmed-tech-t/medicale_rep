package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.SystemSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemSettingDao : BaseDao<SystemSettingEntity> {
    @Query("SELECT value FROM system_settings WHERE `key` = :key")
    fun get(key: String): Flow<String?>
}
