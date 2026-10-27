package com.example.medicalrep.ui.screens.visitlog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.chrome.NexoraTabs
import com.example.medicalrep.ui.components.widgets.FilterMenu
import com.example.medicalrep.ui.components.widgets.PendingVisitDialog
import com.example.medicalrep.ui.components.widgets.ResultsFilterRow
import com.example.medicalrep.ui.components.widgets.doneVisitFilterOptions
import com.example.medicalrep.ui.components.widgets.plannedVisitFilterOptions
import com.example.medicalrep.ui.components.widgets.visit.CompletedVisitCard
import com.example.medicalrep.ui.components.widgets.visit.PlannedVisitCard
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogEvents
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogOverlay
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogState
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogTab
import com.example.medicalrep.ui.screens.visitlog.utils.VisitLogFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 403:520 / 403:543: visit log with Required and Done tabs. */
@Composable
fun VisitLogUi(
    state: VisitLogState,
    visits: LazyPagingItems<VisitUi>,
    onEvent: (VisitLogEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(
            title = stringResource(R.string.drawer_my_visits),
            onBack = { onEvent(VisitLogEvents.Back) },
        )
        NexoraTabs(
            labels = VisitLogTab.entries.map { tab ->
                stringResource(
                    when (tab) {
                        VisitLogTab.Required -> R.string.visit_log_tab_required
                        VisitLogTab.Done -> R.string.visit_log_tab_done
                        VisitLogTab.Missed -> R.string.visit_log_tab_missed
                    },
                )
            },
            selectedIndex = state.selectedTab.ordinal,
            onSelect = { onEvent(VisitLogEvents.SelectTab(VisitLogTab.entries[it])) },
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
                        onFilterClick = { onEvent(VisitLogEvents.OpenFilter) },
                        isFiltered = state.isFiltered,
                        filterMenu = {
                            FilterMenu(
                                expanded = state.overlay == VisitLogOverlay.Filter,
                                options = when (state.selectedTab) {
                                    VisitLogTab.Required, VisitLogTab.Missed -> plannedVisitFilterOptions
                                    VisitLogTab.Done -> doneVisitFilterOptions
                                },
                                selected = state.filter,
                                onSelect = { onEvent(VisitLogEvents.SelectFilter(it)) },
                                onDismiss = { onEvent(VisitLogEvents.DismissOverlay) },
                            )
                        },
                    )
                }

                items(count = visits.itemCount, key = visits.itemKey { it.id }) { index ->
                    visits[index]?.let {
                        VisitLogItem(
                            state.selectedTab,
                            it,
                            onEvent,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }


    if (state.overlay is VisitLogOverlay.PendingVisit) {
        PendingVisitDialog(
            onConfirm = { onEvent(VisitLogEvents.ConfirmPending) },
            onDismiss = { onEvent(VisitLogEvents.DismissOverlay) },
        )
    }
}

@Composable
private fun VisitLogItem(
    tab: VisitLogTab,
    visit: VisitUi,
    onEvent: (VisitLogEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    when (tab) {
        VisitLogTab.Required -> PlannedVisitCard(
            model = visit,
            onAction = { onEvent(VisitLogEvents.StartVisit(visit.id)) },
            modifier = modifier,
        )

        VisitLogTab.Done -> CompletedVisitCard(
            model = visit,
            onClick = { onEvent(VisitLogEvents.OpenVisit(visit.id)) },
            modifier = modifier,
        )

        VisitLogTab.Missed -> PlannedVisitCard(
            model = visit,
            onAction = null,
            onClick = { onEvent(VisitLogEvents.OpenVisit(visit.id)) },
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitLogUiRequiredPreview() {
    MedicalRepTheme {
        VisitLogUi(
            state = VisitLogFakes.state(),
            visits = pagingItemsOf(VisitLogFakes.requiredVisits()),
            onEvent = {})
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitLogUiDonePreview() {
    MedicalRepTheme {
        VisitLogUi(
            state = VisitLogFakes.state().copy(selectedTab = VisitLogTab.Done),
            visits = pagingItemsOf(VisitLogFakes.doneVisits()),
            onEvent = {},
        )
    }
}
