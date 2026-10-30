package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * A sample or gift given in a visit. There is no foreign key to the catalog on purpose: replacing a catalog
 * row during a download must never delete lines of visits that are not uploaded yet.
 */
@Entity(
    tableName = "visit_items",
    primaryKeys = ["visitId", "itemId"],
    foreignKeys = [
        ForeignKey(VisitEntity::class, parentColumns = ["id"], childColumns = ["visitId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("itemId")],
)
data class VisitItemEntity(
    val visitId: String,
    val itemId: String,
    val quantity: Int,
)
