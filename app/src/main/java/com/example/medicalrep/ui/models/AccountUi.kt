package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.AccountType

@Immutable
data class AccountUi(
    val id: String = "",
    val name: String = "",
    val entityType: String = "",
    val type: AccountType? = null,
    val classCategory: String = "",
    val specialty: String = "",
    val city: String = "",
    val district: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isSynced: Boolean = false,
)
