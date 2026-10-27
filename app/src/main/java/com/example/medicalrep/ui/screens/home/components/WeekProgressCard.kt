package com.example.medicalrep.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandBorder
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate300
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate500

/** The visits done out of the visits planned this week, as numbers, a percent and a bar. */
@Composable
fun WeekProgressCard(progress: VisitProgress, modifier: Modifier = Modifier) {
    NexoraCard(modifier = modifier, containerColor = BrandTint.copy(alpha = .4f), borderColor = BrandBorder) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    NexoraText(
                        text = stringResource(R.string.home_week_progress),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Brand,
                    )
                    NexoraText(
                        text = stringResource(R.string.home_completed_visits),
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate500
                    )
                }
                Row {
                    NexoraText(
                        text = "${progress.planned} / ",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Ink,
                        modifier = Modifier.alignByBaseline()
                    )
                    NexoraText(
                        text = "${progress.completed}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Ink,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)
            ) {
                NexoraText(
                    text = "${progress.percent}%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate400
                )

                ProgressBarComponent(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(radii.pill)),
                    percentage = progress.percent / 100f
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun WeekProgressCardPreview() {
    MedicalRepTheme {
        WeekProgressCard(
            progress = VisitProgress(completed = 10, planned = 26),
            modifier = Modifier.padding(spacing.spaceMedium)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekProgressCardEmptyPreview() {
    MedicalRepTheme {
        WeekProgressCard(
            progress = VisitProgress(),
            modifier = Modifier.padding(spacing.spaceMedium)
        )
    }
}
