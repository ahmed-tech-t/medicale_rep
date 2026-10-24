package com.example.medicalrep.ui.screens.visitdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitdetails.utils.VisitDetailsFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.DetailsBorder
import com.example.medicalrep.ui.ui.theme.DetailsLabel
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate800

/** Figma 302:2216 first card: place name, doctor line, account type, region and area. */
@Composable
fun VisitInfoCard(visit: VisitUi, modifier: Modifier = Modifier) {
    DetailsCard(modifier = modifier) {
       Column {
           NexoraText(
               text = visit.resolvedDisplayName(),
               style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
               color = Slate800,
           )
           NexoraText(
               text = stringResource(R.string.visit_details_doctor_format, visit.placeName),
               style = MaterialTheme.typography.labelMedium,
               color = DetailsLabel,
           )
       }
        HorizontalDivider(color = Slate100)
        InfoRow(label = stringResource(R.string.visit_details_account_type)) {
            NexoraChip(text = visit.account, container = Slate25, content = Brand, border = DetailsBorder)
        }
        InfoRow(label = stringResource(R.string.visit_details_region), value = visit.region)
        InfoRow(label = stringResource(R.string.visit_details_area), value = visit.city)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun VisitInfoCardPreview() {
    MedicalRepTheme { VisitInfoCard(visit = VisitDetailsFakes.visit("visit-1")) }
}
