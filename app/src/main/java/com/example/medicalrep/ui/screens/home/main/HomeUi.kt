package com.example.medicalrep.ui.screens.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.visit.PlannedVisitCard
import com.example.medicalrep.ui.models.StaticsMetricUi
import com.example.medicalrep.ui.preview.HomeSamples
import com.example.medicalrep.ui.screens.home.components.HomeHeader
import com.example.medicalrep.ui.screens.home.components.StaticsGiftsSummaryCard
import com.example.medicalrep.ui.screens.home.components.StaticsMetricCard
import com.example.medicalrep.ui.screens.home.components.SyncComponent
import com.example.medicalrep.ui.screens.home.components.UnCompletedVisitCard
import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerSheet
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.min

private const val TodayVisitsLimit = 3

@Composable
fun HomeUi(
    state: HomeState,
    onEvent: (HomeEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentOnEvent by rememberUpdatedState(onEvent)

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
            AddVisitButton(
                onClick = { onEvent(HomeEvents.OnAddVisitClick) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(spacing.spaceMediumPlus)
                    .testTag(HomeTestTags.AddVisit),
            )
        }
    }
}

@Composable
private fun HomeList(
    state: HomeState,
    onEvent: (HomeEvents) -> Unit,
) {
    val metricRows = remember(state.visitMetrics) { state.visitMetrics.chunked(2) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag(HomeTestTags.List),
        contentPadding = PaddingValues(
            start = spacing.spaceMedium,
            end = spacing.spaceMedium,
            top = spacing.spaceMedium,
            bottom = spacing.spaceExtraLarge + spacing.spaceLarge,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        if (state.shouldSync) {
            item(key = "sync") {
                SyncComponent(
                    syncItemsCount = state.toSyncVisits.size,
                    onSync = { onEvent(HomeEvents.OnSyncClick) },
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

        item(key = "today-header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NexoraText(
                    text = stringResource(R.string.home_today_visits),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                )
                if (state.todayVisits.isNotEmpty()) {
                    NexoraText(
                        text = stringResource(R.string.home_more),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Brand,
                        modifier = Modifier.clickable { onEvent(HomeEvents.OnTodayMoreClick) },
                    )
                }
            }
        }

        if (state.todayVisits.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    NexoraText(text = stringResource(R.string.no_visits_today))
                }
            }
        } else {
            items(
                items = state.todayVisits.take(min(TodayVisitsLimit, state.todayVisits.size)),
                key = { "today-${it.id}" }) { visit ->
                PlannedVisitCard(
                    model = visit,
                    onAction = { onEvent(HomeEvents.OnStartVisit(visit.id)) },
                    modifier = Modifier.testTag(HomeTestTags.TodayVisit),
                )
            }
        }

        if (metricRows.isNotEmpty()) {
            item(key = "stats-title") {
                SectionTitle(text = stringResource(R.string.home_section_visit_stats))
            }
            items(
                items = metricRows,
                key = { row -> "stats-${row.joinToString { it.labelRes.toString() }}" }) { row ->
                MetricRow(metrics = row)
            }

        }
        if (state.custodySummary.isNotEmpty()) {
            item(key = "custody-title") {
                SectionTitle(text = stringResource(R.string.statics_section_custody))
            }
            item(key = "custody") {
                StaticsGiftsSummaryCard(items = state.custodySummary)
            }
        }

    }
}

@Composable
private fun MetricRow(metrics: List<StaticsMetricUi>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        metrics.forEach { metric ->
            StaticsMetricCard(metric = metric, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    NexoraText(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = Slate500,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacing.spaceSmall),
    )
}

@Composable
private fun AddVisitButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(radii.large),
        containerColor = Brand,
        contentColor = Slate50,
        icon = {
            NexoraIcon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Slate50
            )
        },
        text = {
            NexoraText(
                text = stringResource(R.string.home_add_visit),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Slate50,
            )
        },
    )
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
                toSyncVisits = emptyList(),
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
