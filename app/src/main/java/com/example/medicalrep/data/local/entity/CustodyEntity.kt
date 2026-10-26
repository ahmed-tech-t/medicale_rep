package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.ProductType

/** A sample or gift the rep carries; the numbers are the server's. */
@Entity(tableName = "custody", indices = [Index("kind")])
data class CustodyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val kind: ProductType,
    val description: String,
    val received: Int,
    val dispatched: Int,
    val searchText: String = "",
)
