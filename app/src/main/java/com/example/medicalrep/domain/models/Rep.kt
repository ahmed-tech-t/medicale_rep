package com.example.medicalrep.domain.models

data class Rep(
    val id: String,
    val displayName: String,
    val employeeCode: String = "",
    val deviceId: String = "",
    val role: RepRole = RepRole.Rep,
    val supervisorId: String? = null,
)
