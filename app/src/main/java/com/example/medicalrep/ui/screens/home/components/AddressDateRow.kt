package com.example.medicalrep.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.utils.formatWeekdayDate

/** The street the rep is on (when known) and today's date. */
@Composable
fun AddressDateRow(address: String?, dateMillis: Long, modifier: Modifier = Modifier) {
    val date = remember(dateMillis) { formatWeekdayDate(dateMillis) }
    Column(modifier = modifier.fillMaxWidth()) {
        if (!address.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)
            ) {
                NexoraIcon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Brand,
                    modifier = Modifier.size(dim.smallIconSize)
                )
                NexoraText(
                    text = address,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    maxLines = 1,
                )
            }
        }
        NexoraText(text = date, style = MaterialTheme.typography.labelLarge, color = Slate400)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AddressDateRowPreview() {
    MedicalRepTheme {
        AddressDateRow(
            address = "Stockton St, Union Square",
            dateMillis = 1_792_000_000_000L,
            modifier = Modifier.padding(spacing.spaceMedium)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddressDateRowNoAddressPreview() {
    MedicalRepTheme {
        AddressDateRow(
            address = null,
            dateMillis = 1_792_000_000_000L,
            modifier = Modifier.padding(spacing.spaceMedium)
        )
    }
}
