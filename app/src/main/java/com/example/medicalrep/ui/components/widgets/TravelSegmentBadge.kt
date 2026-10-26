package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.common.travel.TravelMode
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate900
import com.example.medicalrep.ui.utils.durationText

/** Small white badge on the route: how long the rep took between two visits and whether he drove or walked. */
@Composable
fun TravelSegmentBadge(mode: TravelMode, seconds: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Slate200),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceExtraSmall),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraText(
                text = durationText(seconds),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate900,
            )
            NexoraIcon(
                painter = painterResource(if (mode == TravelMode.Car) R.drawable.ic_car else R.drawable.ic_walk),
                tint = Brand,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun TravelSegmentBadgePreview() {
    MedicalRepTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            TravelSegmentBadge(mode = TravelMode.Foot, seconds = 20 * 60)
            TravelSegmentBadge(mode = TravelMode.Car, seconds = 5 * 60)
        }
    }
}
