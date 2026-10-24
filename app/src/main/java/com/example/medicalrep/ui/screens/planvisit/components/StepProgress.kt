package com.example.medicalrep.ui.screens.planvisit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitStep
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate300
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50

@Composable
internal fun StepProgress(currentStep: PlanVisitStep) {
    val labels = listOf(
        stringResource(R.string.step_entity_data),
        stringResource(R.string.step_gifts_products),
        stringResource(R.string.step_result_finish),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate50)
            .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlanVisitStep.entries.forEachIndexed { index, step ->
            if (index > 0) StepSeparator()
            val isCurrent = step == currentStep
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .padding(end = spacing.spaceExtraSmall)
                            .size(dim.stepDotSize)
                            .background(Brand, CircleShape),
                    )
                } else if (step.ordinal < currentStep.ordinal) {
                    NexoraIcon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier
                            .padding(end = spacing.spaceExtraSmall)
                            .size(dim.stepCheckSize),
                    )
                }
                NexoraText(
                    text = labels[index],
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (step.ordinal <= currentStep.ordinal) Brand else Slate400,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun StepSeparator() {
    HorizontalDivider(modifier = Modifier.width(dim.stepSeparatorWidth), color = Slate300)
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun StepProgressArabicPreview() {
    MedicalRepTheme { StepProgress(currentStep = PlanVisitStep.GiftsProducts) }
}

@Preview(showBackground = true)
@Composable
private fun StepProgressEnglishPreview() {
    MedicalRepTheme { StepProgress(currentStep = PlanVisitStep.Entity) }
}
