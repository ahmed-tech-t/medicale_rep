package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.CatalogItemEntity
import com.example.medicalrep.data.local.entity.UserInventoryEntity
import com.example.medicalrep.data.local.relation.CatalogItemWithStock
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType

/** What is left of a product is what the rep holds minus what orders sold, never below zero. */
fun CatalogItemWithStock.toProduct() = Product(
    id = item.id,
    name = item.name,
    description = item.description,
    price = item.price ?: 0,
    discountPercent = item.discountPercent,
    received = original,
    remaining = (stock - sold).coerceAtLeast(0),
)

fun CatalogItemWithStock.toCustody() = Custody(
    id = item.id,
    name = item.name,
    kind = item.type,
    description = item.description,
    received = original,
    remaining = stock,
)

fun Product.toItem() = CatalogItemEntity(
    id = requireNotNull(id) { "A stored product needs an id" },
    name = name.orEmpty(),
    description = description.orEmpty(),
    type = ProductType.Product,
    price = price,
    discountPercent = discountPercent,
    searchText = searchTextOf(name, description),
)

/** A product the rep just got: all of it is left. */
fun Product.toInventory(repId: String) =
    UserInventoryEntity(repId, requireNotNull(id) { "A stored product needs an id" }, original = received, current = received)

fun Custody.toItem() = CatalogItemEntity(
    id = id,
    name = name,
    description = description,
    type = kind,
    price = null,
    discountPercent = 0,
    searchText = searchTextOf(name, description),
)

fun Custody.toInventory(repId: String) = UserInventoryEntity(repId, id, original = received, current = remaining)
