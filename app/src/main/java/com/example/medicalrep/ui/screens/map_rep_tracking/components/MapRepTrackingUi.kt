package com.example.medicalrep.ui.screens.map_rep_tracking.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.screens.map_rep_tracking.main.MapRepTrackingEvents
import com.example.medicalrep.ui.screens.map_rep_tracking.main.MapRepTrackingState
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.screens.map_rep_tracking.utils.MapRepTrackingFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** Figma 287:1247: the rep's route on a map, with the tapped visit's card at the bottom. */
@Composable
fun MapRepTrackingUi(
    state: MapRepTrackingState,
    onEvent: (MapRepTrackingEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visitMarkers = remember(state.visits) {
        state.visits.mapIndexed { index, visit ->
            TrackUserMapMarker(index, visit.visitLatitude, visit.visitLongitude, visit.id)
        }
    }
    Column(modifier = modifier.fillMaxSize()) {
        AppHeader(
            title = stringResource(R.string.map_rep_tracking_title),
            onBack = { onEvent(MapRepTrackingEvents.Back) },
        )
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMapComponent(
                visitss = visitMarkers,
                repTrack = state.routePoints,
                roadRoute = state.route.data.orEmpty(),
                onMarkerClick = { onEvent(MapRepTrackingEvents.MarkerClicked(it)) },
                selectedVisitId = state.selectedVisitId,
            )
            state.selectedVisit?.let { visit ->
                VisitCartComponent(
                    visit = visit,
                    onViewDetails = { onEvent(MapRepTrackingEvents.OpenVisitDetails(visit.id)) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(spacing.spaceMedium),
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun MapRepTrackingUiArabicPreview() {
    MedicalRepTheme { MapRepTrackingUi(state = MapRepTrackingFakes.state(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun MapRepTrackingUiEnglishPreview() {
    MedicalRepTheme { MapRepTrackingUi(state = MapRepTrackingFakes.state(), onEvent = {}) }
}
