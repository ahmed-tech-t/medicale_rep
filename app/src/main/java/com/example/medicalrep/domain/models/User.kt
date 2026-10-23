package com.example.medicalrep.domain.models

data class User(
    val id: String,
    val displayName: String,
    val roleTitle: String,
    val specialty: String = "",
    val employeeCode: String = "",
    val deviceId: String = "",
    val initials: String = "",
)
