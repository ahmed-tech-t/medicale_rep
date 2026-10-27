package com.example.medicalrep.data.local.relation

import androidx.room.Embedded
import com.example.medicalrep.data.local.entity.ProductEntity

/** A product plus how many were sold: the quantity of every order line that names it. */
data class ProductWithSold(
    @Embedded val product: ProductEntity,
    val sold: Int,
)
