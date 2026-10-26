package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.ProductEntity
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Product

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    discountPercent = discountPercent,
)

fun Product.toEntity() = ProductEntity(
    id = requireNotNull(id) { "A stored product needs an id" },
    name = name.orEmpty(),
    description = description.orEmpty(),
    price = price,
    discountPercent = discountPercent,
    searchText = searchTextOf(name, description),
)
