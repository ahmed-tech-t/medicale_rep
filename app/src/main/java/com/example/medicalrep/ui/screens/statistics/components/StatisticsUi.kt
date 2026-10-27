package com.example.medicalrep.ui.screens.statistics.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.TwoPerRow
import com.example.medicalrep.domain.models.Statistics
import com.example.medicalrep.domain.models.StockCount
import com.example.medicalrep.ui.screens.statistics.main.StatisticsEvents
import com.example.medicalrep.ui.screens.statistics.main.StatisticsState
import com.example.medicalrep.ui.screens.statistics.utils.StatisticsFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** The week's visits, the stock of samples, gifts and products, and the accounts by class. */
@Composable
fun StatisticsUi(
    state: StatisticsState,
    onEvent: (StatisticsEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(title = stringResource(R.string.nav_statics), onBack = { onEvent(StatisticsEvents.Back) })
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            Section(R.string.statistics_section_week_visits)
            VisitCards(state.statistics)

            Section(R.string.statistics_section_stock)
            StockCards(state.statistics)

            Section(R.string.statistics_section_classes)
            ClassDistributionCard(statistics = state.statistics)
        }
    }
}

@Composable
private fun VisitCards(statistics: Statistics) {
    val doctors = stringResource(R.string.statistics_doctors)
    val clinics = stringResource(R.string.statistics_clinics)
    val hospitals = stringResource(R.string.statistics_hospitals)
    val pharmacies = stringResource(R.string.statistics_pharmacies)
    val medicalCenters = stringResource(R.string.statistics_medical_centers)
    val completed = stringResource(R.string.statistics_completed_visits)
    val cards = remember(statistics, doctors, clinics, hospitals, pharmacies, medicalCenters, completed) {
        listOf(
            StatItem(completed, "${statistics.completedVisits} / ${statistics.plannedVisits}"),
            StatItem(doctors, statistics.doctors.toString()),
            StatItem(clinics, statistics.clinics.toString()),
            StatItem(hospitals, statistics.hospitals.toString()),
            StatItem(pharmacies, statistics.pharmacies.toString()),
            StatItem(medicalCenters, statistics.medicalCenters.toString()),
        )
    }
    TwoPerRow(items = cards) { item, cardModifier -> StatCard(item = item, modifier = cardModifier) }
}

@Composable
private fun StockCards(statistics: Statistics) {
    val received = stringResource(R.string.statistics_received)
    val total = stringResource(R.string.statistics_total)
    val used = stringResource(R.string.statistics_used)
    val remaining = stringResource(R.string.statistics_remaining)
    val sold = stringResource(R.string.statistics_sold)
    val samples = stringResource(R.string.statistics_samples)
    val gifts = stringResource(R.string.statistics_gifts)
    val products = stringResource(R.string.statistics_products)
    val cards = remember(statistics, received, total, used, remaining, sold, samples, gifts, products) {
        fun columns(stock: StockCount, totalLabel: String, usedLabel: String) = listOf(
            StatColumn(totalLabel, stock.total), StatColumn(usedLabel, stock.used), StatColumn(remaining, stock.remaining),
        )
        listOf(
            samples to columns(statistics.samples, received, used),
            gifts to columns(statistics.gifts, received, used),
            products to columns(statistics.products, total, sold),
        )
    }
    TwoPerRow(items = cards) { (title, stock), cardModifier -> StatBreakdownCard(title = title, columns = stock, modifier = cardModifier) }
}

@Composable
private fun Section(@StringRes titleRes: Int) {
    NexoraText(
        text = stringResource(titleRes),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
        color = Ink,
        modifier = Modifier.padding(top = spacing.spaceSmall),
    )
}

@Preview(showBackground = true, locale = "ar", heightDp = 1100)
@Composable
private fun StatisticsUiPreview() {
    MedicalRepTheme { StatisticsUi(state = StatisticsFakes.state(), onEvent = {}) }
}
