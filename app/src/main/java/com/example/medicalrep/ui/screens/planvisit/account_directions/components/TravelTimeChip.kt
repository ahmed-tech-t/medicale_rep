package com.example.medicalrep.ui.screens.planvisit.account_directions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraOutlinedSurface
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate900

/** A travel mode with its time, e.g. "25 min" over "by car · 13 km". */
@Composable
fun TravelTimeChip(
    iconRes: Int,
    duration: String,
    detail: String,
    modifier: Modifier = Modifier,
) {
    NexoraOutlinedSurface(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NexoraText(
                    text = duration,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Slate900,
                )
                NexoraText(text = detail, style = MaterialTheme.typography.labelSmall, color = Slate500)
            }
            NexoraIcon(painter = painterResource(iconRes), tint = Brand)
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun TravelTimeChipPreview() {
    MedicalRepTheme {
        TravelTimeChip(iconRes = R.drawable.ic_car, duration = "25 د", detail = "بالسيارة · 13 كم")
    }
}
