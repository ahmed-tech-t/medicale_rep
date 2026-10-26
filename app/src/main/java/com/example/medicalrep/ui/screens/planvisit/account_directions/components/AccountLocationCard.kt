package com.example.medicalrep.ui.screens.planvisit.account_directions.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.screens.planvisit.account_directions.main.AccountDirectionsState
import com.example.medicalrep.ui.screens.planvisit.account_directions.utils.distanceText
import com.example.medicalrep.ui.components.widgets.LocationMatchBanner
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate900
import com.example.medicalrep.ui.utils.durationText
import com.example.medicalrep.ui.utils.formatClockTime

/** Where the rep is, how far the account is and how long it takes; the chevron minimizes the card to its header. */
@Composable
fun AccountLocationCard(
    state: AccountDirectionsState,
    onToggle: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NexoraCard(modifier = modifier, borderColor = Color.Unspecified) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NexoraText(
                    text = stringResource(R.string.plan_visit_your_location),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Slate900,
                )
                state.userStreet?.let {
                    NexoraText(text = it, style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
            }
            state.capturedAtMillis?.let { millis ->
                val time = remember(millis) { formatClockTime(millis) }
                NexoraChip(
                    text = time,
                    container = Slate100,
                    content = Slate600,
                )
            }
            NexoraIconButton(
                imageVector = if (state.isCardExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(if (state.isCardExpanded) R.string.cd_collapse else R.string.cd_expand),
                onClick = onToggle,
            )
        }
        AnimatedVisibility(visible = state.isCardExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
                state.distanceToAccountMeters?.let { LocationMatchBanner(distanceMeters = it) }
                state.travelDistanceMeters?.let { meters ->
                    NexoraText(
                        text = stringResource(R.string.account_directions_to_account),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900,
                    )
                    val distance = distanceText(meters)
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
                        state.carSeconds?.let {
                            TravelTimeChip(
                                iconRes = R.drawable.ic_car,
                                duration = durationText(it),
                                detail = stringResource(R.string.account_directions_by_car) + " · " + distance,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        state.walkingSeconds?.let {
                            TravelTimeChip(
                                iconRes = R.drawable.ic_walk,
                                duration = durationText(it),
                                detail = stringResource(R.string.account_directions_on_foot) + " · " + distance,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
                NexoraButton(
                    text = stringResource(R.string.account_directions_refresh),
                    onClick = onRefresh,
                    variant = NexoraButtonVariant.Secondary,
                    enabled = !state.isLocating,
                    leadingIcon = { NexoraIcon(imageVector = Icons.Filled.Refresh) },
                )
            }
        }
    }
}

private fun sampleState() = AccountDirectionsState(
    accountLocation = MapCameraTarget(latitude = 30.1444, longitude = 31.2357),
    accountName = "عيادة د. محمود عبد الرحمن",
    userLocation = MapCameraTarget(latitude = 30.0444, longitude = 31.2357),
    userStreet = "Al Geish Street, Quesna",
    capturedAtMillis = 1_791_000_000_000L,
)

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AccountLocationCardExpandedPreview() {
    MedicalRepTheme { AccountLocationCard(state = sampleState(), onToggle = {}, onRefresh = {}) }
}

@Preview(showBackground = true)
@Composable
private fun AccountLocationCardCollapsedPreview() {
    MedicalRepTheme {
        AccountLocationCard(state = sampleState().copy(isCardExpanded = false), onToggle = {}, onRefresh = {})
    }
}
