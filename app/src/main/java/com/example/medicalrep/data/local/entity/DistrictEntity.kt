package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "districts",
    foreignKeys = [ForeignKey(CityEntity::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("cityId")],
)
data class DistrictEntity(@PrimaryKey val id: String, val cityId: String, val name: String)
