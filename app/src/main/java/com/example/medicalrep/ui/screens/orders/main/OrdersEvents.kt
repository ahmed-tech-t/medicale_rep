package com.example.medicalrep.ui.screens.orders.main

sealed interface OrdersEvents {
    data object Back : OrdersEvents
    data class ToggleOrder(val id: String) : OrdersEvents
}
