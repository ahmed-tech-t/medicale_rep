package com.example.medicalrep.ui.screens.visitplanning.components

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
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.widgets.FilterMenu
import com.example.medicalrep.ui.components.widgets.plannedVisitFilterOptions
import com.example.medicalrep.ui.components.widgets.ResultsFilterRow
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningOverlay
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningEvents
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningState
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningTab
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 57:11724: pending approvals and plannable visits, with a schedule sheet. */
@Composable
fun VisitPlanningUi(
    state: VisitPlanningState,
    onEvent: (VisitPlanningEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(
            title = stringResource(R.string.visit_planning_title),
            onBack = { onEvent(VisitPlanningEvents.Back) },
        )
        NexoraTabs(
            labels = VisitPlanningTab.entries.map { tab ->
                stringResource(
                    when (tab) {
                        VisitPlanningTab.Visits -> R.string.visit_planning_tab_visits
                        VisitPlanningTab.Pending -> R.string.visit_planning_tab_pending
                    },
                )
            },
            selectedIndex = state.selectedTab.ordinal,
            onSelect = { onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.entries[it])) },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            item(key = "results") {
                ResultsFilterRow(
                    count = state.resultCount,
                    onFilterClick = { onEvent(VisitPlanningEvents.OpenFilter) },
                    isFiltered = state.isFiltered,
                    filterMenu = {
                        FilterMenu(
                            expanded = state.overlay == VisitPlanningOverlay.Filter,
                            options = plannedVisitFilterOptions,
                            selected = state.filter,
                            onSelect = { onEvent(VisitPlanningEvents.SelectFilter(it)) },
                            onDismiss = { onEvent(VisitPlanningEvents.DismissOverlay) },
                        )
                    },
                )
            }
            items(items = state.visibleVisits, key = { it.id }) { visit ->
                when (state.selectedTab) {
                    VisitPlanningTab.Pending -> PendingVisitCard(visit = visit)
                    VisitPlanningTab.Visits -> SelectableVisitCard(
                        visit = visit,
                        selected = visit.id in state.selectedIds,
                        onToggle = { onEvent(VisitPlanningEvents.ToggleSelect(visit.id)) },
                    )
                }
            }
            if (state.selectedTab == VisitPlanningTab.Visits && state.hasSelection) {
                item(key = "schedule") {
                    NexoraButton(
                        text = stringResource(R.string.visit_planning_schedule_action),
                        onClick = { onEvent(VisitPlanningEvents.OpenSchedule) },
                    )
                }
            }
        }
    }
    ScheduleVisitSheetHost(state = state, onEvent = onEvent)
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitPlanningUiPendingPreview() {
    MedicalRepTheme { VisitPlanningUi(state = VisitPlanningFakes.state(), onEvent = {}) }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitPlanningUiVisitsPreview() {
    MedicalRepTheme {
        val base = VisitPlanningFakes.state()
        VisitPlanningUi(
            state = base.copy(selectedTab = VisitPlanningTab.Visits, selectedIds = setOf(base.visits.last().id)),
            onEvent = {},
        )
    }
}
