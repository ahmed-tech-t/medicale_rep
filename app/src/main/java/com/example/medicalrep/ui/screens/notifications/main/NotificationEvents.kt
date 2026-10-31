package com.example.medicalrep.ui.screens.notifications.main

sealed interface NotificationEvents {
    data object OnBack : NotificationEvents
}
