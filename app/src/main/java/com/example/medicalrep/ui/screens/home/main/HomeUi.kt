package com.example.medicalrep.ui.screens.home.main

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.DiscardPendingDialog
import com.example.medicalrep.ui.components.widgets.PendingVisitDialog
import com.example.medicalrep.ui.components.widgets.TwoPerRow
import com.example.medicalrep.ui.components.widgets.visit.PlannedVisitCard
import com.example.medicalrep.ui.preview.HomeSamples
import com.example.medicalrep.ui.screens.home.components.AddressDateRow
import com.example.medicalrep.ui.screens.home.components.HomeHeader
import com.example.medicalrep.ui.screens.home.components.QuickActionCard
import com.example.medicalrep.ui.screens.home.components.SyncComponent
import com.example.medicalrep.ui.screens.home.components.UnCompletedVisitCard
import com.example.medicalrep.ui.screens.home.components.WeekProgressCard
import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerSheet
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun HomeUi(
    state: HomeState,
    onEvent: (HomeEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentOnEvent by rememberUpdatedState(onEvent)

    when (val overlay = state.overlay) {
        HomeOverlay.None -> Unit
        is HomeOverlay.PendingVisit -> PendingVisitDialog(
            onConfirm = { onEvent(HomeEvents.OnPendingVisitConfirm) },
            onDismiss = { onEvent(HomeEvents.OnOverlayDismiss) },
        )
        HomeOverlay.DiscardPending -> DiscardPendingDialog(
            onConfirm = { onEvent(HomeEvents.OnDiscardPendingConfirm) },
            onDismiss = { onEvent(HomeEvents.OnOverlayDismiss) },
        )
    }

    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) drawerState.open() else drawerState.close()
    }
    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.currentValue }
            .distinctUntilChanged()
            .filter { it == DrawerValue.Closed }
            .collect { currentOnEvent(HomeEvents.OnDrawerDismiss) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawerSheet(
                user = state.user,
                language = state.language,
                onItemClick = { onEvent(HomeEvents.OnDrawerItem(it)) },
            )
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate50)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HomeHeader(
                    user = state.user,
                    connection = state.connection,
                    onMenuClick = { onEvent(HomeEvents.OnMenuClick) },
                    modifier = Modifier.testTag(HomeTestTags.Header),
                )
                HomeList(state = state, onEvent = onEvent)
            }
        }
    }
}

/** One shortcut of the grid: its text and what tapping it asks for. */
private data class QuickAction(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val event: HomeEvents,
    val primary: Boolean = false,
)

private val QuickActions = listOf(
    QuickAction(R.string.home_add_visit, R.string.home_add_visit_hint, HomeEvents.OnAddVisitClick, primary = true),
    QuickAction(R.string.home_plan_visits, R.string.home_plan_visits_hint, HomeEvents.OnPlanVisitsClick),
    QuickAction(R.string.home_samples_gifts, R.string.home_samples_gifts_hint, HomeEvents.OnSamplesAndGiftsClick),
    QuickAction(R.string.home_accounts, R.string.home_accounts_hint, HomeEvents.OnAccountsClick),
)

@Composable
private fun HomeList(
    state: HomeState,
    onEvent: (HomeEvents) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag(HomeTestTags.List),
        contentPadding = PaddingValues(spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {
        item(key = "address-date") { AddressDateRow(address = state.address, dateMillis = state.dateMillis) }

        item(key = "progress") { WeekProgressCard(progress = state.progress) }

        item(key = "actions") {
            TwoPerRow(items = QuickActions) { action, cardModifier ->
                QuickActionCard(
                    title = stringResource(action.title),
                    subtitle = stringResource(action.subtitle),
                    onClick = { onEvent(action.event) },
                    primary = action.primary,
                    modifier = if (action.primary) cardModifier.testTag(HomeTestTags.AddVisit) else cardModifier,
                )
            }
        }

        item(key = "today-header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NexoraText(
                    text = stringResource(R.string.home_today_visits),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Ink,
                )
                NexoraText(
                    text = stringResource(R.string.home_more),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Brand,
                    modifier = Modifier.clickable { onEvent(HomeEvents.OnTodayMoreClick) },
                )
            }
        }

        state.activeVisit?.let { visit ->
            item(key = "active-${visit.id}") {
                UnCompletedVisitCard(
                    model = visit,
                    onPrimaryAction = { onEvent(HomeEvents.OnActiveContinue) },
                    onCancel = { onEvent(HomeEvents.OnActiveCancel) },
                )
            }
        }
        if (state.todayVisits.isEmpty()) {
            item(key = "today-empty") {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    NexoraText(text = stringResource(R.string.no_visits_today))
                }
            }
        } else {
            items(items = state.todayVisits, key = { "today-${it.id}" }) { visit ->
                PlannedVisitCard(
                    model = visit,
                    onAction = { onEvent(HomeEvents.OnStartVisit(visit.id)) },
                    modifier = Modifier.testTag(HomeTestTags.TodayVisit),
                )
            }
        }
    }
}

object HomeTestTags {
    const val Header = "home_header"
    const val List = "home_list"
    const val TodayVisit = "home_today_visit"
    const val AddVisit = "home_add_visit"
}

@Preview(showBackground = true, locale = "ar", widthDp = 374, heightDp = 1170)
@Composable
private fun HomeUiPreview() {
    MedicalRepTheme {
        HomeUi(state = HomeSamples.homeState(), onEvent = {})
    }
}

@Preview(showBackground = true, widthDp = 374, heightDp = 800)
@Composable
private fun HomeUiEmptyDayPreview() {
    MedicalRepTheme {
        HomeUi(
            state = HomeSamples.homeState(language = "en").copy(
                activeVisit = null,
                todayVisits = emptyList(),
                toSyncCount = 0,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, locale = "ar", widthDp = 800, heightDp = 900)
@Composable
private fun HomeUiOfflineTabletPreview() {
    MedicalRepTheme {
        HomeUi(
            state = HomeSamples.homeState().copy(connection = ConnectionState.Offline),
            onEvent = {},
        )
    }
}
