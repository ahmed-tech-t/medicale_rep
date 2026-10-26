package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_info")
data class SyncInfoEntity(
    @PrimaryKey val key: String,
    val lastUpdatedMillis: Long,
)
