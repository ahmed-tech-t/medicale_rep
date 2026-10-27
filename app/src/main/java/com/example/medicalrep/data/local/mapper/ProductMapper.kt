package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.ProductEntity
import com.example.medicalrep.data.local.relation.ProductWithSold
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Product

/** What is left is what the rep received minus what orders sold, never below zero. */
fun ProductWithSold.toDomain() = Product(
    id = product.id,
    name = product.name,
    description = product.description,
    price = product.price,
    discountPercent = product.discountPercent,
    received = product.quantity,
    remaining = (product.quantity - sold).coerceAtLeast(0),
)

fun Product.toEntity() = ProductEntity(
    id = requireNotNull(id) { "A stored product needs an id" },
    name = name.orEmpty(),
    description = description.orEmpty(),
    price = price,
    discountPercent = discountPercent,
    quantity = received,
    searchText = searchTextOf(name, description),
)
