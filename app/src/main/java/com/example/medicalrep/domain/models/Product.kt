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
    /** For a product for sale: how many the rep received. [remaining] is what is left of them after the orders. */
    val received: Int = 0,
)

/** A product for sale, or a sample or gift the rep carries. */
enum class ProductType {
    Product,
    Sample,
    Gift,
}