package com.example.medicalrep.ui.screens.reptracking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.DateField
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.screens.home.components.StaticsMetricCard
import com.example.medicalrep.ui.screens.reptracking.main.RepTrackingEvents
import com.example.medicalrep.ui.screens.reptracking.main.RepTrackingState
import com.example.medicalrep.ui.screens.reptracking.utils.RepTrackingFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

/** Figma 287:1287: rep selector, period filters, performance summary and the executed visits log. */
@Composable
fun RepTrackingUi(
    state: RepTrackingState,
    onEvent: (RepTrackingEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPopup by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(
            title = stringResource(R.string.rep_tracking_title),
            onBack = { onEvent(RepTrackingEvents.Back) },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
        ) {
            SearchFieldComponent(
                value = state.selectedRep,
                options = state.reps,
                onSelect = {
                    onEvent(RepTrackingEvents.SelectRep(it))
                    showPopup = false
                },
                placeholder = stringResource(R.string.rep_tracking_change),
                showPopup = showPopup,
                onClick = { showPopup = !showPopup }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
                DateField(
                    dateMillis = state.fromDateMillis,
                    onDateChange = { onEvent(RepTrackingEvents.FromDateChanged(it)) },
                    prefix = stringResource(R.string.date_from_label),
                    modifier = Modifier.weight(1f),
                )
                DateField(
                    dateMillis = state.toDateMillis,
                    onDateChange = { onEvent(RepTrackingEvents.ToDateChanged(it)) },
                    prefix = stringResource(R.string.date_to_label),
                    modifier = Modifier.weight(1f),
                )
            }

            SectionLabel(stringResource(R.string.rep_tracking_period_summary))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
                StaticsMetricCard(
                    label = stringResource(R.string.rep_tracking_total_visits),
                    value = state.visitsCount,
                    modifier = Modifier.weight(1f),
                )
                StaticsMetricCard(
                    label = stringResource(R.string.rep_tracking_products),
                    value = state.productsCount,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
                StaticsMetricCard(
                    label = stringResource(R.string.rep_tracking_gifts),
                    value = state.giftsCount,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            NexoraButton(
                text = stringResource(R.string.rep_tracking_view_route),
                onClick = { onEvent(RepTrackingEvents.ViewRoute) },
            )

            SectionLabel(stringResource(R.string.rep_tracking_log_title))
            state.visits.forEach { visit ->
                RepVisitCard(
                    visit = visit,
                    onClick = { onEvent(RepTrackingEvents.OpenVisit(visit.id)) })
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    NexoraText(
        text = text, style = MaterialTheme.typography.labelMedium.copy(
            fontSize = 12.sp, fontWeight = FontWeight.Bold
        ), color = Slate500
    )
}

@Preview(showBackground = true, locale = "ar", heightDp = 900)
@Composable
private fun RepTrackingUiArabicPreview() {
    MedicalRepTheme { RepTrackingUi(state = RepTrackingFakes.state(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun RepTrackingUiEnglishPreview() {
    MedicalRepTheme { RepTrackingUi(state = RepTrackingFakes.state(), onEvent = {}) }
}
