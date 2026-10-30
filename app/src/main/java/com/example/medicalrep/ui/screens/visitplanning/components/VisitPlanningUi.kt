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
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.chrome.NexoraTabs
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.widgets.EmptyContent
import com.example.medicalrep.ui.components.widgets.SearchFilterRow
import com.example.medicalrep.ui.components.widgets.StatusChipRow
import com.example.medicalrep.ui.components.widgets.filter.FilterComponent
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningEvents
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningOverlay
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningState
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningTab
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** The answers the Pending chips offer, in the order of Figma 591:2917; the first one lists every request. */
private val approvalChips = listOf<ApprovalStatus?>(null, ApprovalStatus.Pending, ApprovalStatus.Approved, ApprovalStatus.Rejected)

/** Figma 591:2917 (pending approval) and 591:3150 (visits to plan): requests by answer, and visits with their days. */
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
                    .background(MaterialTheme.colorScheme.background),
            ) {
                NexoraButton(
                    text = stringResource(R.string.visit_planning_schedule_action),
                    onClick = { onEvent(VisitPlanningEvents.ScheduleSelectedDays) },
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
                        },
                    )
                },
                selectedIndex = state.selectedTab.ordinal,
                onSelect = { onEvent(VisitPlanningEvents.SelectTab(VisitPlanningTab.entries[it])) },
            )
            // A new list per tab: the scroll position and item animations of one tab do not leak into the next.
            key(state.selectedTab) {
                when (state.selectedTab) {
                    VisitPlanningTab.Pending -> PendingTabContent(state, visits, onEvent)
                    VisitPlanningTab.Visits -> VisitsTabContent(state, visits, onEvent)
                }
            }
        }
        ScheduleVisitSheetHost(state = state, onEvent = onEvent)
        if (state.selectedTab == VisitPlanningTab.Visits && state.overlay == VisitPlanningOverlay.Filter) {
            FilterComponent(
                state = state.sheet,
                onEvent = { onEvent(VisitPlanningEvents.OnFilterEvent(it)) },
                onApplyFilter = { onEvent(VisitPlanningEvents.ApplyFilter) },
                onDismissRequest = { onEvent(VisitPlanningEvents.DismissOverlay) },
                title = stringResource(R.string.filter_visits_planning_title),
                showAccountType = false,
                showSyncStatus = false,
                showVisitType = false,
            )
        }
    }
}

@Composable
private fun PendingTabContent(
    state: VisitPlanningState,
    visits: LazyPagingItems<VisitUi>,
    onEvent: (VisitPlanningEvents) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StatusChipRow(
            labels = approvalChips.map { stringResource(approvalChipLabel(it)) },
            selectedIndex = approvalChips.indexOf(state.approval),
            selectedCount = state.resultCount,
            onSelect = { onEvent(VisitPlanningEvents.SelectApproval(approvalChips[it])) },
            contentPadding = PaddingValues(horizontal = dim.screenPaddingHorizontal, vertical = spacing.spaceSmallPlus),
        )
        if (state.resultCount == 0) EmptyContent()
        else LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = dim.screenPaddingHorizontal, vertical = spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            items(count = visits.itemCount, key = visits.itemKey { it.id }) { index ->
                visits[index]?.let { visit ->
                    PendingVisitCard(
                        visit = visit,
                        onPickAnotherTime = { onEvent(VisitPlanningEvents.PickAnotherTime(visit)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun VisitsTabContent(
    state: VisitPlanningState,
    visits: LazyPagingItems<VisitUi>,
    onEvent: (VisitPlanningEvents) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchFilterRow(
            modifier = Modifier.padding(horizontal = dim.screenPaddingHorizontal, vertical = spacing.spaceSmallPlus),
            query = state.query,
            onQueryChange = { onEvent(VisitPlanningEvents.OnSearchChange(it)) },
            onFilterClick = { onEvent(VisitPlanningEvents.OpenFilter) },
            placeholder = stringResource(R.string.visit_planning_search_hint),
            isFiltered = state.isFiltered,
        )
        if (state.resultCount == 0) EmptyContent()
        else LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = dim.screenPaddingHorizontal, vertical = spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            items(count = visits.itemCount, key = visits.itemKey { it.id }) { index ->
                visits[index]?.let { visit ->
                    PlanVisitCard(
                        visit = visit,
                        days = state.days,
                        selectedDays = state.selectedDays[visit.id].orEmpty(),
                        bookedDays = state.bookedDays[visit.accountId].orEmpty(),
                        onToggleDay = { onEvent(VisitPlanningEvents.ToggleDay(visit.id, it)) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

private fun approvalChipLabel(approval: ApprovalStatus?): Int = when (approval) {
    null -> R.string.filter_all
    ApprovalStatus.Pending -> R.string.request_status_pending
    ApprovalStatus.Approved -> R.string.request_status_approved
    ApprovalStatus.Rejected -> R.string.request_status_rejected
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
        val state = VisitPlanningFakes.state()
        VisitPlanningUi(
            state = state.copy(selectedDays = mapOf(visits.last().id to setOf(state.days[1].millis))),
            visits = pagingItemsOf(visits),
            onEvent = {},
        )
    }
}
