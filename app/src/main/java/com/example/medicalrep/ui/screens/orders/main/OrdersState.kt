package com.example.medicalrep.ui.screens.orders.main

import androidx.compose.runtime.Immutable

/** The list itself is paged and comes from the ViewModel's `orders` flow; this holds which cards are open. */
@Immutable
data class OrdersState(
    val expandedIds: Set<String> = emptySet(),
)
