package com.example.medicalrep.domain.models

data class Notification(
    val id: String,
    val repId: String,
    val title: String,
    val message: String = "",
    val type: String = "",
    val isRead: Boolean = false,
    val createdAtMillis: Long = 0L,
)
