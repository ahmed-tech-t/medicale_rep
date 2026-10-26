package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable

/** A sample or gift the rep carries; [remaining] already subtracts what is not uploaded yet. */
@Immutable
data class CustodyUi(
    val id: String = "",
    val name: String = "",
    val isGift: Boolean = true,
    val description: String = "",
    val received: Int = 0,
    val dispatched: Int = 0,
    val pending: Int = 0,
    val remaining: Int = 0,
)
