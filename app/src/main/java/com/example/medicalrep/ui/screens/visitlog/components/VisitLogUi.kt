package com.example.medicalrep.ui.screens.visitlog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.chrome.NexoraTabs
import com.example.medicalrep.ui.components.widgets.visit.CompletedVisitCard
import com.example.medicalrep.ui.components.widgets.visit.PlannedVisitCard
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogEvents
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogState
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogTab
import com.example.medicalrep.ui.screens.visitlog.utils.VisitLogFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 403:520 / 403:543: visit log with Required and Done tabs. */
@Composable
fun VisitLogUi(
    state: VisitLogState,
    onEvent: (VisitLogEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(
            title = stringResource(R.string.visit_log_title),
            onBack = { onEvent(VisitLogEvents.Back) },
        )
        NexoraTabs(
            labels = VisitLogTab.entries.map { tab ->
                stringResource(
                    when (tab) {
                        VisitLogTab.Required -> R.string.visit_log_tab_required
                        VisitLogTab.Done -> R.string.visit_log_tab_done
                    },
                )
            },
            selectedIndex = state.selectedTab.ordinal,
            onSelect = { onEvent(VisitLogEvents.SelectTab(VisitLogTab.entries[it])) },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            item(key = "results") {
                VisitLogResultsRow(
                    count = state.resultCount,
                    onFilterClick = { onEvent(VisitLogEvents.FilterClick) },
                )
            }
            items(items = state.visibleVisits, key = { it.id }) { visit ->
                VisitLogItem(state.selectedTab, visit, onEvent)
            }
        }
    }
}

@Composable
private fun VisitLogItem(tab: VisitLogTab, visit: VisitUi, onEvent: (VisitLogEvents) -> Unit) {
    when (tab) {
        VisitLogTab.Required -> PlannedVisitCard(
            model = visit,
            onAction = { onEvent(VisitLogEvents.StartVisit(visit.id)) },
        )

        VisitLogTab.Done -> CompletedVisitCard(model = visit)
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitLogUiRequiredPreview() {
    MedicalRepTheme { VisitLogUi(state = VisitLogFakes.state(), onEvent = {}) }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitLogUiDonePreview() {
    MedicalRepTheme {
        VisitLogUi(state = VisitLogFakes.state().copy(selectedTab = VisitLogTab.Done), onEvent = {})
    }
}
