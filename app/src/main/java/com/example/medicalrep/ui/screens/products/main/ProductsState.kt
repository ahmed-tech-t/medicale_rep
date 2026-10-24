package com.example.medicalrep.ui.screens.products.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.ProductUi

@Immutable
data class ProductsState(
    val query: String = "",
    val products: List<ProductUi> = emptyList(),
) {
    val visibleProducts: List<ProductUi>
        get() = products.filter { it.name.contains(query, ignoreCase = true) }

    val hasSelection: Boolean get() = products.any { it.quantity > 0 }
}
