package com.example.medicalrep.ui.screens.reptracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.mapper.resolvedSpecialtyAccountLine
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.reptracking.utils.RepTrackingFakes
import com.example.medicalrep.ui.ui.theme.CardBorderCool
import com.example.medicalrep.ui.ui.theme.CompletedGreen
import com.example.medicalrep.ui.ui.theme.CompletedGreenSoft
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate800

/** Figma 287:1287 visit card: time badge, doctor, summary line and gifts / samples / completed tags. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepVisitCard(visit: VisitUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val samples = visit.products.sumOf { it.quantity }
    val gifts = visit.gifts.sumOf { it.quantity }

    NexoraCard(modifier = modifier, onClick = onClick, containerColor = Slate50, borderColor = CardBorderCool) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NexoraText(
                    text = visit.resolvedDisplayName(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate800,
                )
                NexoraChip(text = visit.time, container = Slate25, content = Slate500)
            }
            NexoraText(
                text = visit.resolvedSpecialtyAccountLine(),
                style = MaterialTheme.typography.labelMedium,
                color = Slate500,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            ) {
                NexoraChip(
                    text = stringResource(R.string.visit_status_completed),
                    container = CompletedGreenSoft,
                    content = CompletedGreen,
                )
                if (samples > 0) {
                    NexoraChip(
                        text = stringResource(R.string.rep_tracking_samples_format, samples),
                        container = Slate25,
                        content = Slate800,
                    )
                }
                if (gifts > 0) {
                    NexoraChip(
                        text = stringResource(R.string.rep_tracking_gifts_format, gifts),
                        container = Slate25,
                        content = Slate800,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun RepVisitCardPreview() {
    MedicalRepTheme {
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            RepTrackingFakes.visits().forEach { RepVisitCard(visit = it, onClick = {}) }
        }
    }
}
