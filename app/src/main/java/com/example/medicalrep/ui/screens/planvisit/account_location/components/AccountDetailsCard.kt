package com.example.medicalrep.ui.screens.planvisit.account_location.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.screens.planvisit.account_location.main.AccountLocationState
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate900

/** Account name with its class, address, entity type and specialty, and the Get Directions button. */
@Composable
fun AccountDetailsCard(
    state: AccountLocationState,
    onGetDirections: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NexoraCard(modifier = modifier, borderColor = Color.Unspecified) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraText(
                text = state.accountName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Slate900,
                modifier = Modifier.weight(1f),
            )
            if (state.classCategory.isNotBlank()) {
                NexoraChip(
                    text = stringResource(R.string.unplanned_class_format, state.classCategory),
                    container = InfoContainer,
                    content = Info,
                )
            }
        }
        if (state.address.isNotBlank()) {
            NexoraText(text = state.address, style = MaterialTheme.typography.labelSmall, color = Slate500)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            listOf(state.accountType, state.specialty).filter { it.isNotBlank() }.forEach {
                NexoraChip(text = it, container = Slate100, content = Slate600)
            }
        }
        NexoraButton(text = stringResource(R.string.account_location_get_directions), onClick = onGetDirections)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AccountDetailsCardPreview() {
    MedicalRepTheme {
        AccountDetailsCard(
            state = AccountLocationState(
                accountLocation = MapCameraTarget(latitude = 30.5657, longitude = 31.0043),
                accountName = "Al-Iman Pharmacy",
                address = "Shebin El-Kom - Stadium Square",
                classCategory = "C",
                accountType = "Medical Center",
                specialty = "Orthopedics",
            ),
            onGetDirections = {},
        )
    }
}
