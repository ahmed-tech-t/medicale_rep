package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** The districts a rep covers. */
@Entity(
    tableName = "user_districts",
    primaryKeys = ["repId", "districtId"],
    foreignKeys = [
        ForeignKey(RepEntity::class, parentColumns = ["id"], childColumns = ["repId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DistrictEntity::class, parentColumns = ["id"], childColumns = ["districtId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("districtId")],
)
data class UserDistrictEntity(val repId: String, val districtId: String)
