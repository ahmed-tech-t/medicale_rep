package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** How many of an item the rep got ([original]) and how many are left ([current]). */
@Entity(
    tableName = "user_inventory",
    primaryKeys = ["repId", "itemId"],
    foreignKeys = [
        ForeignKey(RepEntity::class, parentColumns = ["id"], childColumns = ["repId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(CatalogItemEntity::class, parentColumns = ["id"], childColumns = ["itemId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("itemId")],
)
data class UserInventoryEntity(
    val repId: String,
    val itemId: String,
    val original: Int,
    val current: Int,
)
