package com.example.medicalrep.ui.screens.visitlog.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.models.VisitUi

enum class VisitLogTab { Required, Done }

sealed interface VisitLogOverlay {
    data object None : VisitLogOverlay
    data object Filter : VisitLogOverlay
}

@Immutable
data class VisitLogState(
    val selectedTab: VisitLogTab = VisitLogTab.Required,
    val requiredVisits: List<VisitUi> = emptyList(),
    val doneVisits: List<VisitUi> = emptyList(),
    /** Belongs to the selected tab: switching tab resets it to All. */
    val filter: VisitFilter = VisitFilter.All,
    val overlay: VisitLogOverlay = VisitLogOverlay.None,
) {
    val visibleVisits: List<VisitUi>
        get() = when (selectedTab) {
            VisitLogTab.Required -> requiredVisits
            VisitLogTab.Done -> doneVisits
        }.filter(filter.matches)

    val resultCount: Int get() = visibleVisits.size
    val isFiltered: Boolean get() = filter != VisitFilter.All
}
