package com.example.medicalrep.ui.screens.visitlog.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.VisitUi

enum class VisitLogTab { Required, Done }

@Immutable
data class VisitLogState(
    val selectedTab: VisitLogTab = VisitLogTab.Required,
    val requiredVisits: List<VisitUi> = emptyList(),
    val doneVisits: List<VisitUi> = emptyList(),
) {
    val visibleVisits: List<VisitUi>
        get() = when (selectedTab) {
            VisitLogTab.Required -> requiredVisits
            VisitLogTab.Done -> doneVisits
        }

    val resultCount: Int get() = visibleVisits.size
}
