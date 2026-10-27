package com.example.medicalrep.ui.screens.visitplanning.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.chrome.NexoraTabs
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.widgets.FilterMenu
import com.example.medicalrep.ui.components.widgets.ResultsFilterRow
import com.example.medicalrep.ui.components.widgets.plannedVisitFilterOptions
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningEvents
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningOverlay
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningState
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningTab
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 57:11724: pending approvals and plannable visits, with a schedule sheet. */
@Composable
fun VisitPlanningUi(
    state: VisitPlanningState,
    visits: LazyPagingItems<VisitUi>,
    onEvent: (VisitPlanningEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(bottomBar = {

        if (state.selectedTab == VisitPlanningTab.Visits && state.hasSelection) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dim.screenPaddingHorizontal)
                    .background(
                        MaterialTheme.colorScheme.background
                    )
            ) {
                NexoraButton(
                    text = stringResource(R.string.visit_planning_schedule_action),
                    onClick = { onEvent(VisitPlanningEvents.OpenSchedule) },
                )
            }

        }
    }) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
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
                            VisitPlanningTab.Rejected -> R.string.visit_planning_tab_rejected
                        },
                    )
                },
                selectedIndex = state.selectedTab.ordinal,
                onSelect = { onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.entries[it])) },
            )
            // A new list per tab: the scroll position and item animations of one tab do not leak into the next.
            key(state.selectedTab) {
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
                    items(count = visits.itemCount, key = visits.itemKey { it.id }) { index ->
                        visits[index]?.let { visit ->
                            when (state.selectedTab) {
                                VisitPlanningTab.Pending, VisitPlanningTab.Rejected -> PendingVisitCard(visit = visit, modifier = Modifier.animateItem())
                                VisitPlanningTab.Visits -> SelectableVisitCard(
                                    visit = visit,
                                    selected = visit.id in state.selectedIds,
                                    onToggle = { onEvent(VisitPlanningEvents.ToggleSelect(visit.id)) },
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        }
                    }

                }
            }
        }
        ScheduleVisitSheetHost(state = state, onEvent = onEvent)
    }

}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitPlanningUiPendingPreview() {
    MedicalRepTheme {
        VisitPlanningUi(
            state = VisitPlanningFakes.state().copy(selectedTab = VisitPlanningTab.Pending),
            visits = pagingItemsOf(VisitPlanningFakes.pendingVisits()),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitPlanningUiVisitsPreview() {
    MedicalRepTheme {
        val visits = VisitPlanningFakes.visits()
        VisitPlanningUi(
            state = VisitPlanningFakes.state().copy(selectedIds = setOf(visits.last().id)),
            visits = pagingItemsOf(visits),
            onEvent = {},
        )
    }
}
