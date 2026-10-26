package com.example.medicalrep.ui.screens.planvisit.account_location.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.GeoPoint

@Immutable
data class AccountLocationState(
    val accountLocation: GeoPoint,
    val accountName: String,
    val address: String,
    val classCategory: String,
    val accountType: String,
    val specialty: String,
)
