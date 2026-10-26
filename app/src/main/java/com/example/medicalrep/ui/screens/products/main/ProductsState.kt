package com.example.medicalrep.ui.screens.products.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.ProductUi

sealed interface ProductsOverlay {
    data object None : ProductsOverlay
    data object PickAccount : ProductsOverlay
}

/**
 * The product list is paged and comes from the ViewModel's `products` flow. [selected] keeps the chosen products by id
 * with their quantity, name, price and discount, so an order can be made even when their page is no longer loaded.
 */
@Immutable
data class ProductsState(
    val query: String = "",
    val selected: Map<String, ProductUi> = emptyMap(),
    val overlay: ProductsOverlay = ProductsOverlay.None,
    val accountQuery: String = "",
) {
    val hasSelection: Boolean get() = selected.isNotEmpty()
}
