package com.example.medicalrep.ui.screens.planvisit.components.entity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraOutlinedSurface
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate900

/** The chosen account's location line (city - address) with a button that opens the map to it. */
@Composable
fun AccountLocationRow(
    addressLine: String,
    onViewMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NexoraOutlinedSurface(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Slate100, RoundedCornerShape(radii.medium)),
                contentAlignment = Alignment.Center,
            ) {
                NexoraIcon(painter = painterResource(R.drawable.ic_building), tint = Slate500)
            }
            Column(modifier = Modifier.weight(1f)) {
                NexoraText(
                    text = stringResource(R.string.visit_details_account_location),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900,
                )
                if (addressLine.isNotBlank()) {
                    NexoraText(
                        text = addressLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            NexoraButton(
                text = stringResource(R.string.plan_visit_map),
                onClick = onViewMap,
                variant = NexoraButtonVariant.Secondary,
                compact = true,
                fillMaxWidth = false,
                leadingIcon = { NexoraIcon(imageVector = Icons.Outlined.Map, tint = Brand) },
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AccountLocationRowPreview() {
    MedicalRepTheme { AccountLocationRow(addressLine = "Shebin El-Kom - Stadium Street", onViewMap = {}) }
}

@Preview(showBackground = true)
@Composable
private fun AccountLocationRowNoAddressPreview() {
    MedicalRepTheme { AccountLocationRow(addressLine = "", onViewMap = {}) }
}
