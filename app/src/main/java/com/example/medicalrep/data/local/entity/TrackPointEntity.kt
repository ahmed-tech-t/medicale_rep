package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.SyncStatus

/** One GPS point of a rep; a point can be stored only once per rep and time. */
@Entity(
    tableName = "track_points",
    foreignKeys = [
        ForeignKey(VisitEntity::class, parentColumns = ["id"], childColumns = ["visitId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [
        Index("repId", "timeMillis", unique = true),
        Index("visitId"),
        Index("syncStatus"),
    ],
)
data class TrackPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val backendId: String?,
    val repId: String,
    val visitId: String?,
    val latitude: Double,
    val longitude: Double,
    val timeMillis: Long,
    val syncStatus: SyncStatus,
)
