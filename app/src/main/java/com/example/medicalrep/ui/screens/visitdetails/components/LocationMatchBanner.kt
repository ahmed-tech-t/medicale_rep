package com.example.medicalrep.ui.screens.visitdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.visitdetails.utils.isAtAccountLocation
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.OnSuccessContainer
import com.example.medicalrep.ui.ui.theme.OnWarningContainer
import com.example.medicalrep.ui.ui.theme.Slate800
import com.example.medicalrep.ui.ui.theme.Success
import com.example.medicalrep.ui.ui.theme.SuccessContainer
import com.example.medicalrep.ui.ui.theme.Warning
import com.example.medicalrep.ui.ui.theme.WarningBorder
import com.example.medicalrep.ui.ui.theme.WarningContainer

private const val METERS_PER_KM = 1000

/** Green when the visit was recorded at the account (within 50 m), amber warning otherwise. */
@Composable
fun LocationMatchBanner(distanceMeters: Int, modifier: Modifier = Modifier) {
    val matched = isAtAccountLocation(distanceMeters)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (matched) SuccessContainer else WarningContainer,
        shape = RoundedCornerShape(radii.medium),
        border = BorderStroke(1.dp, if (matched) Success.copy(alpha = 0.3f) else WarningBorder),
    ) {
        Row(
            modifier = Modifier.padding(spacing.spaceSmallPlus),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (matched) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = null,
                tint = if (matched) Success else Warning,
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
                NexoraText(
                    text = stringResource(
                        if (matched) R.string.visit_details_location_matched else R.string.visit_details_location_away
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate800,
                )
                NexoraText(
                    text = if (distanceMeters >= METERS_PER_KM) {
                        stringResource(R.string.visit_details_location_distance_km, (distanceMeters / METERS_PER_KM.toDouble()).roundToInt())
                    } else {
                        stringResource(R.string.visit_details_location_distance, distanceMeters)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (matched) OnSuccessContainer else OnWarningContainer,
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun LocationMatchBannerMatchedPreview() {
    MedicalRepTheme { LocationMatchBanner(distanceMeters = 20) }
}

@Preview(showBackground = true)
@Composable
private fun LocationMatchBannerAwayPreview() {
    MedicalRepTheme { LocationMatchBanner(distanceMeters = 750) }
}
