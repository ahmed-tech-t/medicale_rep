package com.example.medicalrep.ui.screens.visitdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitdetails.utils.VisitDetailsFakes
import com.example.medicalrep.ui.screens.visitdetails.utils.distanceFromAccountMeters
import com.example.medicalrep.ui.screens.visitdetails.utils.hasLocations
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.DetailsLabel
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate800

/** Figma 302:2216 third card: visit and account coordinates and the "view on map" button. */
@Composable
fun VisitLocationCard(
    visit: VisitUi,
    onViewOnMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsCard(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            CoordinateBox(
                title = stringResource(R.string.visit_details_visit_location),
                latitude = visit.visitLatitude,
                longitude = visit.visitLongitude,
                modifier = Modifier.weight(1f),
            )
            CoordinateBox(
                title = stringResource(R.string.visit_details_account_location),
                latitude = visit.accountLatitude,
                longitude = visit.accountLongitude,
                modifier = Modifier.weight(1f),
            )
        }
        if (visit.hasLocations) {
            val distance = remember(visit) { visit.distanceFromAccountMeters }
            LocationMatchBanner(distanceMeters = distance)
        }
        NexoraButton(text = stringResource(R.string.visit_details_view_on_map), onClick = onViewOnMap)
    }
}

@Composable
private fun CoordinateBox(title: String, latitude: Double, longitude: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Slate25, RoundedCornerShape(radii.medium))
            .padding(spacing.spaceSmall)
            .fillMaxWidth(),
    ) {
        NexoraText(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Brand,
        )
        CoordinateLine(stringResource(R.string.visit_details_latitude), latitude)
        CoordinateLine(stringResource(R.string.visit_details_longitude), longitude)
    }
}

@Composable
private fun CoordinateLine(label: String, value: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        NexoraText(text = label, style = MaterialTheme.typography.labelSmall, color = DetailsLabel)
        NexoraText(
            text = value.toString(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Slate800,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun VisitLocationCardPreview() {
    MedicalRepTheme { VisitLocationCard(visit = VisitDetailsFakes.visit("visit-1"), onViewOnMap = {}) }
}
