package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.SyncStatus

@Entity(tableName = "doctors", indices = [Index("backendId"), Index("syncStatus")])
data class DoctorEntity(
    @PrimaryKey val id: String,
    val backendId: String?,
    val name: String,
    val classCategory: String,
    val specialty: String,
    val syncStatus: SyncStatus,
)
