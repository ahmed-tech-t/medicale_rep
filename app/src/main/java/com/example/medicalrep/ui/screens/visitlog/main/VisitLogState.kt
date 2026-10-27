package com.example.medicalrep.ui.screens.visitlog.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.VisitFilter

enum class VisitLogTab { Required, Done, Missed }

sealed interface VisitLogOverlay {
    data object None : VisitLogOverlay
    data object Filter : VisitLogOverlay

    /** Starting [visitId] while another visit is pending: waits for the rep's answer. */
    data class PendingVisit(val visitId: String) : VisitLogOverlay
}

/** The list is paged and comes from the ViewModel's `visits` flow; this holds what surrounds it. */
@Immutable
data class VisitLogState(
    val selectedTab: VisitLogTab = VisitLogTab.Required,
    /** Belongs to the selected tab: switching tab resets it to All. */
    val filter: VisitFilter = VisitFilter.All,
    val resultCount: Int = 0,
    val overlay: VisitLogOverlay = VisitLogOverlay.None,
    val hasPendingVisit: Boolean = false,
) {
    val isFiltered: Boolean get() = filter != VisitFilter.All
}
