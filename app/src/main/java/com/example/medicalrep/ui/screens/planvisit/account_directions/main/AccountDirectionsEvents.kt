package com.example.medicalrep.ui.screens.planvisit.account_directions.main

sealed interface AccountDirectionsEvents {
    data object Back : AccountDirectionsEvents
    data object ToggleCard : AccountDirectionsEvents
    data object RefreshLocation : AccountDirectionsEvents
}
