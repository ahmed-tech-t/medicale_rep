package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * A sample or gift given in a visit. There is no foreign key to custody on purpose: replacing a custody row
 * during a download must never delete lines of visits that are not uploaded yet.
 */
@Entity(
    tableName = "visit_custody",
    primaryKeys = ["visitId", "custodyId"],
    foreignKeys = [
        ForeignKey(VisitEntity::class, parentColumns = ["id"], childColumns = ["visitId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("custodyId")],
)
data class VisitCustodyEntity(
    val visitId: String,
    val custodyId: String,
    val quantity: Int,
)
