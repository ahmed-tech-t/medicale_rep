package com.example.medicalrep.ui.screens.visitplanning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.mapper.resolvedSpecialtyAccountLine
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate900

/** Figma 57:11724 pending card: out-of-plan chip, name, specialty line and date / time. */
@Composable
fun PendingVisitCard(
    visit: VisitUi,
    modifier: Modifier = Modifier,
) {
    NexoraCard(modifier = modifier, containerColor = Slate50, borderColor = Slate200) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
            NexoraChip(
                text = stringResource(R.string.visit_planning_out_of_plan),
                container = Slate100,
                content = Slate600,
                shape = RoundedCornerShape(radii.pill),
            )
            NexoraText(
                text = visit.resolvedDisplayName(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Slate900,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VisitSubtitle(visit = visit, modifier = Modifier.weight(1f))
                NexoraText(
                    text = "${visit.date} • ${visit.time}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate500,
                )
            }
        }
    }
}

/** "specialty • account" line under a visit name. */
@Composable
internal fun VisitSubtitle(visit: VisitUi, modifier: Modifier = Modifier) {
    NexoraText(
        text = visit.resolvedSpecialtyAccountLine(),
        style = MaterialTheme.typography.labelMedium,
        color = Slate500,
        modifier = modifier,
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PendingVisitCardPreview() {
    MedicalRepTheme {
        PendingVisitCard(
            visit = VisitPlanningFakes.pendingVisits().first(),
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
