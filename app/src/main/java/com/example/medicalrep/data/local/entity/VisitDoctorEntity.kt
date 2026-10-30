package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** The doctors seen in a visit. Removing either side removes only the link. */
@Entity(
    tableName = "visit_doctors",
    primaryKeys = ["visitId", "doctorId"],
    foreignKeys = [
        ForeignKey(VisitEntity::class, parentColumns = ["id"], childColumns = ["visitId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DoctorEntity::class, parentColumns = ["id"], childColumns = ["doctorId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("doctorId")],
)
data class VisitDoctorEntity(
    val visitId: String,
    val doctorId: String,
)
