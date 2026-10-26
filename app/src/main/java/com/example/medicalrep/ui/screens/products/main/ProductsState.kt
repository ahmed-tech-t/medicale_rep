package com.example.medicalrep.ui.screens.products.main

import androidx.compose.runtime.Immutable

/** The product list is paged and comes from the ViewModel's `products` flow; quantities are kept here by product id. */
@Immutable
data class ProductsState(
    val query: String = "",
    val quantities: Map<String, Int> = emptyMap(),
) {
    val hasSelection: Boolean get() = quantities.values.any { it > 0 }
}
