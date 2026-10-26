package com.example.medicalrep.ui.screens.visit_location_map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.widgets.LocationMatchBanner
import com.example.medicalrep.ui.components.widgets.TravelSegmentBadge
import com.example.medicalrep.ui.screens.visit_location_map.main.VisitLocationMapEvents
import com.example.medicalrep.ui.screens.visit_location_map.main.VisitLocationMapState
import com.example.medicalrep.ui.screens.visit_location_map.utils.VisitLocationMapFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** Where the rep did the visit and the account on one map, with the line between them and how he travelled. */
@Composable
fun VisitLocationMapUi(
    state: VisitLocationMapState,
    onEvent: (VisitLocationMapEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppHeader(
            title = stringResource(R.string.visit_location_map_title),
            onBack = { onEvent(VisitLocationMapEvents.Back) },
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LocationMapComponent(
                userLocation = state.visitLocation,
                visitLocation = state.accountLocation,
                routePoints = state.line,
                destinationTitle = stringResource(R.string.visit_details_account_location),
                badge = if (state.showTravel) {
                    { TravelSegmentBadge(mode = state.mode, seconds = state.travelSeconds) }
                } else null,
            )
            NexoraCard(
                borderColor = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(spacing.spaceMedium),
            ) {
                LocationMatchBanner(distanceMeters = state.distanceMeters)
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitLocationMapUiPreview() {
    MedicalRepTheme { VisitLocationMapUi(state = VisitLocationMapFakes.state(), onEvent = {}) }
}
