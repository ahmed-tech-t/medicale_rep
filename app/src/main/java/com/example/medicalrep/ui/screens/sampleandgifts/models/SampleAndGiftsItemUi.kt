package com.example.medicalrep.ui.screens.sampleandgifts.models

import androidx.compose.runtime.Immutable

enum class SampleAndGiftsItemType {
    Sample,
    Gift,
}

@Immutable
data class SampleAndGiftsItemUi(
    val id: String,
    val type: SampleAndGiftsItemType,
    val name: String,
    val received: Int,
    val dispatched: Int,
    val remaining: Int,
)
