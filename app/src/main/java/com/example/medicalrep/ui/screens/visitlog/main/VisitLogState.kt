package com.example.medicalrep.ui.screens.visitlog.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.ui.components.widgets.filter.FilterState

enum class VisitLogTab { Required, Done }

sealed interface VisitLogOverlay {
    data object None : VisitLogOverlay
    data object Filter : VisitLogOverlay

    /** The rep is writing why [visitId] is cancelled; the text is [VisitLogState.cancelReason]. */
    data class CancelVisit(val visitId: String) : VisitLogOverlay

    /** Starting [visitId] while another visit is pending: waits for the rep's answer. */
    data class PendingVisit(val visitId: String) : VisitLogOverlay
}

/** The list is paged and comes from the ViewModel's `visits` flow; this holds what surrounds it. */
@Immutable
data class VisitLogState(
    val selectedTab: VisitLogTab = VisitLogTab.Required,
    /** What the list is narrowed by now; it belongs to the selected tab: switching tab clears it. */
    val filter: VisitListFilter = VisitListFilter(),
    /** What the search box holds; the list uses it trimmed, after the rep stops typing. Switching tab clears it. */
    val query: String = "",
    /** The filter sheet's form: becomes [filter] when the rep applies it. */
    val sheet: FilterState = FilterState(),
    /** The notes of the cancel sheet: a visit is only cancelled with a reason. */
    val cancelReason: TextFieldState = TextFieldState(),
    val resultCount: Int = 0,
    val overlay: VisitLogOverlay = VisitLogOverlay.None,
    val hasPendingVisit: Boolean = false,
) {
    val isFiltered: Boolean get() = filter.isActive
}
