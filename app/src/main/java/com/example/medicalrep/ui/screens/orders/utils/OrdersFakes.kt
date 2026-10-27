package com.example.medicalrep.ui.screens.orders.utils

import com.example.medicalrep.ui.models.OrderItemUi
import com.example.medicalrep.ui.models.OrderUi

/** Sample orders for previews. */
object OrdersFakes {
    fun orders() = listOf(
        OrderUi(
            id = "o1001", number = 1001, date = "26 أكتوبر، 2026", time = "9:22 م", total = 1388.8,
            items = listOf(OrderItemUi("p1", "Panadol Extra", quantity = 4, unitPrice = 434, discountPercent = 20, lineTotal = 1388.8)),
        ),
        OrderUi(id = "o1000", number = 1000, date = "25 أكتوبر، 2026", time = "7:22 م", total = 775.5),
        OrderUi(id = "o999", number = 999, date = "23 أكتوبر، 2026", time = "9:22 م", total = 694.4),
    )
}
