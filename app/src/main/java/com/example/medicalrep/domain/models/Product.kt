package com.example.medicalrep.domain.models

data class Product(
    val id: String? = null,
    val name: String? = null,
    val quantity: Int = 0,
    val kind: ProductType? = null,
    val description: String? = null,
    val remaining: Int = 0,
    val price: Int = 0,
    val discountPercent: Int = 0,
)

enum class ProductType {
    Gift,
    Product,
}