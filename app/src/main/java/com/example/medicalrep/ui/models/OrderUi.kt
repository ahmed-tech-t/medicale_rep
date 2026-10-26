package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class OrderUi(
    val id: String = "",
    val accountName: String = "",
    val date: String = "",
    val time: String = "",
    val total: Int = 0,
    val isSynced: Boolean = false,
    val items: List<OrderItemUi> = emptyList(),
)

@Immutable
data class OrderItemUi(
    val productId: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val unitPrice: Int = 0,
    val discountPercent: Int = 0,
    val lineTotal: Int = 0,
)
