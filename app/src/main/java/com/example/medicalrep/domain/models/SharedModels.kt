package com.example.medicalrep.domain.models

import androidx.annotation.StringRes
import com.example.medicalrep.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

sealed class ConnectionState {
    data object Online : ConnectionState()
    data object Offline : ConnectionState()
}


data class StatMetric(
    val id: String,
    val title: String,
    val value: String,
    val hint: String? = null,
)


data class AttachmentPill(
    val id: String,
    val label: String,
    val isRequired: Boolean = false,
)

