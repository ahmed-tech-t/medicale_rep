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
    /** How many the rep received; what orders sold is counted from the order lines. */
    val quantity: Int,
    val searchText: String = "",
)
