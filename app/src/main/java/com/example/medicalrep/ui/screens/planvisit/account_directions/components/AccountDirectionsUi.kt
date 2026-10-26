package com.example.medicalrep.ui.screens.planvisit.account_directions.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.screens.planvisit.account_directions.main.AccountDirectionsEvents
import com.example.medicalrep.ui.screens.planvisit.account_directions.main.AccountDirectionsState
import com.example.medicalrep.ui.screens.visit_location_map.components.LocationMapComponent
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** How the rep gets to the account: his position, the account, the route between them and a minimizable card. */
@Composable
fun AccountDirectionsUi(
    state: AccountDirectionsState,
    onEvent: (AccountDirectionsEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppHeader(
            title = stringResource(R.string.map_my_location),
            onBack = { onEvent(AccountDirectionsEvents.Back) },
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LocationMapComponent(
                userLocation = state.userLocation,
                visitLocation = state.accountLocation,
                routePoints = state.lineToAccount,
                showMyLocationDot = true,
                destinationTitle = state.accountName,
            )
            AccountLocationCard(
                state = state,
                onToggle = { onEvent(AccountDirectionsEvents.ToggleCard) },
                onRefresh = { onEvent(AccountDirectionsEvents.RefreshLocation) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(spacing.spaceMedium),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun AccountDirectionsUiPreview() {
    MedicalRepTheme {
        AccountDirectionsUi(
            state = AccountDirectionsState(
                accountLocation = GeoPoint(latitude = 30.1444, longitude = 31.2357),
                accountName = "عيادة د. محمود عبد الرحمن",
                userLocation = GeoPoint(latitude = 30.0444, longitude = 31.2357),
                userStreet = "Al Geish Street, Quesna",
                capturedAtMillis = 1_791_000_000_000L,
            ),
            onEvent = {},
        )
    }
}
