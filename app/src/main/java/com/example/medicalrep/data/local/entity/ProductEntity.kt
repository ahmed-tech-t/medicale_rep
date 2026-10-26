package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** An item for sale. Samples and gifts are [CustodyEntity]. */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val discountPercent: Int,
    val searchText: String = "",
)
