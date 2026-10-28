package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.RepRole

@Entity(
    tableName = "reps",
    foreignKeys = [
        ForeignKey(
            entity = RepEntity::class,
            parentColumns = ["id"],
            childColumns = ["supervisorId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("supervisorId"), Index("role")],
)
data class RepEntity(
    @PrimaryKey val id: String,
    val role: RepRole,
    /** The rep's supervisor; empty for supervisors. */
    val supervisorId: String?,
    val displayName: String,
    val employeeCode: String,
    val deviceId: String,
    val searchText: String = "",
)
