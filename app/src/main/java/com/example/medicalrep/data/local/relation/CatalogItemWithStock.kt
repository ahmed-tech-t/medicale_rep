package com.example.medicalrep.data.local.relation

import androidx.room.Embedded
import com.example.medicalrep.data.local.entity.CatalogItemEntity

/** A catalog item with the stock the rep holds of it (0 without an inventory row) and how many orders sold. */
data class CatalogItemWithStock(
    @Embedded val item: CatalogItemEntity,
    val original: Int,
    val stock: Int,
    val sold: Int,
)
