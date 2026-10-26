package com.example.medicalrep.ui.screens.planvisit.account_location.components

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
import com.example.medicalrep.ui.screens.planvisit.account_location.main.AccountLocationEvents
import com.example.medicalrep.ui.screens.planvisit.account_location.main.AccountLocationState
import com.example.medicalrep.ui.screens.visit_location_map.components.LocationMapComponent
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** The account on a map with its details and a Get Directions button. */
@Composable
fun AccountLocationUi(
    state: AccountLocationState,
    onEvent: (AccountLocationEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppHeader(
            title = stringResource(R.string.visit_details_account_location),
            onBack = { onEvent(AccountLocationEvents.Back) },
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LocationMapComponent(
                userLocation = null,
                visitLocation = state.accountLocation,
                destinationTitle = state.accountName,
            )
            AccountDetailsCard(
                state = state,
                onGetDirections = { onEvent(AccountLocationEvents.GetDirections) },
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
private fun AccountLocationUiPreview() {
    MedicalRepTheme {
        AccountLocationUi(
            state = AccountLocationState(
                accountLocation = GeoPoint(latitude = 30.5657, longitude = 31.0043),
                accountName = "Al-Iman Pharmacy",
                address = "Shebin El-Kom - Stadium Square",
                classCategory = "C",
                accountType = "Medical Center",
                specialty = "Orthopedics",
            ),
            onEvent = {},
        )
    }
}
