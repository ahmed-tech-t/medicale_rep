package com.example.medicalrep.ui.screens.planvisit.account_location.main

sealed interface AccountLocationEvents {
    data object Back : AccountLocationEvents
    data object GetDirections : AccountLocationEvents
}
