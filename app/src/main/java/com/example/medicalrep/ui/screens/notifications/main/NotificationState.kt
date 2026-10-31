package com.example.medicalrep.ui.screens.notifications.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.Notification

@Immutable
data class NotificationState(
    val notifications: List<Notification> = emptyList(),
)
