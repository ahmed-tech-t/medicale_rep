package com.example.medicalrep.ui.screens.products.utils

import com.example.medicalrep.ui.screens.products.main.ProductsState
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.models.ProductUi

/** Sample data shown until products are loaded from Room. */
object ProductsFakes {

    private fun product(id: String, name: String, price: Int, discountPercent: Int, quantity: Int = 0) = ProductUi(
        id = id,
        name = name,
        quantity = quantity,
        kind = ProductType.Product,
        remaining = 0,
        description = "",
        price = price,
        discountPercent = discountPercent,
    )

    fun products(): List<ProductUi> = listOf(
        product(id = "p1", name = "بانادول اكسترا", price = 434, discountPercent = 20, quantity = 2),
        product(id = "p2", name = "أوميبرازول فورت 40 مجم", price = 120, discountPercent = 20),
        product(id = "p3", name = "كاردكس بلس 10 مجم", price = 88, discountPercent = 15, quantity = 1),
        product(id = "p4", name = "أموكسيسيلين 500 مجم", price = 65, discountPercent = 10, quantity = 2),
        product(id = "p5", name = "كونكور 5 مجم", price = 150, discountPercent = 20),
    )

    fun state(): ProductsState = ProductsState(products = products())
}
