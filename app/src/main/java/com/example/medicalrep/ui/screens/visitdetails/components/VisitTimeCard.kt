package com.example.medicalrep.ui.screens.visitdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitdetails.utils.VisitDetailsFakes
import com.example.medicalrep.ui.ui.theme.DetailsBorder
import com.example.medicalrep.ui.ui.theme.DetailsLabel
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate800

/** Figma 302:2216 second card: visit time and date boxes, then the visit type. */
@Composable
fun VisitTimeCard(visit: VisitUi, modifier: Modifier = Modifier) {
    DetailsCard(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            MiniInfoBox(
                label = stringResource(R.string.visit_details_visit_date),
                value = visit.date,
                modifier = Modifier.weight(1f),
            )
            MiniInfoBox(
                label = stringResource(R.string.visit_details_visit_time),
                value = visit.time,
                modifier = Modifier.weight(1f),
            )
        }
        InfoRow(
            label = stringResource(R.string.visit_details_visit_type),
            value = if (visit.isDoubleVisit) {
                stringResource(R.string.unplanned_double_with_format, visit.supervisor?.name.orEmpty())
            } else {
                stringResource(R.string.visit_type_single)
            },
        )
    }
}

@Composable
private fun MiniInfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(radii.large)
    Column(
        modifier = modifier
            .background(Slate25, shape)
            .border(DividerDefaults.Thickness, DetailsBorder, shape)
            .padding(vertical = spacing.spaceSmallPlus)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NexoraText(text = label, style = MaterialTheme.typography.labelSmall, color = DetailsLabel)
        NexoraText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate800,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun VisitTimeCardPreview() {
    MedicalRepTheme { VisitTimeCard(visit = VisitDetailsFakes.visit("visit-1")) }
}

@Preview(showBackground = true)
@Composable
private fun VisitTimeCardDoublePreview() {
    MedicalRepTheme {
        VisitTimeCard(visit = VisitDetailsFakes.visit("visit-1").copy(isDoubleVisit = true, supervisor = BasicModelUi("sup-1", "Ahmed")))
    }
}
