package com.example.medicalrep.ui.screens.products.main

import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.models.ProductUi

sealed interface ProductsEvents {
    data object Back : ProductsEvents
    data class SearchChanged(val query: String) : ProductsEvents
    data class ChangeQuantity(val product: ProductUi, val delta: Int) : ProductsEvents

    /** Opens the account picker; the order is saved once an account is chosen. */
    data object Order : ProductsEvents
    data object DismissOverlay : ProductsEvents
    data class AccountQueryChanged(val query: String) : ProductsEvents
    data class SelectAccount(val account: AccountUi) : ProductsEvents
}
