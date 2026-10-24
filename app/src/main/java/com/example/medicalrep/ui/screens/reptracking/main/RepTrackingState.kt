package com.example.medicalrep.ui.screens.reptracking.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.VisitUi

@Immutable
data class RepTrackingState(
    val reps: List<String> = emptyList(),
    val selectedRep: String = "",
    val fromDateMillis: Long = 0L,
    val toDateMillis: Long = 0L,
    val visits: List<VisitUi> = emptyList(),
) {
    val visitsCount: Int get() = visits.size
    val productsCount: Int get() = visits.sumOf { visit -> visit.products.sumOf { it.quantity } }
    val giftsCount: Int get() = visits.sumOf { visit -> visit.gifts.sumOf { it.quantity } }
}
