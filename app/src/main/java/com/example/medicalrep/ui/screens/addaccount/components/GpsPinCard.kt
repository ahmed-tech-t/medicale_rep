package com.example.medicalrep.ui.screens.addaccount.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountEvents
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountState
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandBorder
import com.example.medicalrep.ui.ui.theme.BrandSoft
import com.example.medicalrep.ui.ui.theme.BrandTint

/** Tap the map to set the clinic location; the chip shows the pinned coordinates or the hint. */
@Composable
internal fun GpsPinCard(state: AddAccountState, onEvent: (AddAccountEvents) -> Unit) {
    StepCard {
        NexoraText(
            text = stringResource(R.string.add_account_pin_title),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = BrandSoft,
        )
        PinPickerMap(
            userLocation = state.userLocation,
            pin = state.pin,
            onPinPlaced = { latitude, longitude -> onEvent(AddAccountEvents.PinPlaced(latitude, longitude)) },
        )
        NexoraChip(
            text = state.pin?.let { stringResource(R.string.add_account_pin_format, it.latitude, it.longitude) }
                ?: stringResource(R.string.add_account_pin_hint),
            container = BrandTint,
            content = Brand,
            border = BrandBorder,
        )
    }
}
