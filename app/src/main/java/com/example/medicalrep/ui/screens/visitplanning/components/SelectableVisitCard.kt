package com.example.medicalrep.ui.screens.visitplanning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate900
import com.example.medicalrep.ui.ui.theme.Success

/** Figma 57:11724 selectable card: tap toggles the check circle; selected gets a green border. */
@Composable
fun SelectableVisitCard(
    visit: VisitUi,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NexoraCard(
        modifier = modifier,
        onClick = onToggle,
        containerColor = Slate50,
        borderColor = if (selected) Success else Slate200,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NexoraText(
                    text = visit.resolvedDisplayName(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Slate900,
                )
                VisitSubtitle(visit)
            }
            NexoraIcon(
                imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = stringResource(R.string.cd_select_visit),
                tint = if (selected) Success else Slate400,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SelectableVisitCardPreview() {
    MedicalRepTheme {
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            val visit = VisitPlanningFakes.visits().first()
            SelectableVisitCard(visit = visit, selected = false, onToggle = {})
            SelectableVisitCard(visit = visit, selected = true, onToggle = {})
        }
    }
}
