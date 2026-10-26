package com.example.medicalrep.ui.screens.planvisit.components.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.screens.planvisit.components.StepCardTitle
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.screens.visitdetails.components.LocationMatchBanner
import com.example.medicalrep.ui.screens.visitdetails.utils.distanceFromAccountMeters
import com.example.medicalrep.ui.screens.visitdetails.utils.hasLocations
import com.example.medicalrep.ui.screens.visitdetails.utils.hasVisitLocation
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.utils.formatClockTime
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate800

/** Where the rep captured the visit: address, coordinates, distance from the account, map and refresh. */
@Composable
fun YourLocationCard(
    state: PlanVisitState,
    onViewOnMap: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visit = state.visit
    StepCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepCardTitle(stringResource(R.string.plan_visit_your_location))
            state.capturedAtMillis?.let { millis ->
                val time = remember(millis) { formatClockTime(millis) }
                NexoraChip(
                    text = time,
                    container = Slate100,
                    content = Slate600,
                )
            }
        }
        if (visit.hasVisitLocation) {

            Column {
                state.locationAddress?.let {
                    NexoraText(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate800,
                    )
                }
                NexoraText(
                    text = stringResource(
                        R.string.plan_visit_coordinates,
                        visit.visitLatitude,
                        visit.visitLongitude
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                )
            }

        }
        if (visit.hasLocations) LocationMatchBanner(distanceMeters = visit.distanceFromAccountMeters)
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
            NexoraButton(
                text = stringResource(R.string.plan_visit_view_on_map),
                onClick = onViewOnMap,
                fillMaxWidth = false,
                modifier = Modifier.weight(2f),
                leadingIcon = {
                    NexoraIcon(
                        imageVector = Icons.Outlined.Map,
                        modifier = Modifier.size(dim.smallIconSize)
                    )
                },
            )
            NexoraButton(
                text = stringResource(R.string.plan_visit_refresh_location),
                onClick = onRefresh,
                variant = NexoraButtonVariant.Muted,
                enabled = !state.isLocating,
                fillMaxWidth = false,
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    NexoraIcon(
                        imageVector = Icons.Filled.Refresh,
                        modifier = Modifier.size(dim.smallIconSize)
                    )
                },
            )
        }
    }
}

private fun sampleState() = PlanVisitFakes.state().let {
    it.copy(
        visit = it.visit.copy(visitLatitude = 30.547864, visitLongitude = 31.133548),
        capturedAtMillis = 1_791_000_000_000L,
        locationAddress = "Al Geish Street",
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun YourLocationCardPreview() {
    MedicalRepTheme {
        YourLocationCard(
            state = sampleState(),
            onViewOnMap = {},
            onRefresh = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun YourLocationCardNoAddressPreview() {
    MedicalRepTheme {
        YourLocationCard(
            state = sampleState().copy(locationAddress = null, isLocating = true),
            onViewOnMap = {},
            onRefresh = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
