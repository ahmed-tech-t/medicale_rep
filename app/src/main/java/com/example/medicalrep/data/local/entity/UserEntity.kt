package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.UserRole

@Entity(
    tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["supervisorId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("supervisorId"), Index("role")],
)
data class UserEntity(
    @PrimaryKey val id: String,
    val role: UserRole,
    /** The rep's supervisor; empty for supervisors. */
    val supervisorId: String?,
    val displayName: String,
    val roleTitle: String,
    val specialty: String,
    val employeeCode: String,
    val deviceId: String,
    val initials: String,
    val searchText: String = "",
)
