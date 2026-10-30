package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.medicalrep.domain.models.ProductType

/** A product for sale, a sample or a gift. How many the rep holds is in [UserInventoryEntity]. */
@Entity(tableName = "catalog_items", indices = [Index("type")])
data class CatalogItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val type: ProductType,
    /** Only products for sale have a price. */
    val price: Int?,
    val discountPercent: Int,
    val searchText: String = "",
)
