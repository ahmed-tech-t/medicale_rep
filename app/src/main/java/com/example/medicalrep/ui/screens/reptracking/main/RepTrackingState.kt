package com.example.medicalrep.ui.screens.reptracking.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.ui.models.RepUi
import com.example.medicalrep.ui.models.VisitUi

@Immutable
data class RepTrackingState(
    val reps: List<RepUi> = emptyList(),
    val selectedRepId: String? = null,
    /** The day whose visits are shown. */
    val dateMillis: Long = 0L,
    /** The visits the selected rep finished that day. */
    val visits: List<VisitUi> = emptyList(),
    /** The selected rep's day: done out of planned, and missed. */
    val progress: VisitProgress = VisitProgress(),
) {
    val selectedRepName: String get() = reps.firstOrNull { it.id == selectedRepId }?.displayName.orEmpty()
    val visitsCount: Int get() = visits.size
    val productsCount: Int get() = visits.sumOf { visit -> visit.products.sumOf { it.quantity } }
    val giftsCount: Int get() = visits.sumOf { visit -> visit.gifts.sumOf { it.quantity } }
}
