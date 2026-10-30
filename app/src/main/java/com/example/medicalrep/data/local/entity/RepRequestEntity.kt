package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.RequestCategory
import com.example.medicalrep.domain.models.RequestStatus
import com.example.medicalrep.domain.models.Shift

@Entity(
    tableName = "rep_requests",
    foreignKeys = [ForeignKey(RepEntity::class, parentColumns = ["id"], childColumns = ["repId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("repId", "dateMillis")],
)
data class RepRequestEntity(
    @PrimaryKey val id: String,
    val repId: String,
    val dateMillis: Long,
    val category: RequestCategory,
    val subType: String,
    val shift: Shift?,
    val status: RequestStatus,
    val notes: String,
    val createdAtMillis: Long,
    val decidedAtMillis: Long?,
)
