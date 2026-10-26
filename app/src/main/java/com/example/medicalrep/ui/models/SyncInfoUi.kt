package com.example.medicalrep.ui.models

import androidx.compose.runtime.Immutable

/** [lastUpdated] is empty when the card was never updated. */
@Immutable
data class SyncInfoUi(
    val key: String = "",
    val lastUpdated: String = "",
)
