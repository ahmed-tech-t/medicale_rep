package com.example.medicalrep.ui.screens.visit_location_map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.screens.visit_location_map.main.VisitLocationMapEvents
import com.example.medicalrep.ui.screens.visit_location_map.main.VisitLocationMapState
import com.example.medicalrep.ui.screens.visit_location_map.utils.VisitLocationMapFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** The rep's current location and the visit location on one map. */
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
        LocationMapComponent(
            userLocation = state.userLocation,
            visitLocation = state.visitLocation,
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun VisitLocationMapUiPreview() {
    MedicalRepTheme { VisitLocationMapUi(state = VisitLocationMapFakes.state(), onEvent = {}) }
}
