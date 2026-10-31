package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    foreignKeys = [ForeignKey(RepEntity::class, parentColumns = ["id"], childColumns = ["repId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("repId", "createdAtMillis")],
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val repId: String,
    val title: String,
    val message: String,
    val type: String,
    val createdAtMillis: Long,
)
