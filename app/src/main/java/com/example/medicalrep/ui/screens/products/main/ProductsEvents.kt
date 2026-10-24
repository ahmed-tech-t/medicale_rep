package com.example.medicalrep.ui.screens.products.main

sealed interface ProductsEvents {
    data object Back : ProductsEvents
    data class SearchChanged(val query: String) : ProductsEvents
    data class ChangeQuantity(val id: String, val delta: Int) : ProductsEvents
    data object Order : ProductsEvents
}
