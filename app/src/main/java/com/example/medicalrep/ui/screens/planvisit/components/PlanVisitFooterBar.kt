package com.example.medicalrep.ui.screens.planvisit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitEvents
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitStep
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate50

@Composable
internal fun FooterBar(
    currentStep: PlanVisitStep,
    onEvent: (PlanVisitEvents) -> Unit,
    nextEnabled: Boolean = true,
    endEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate50)
            .navigationBarsPadding()
            .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        if (currentStep != PlanVisitStep.Entity) {
            NexoraButton(
                text = stringResource(R.string.action_previous),
                onClick = { onEvent(PlanVisitEvents.Previous) },
                variant = NexoraButtonVariant.Secondary,
                border = BorderStroke(DividerDefaults.Thickness, Slate200),
                modifier = Modifier
                    .weight(.5f)
                    .height(dim.primaryButtonHeight)
                    .testTag(PlanVisitTestTags.Previous),
            )
        }
        if (currentStep == PlanVisitStep.Result) {
            NexoraButton(
                text = stringResource(R.string.unplanned_visit_end),
                onClick = { onEvent(PlanVisitEvents.EndVisit) },
                enabled = endEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(dim.primaryButtonHeight)
                    .testTag(PlanVisitTestTags.EndVisit),
            )
        } else {
            NexoraButton(
                text = stringResource(R.string.action_next),
                onClick = { onEvent(PlanVisitEvents.Next) },
                enabled = nextEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(dim.primaryButtonHeight)
                    .testTag(PlanVisitTestTags.Next),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun FooterBarEntityPreview() {
    MedicalRepTheme { FooterBar(currentStep = PlanVisitStep.Entity, onEvent = {}) }
}

@Preview(showBackground = true)
@Composable
private fun FooterBarGiftsPreview() {
    MedicalRepTheme { FooterBar(currentStep = PlanVisitStep.GiftsProducts, onEvent = {}) }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun FooterBarBlockedPreview() {
    MedicalRepTheme { FooterBar(currentStep = PlanVisitStep.Entity, onEvent = {}, nextEnabled = false) }
}

@Preview(showBackground = true)
@Composable
private fun FooterBarResultPreview() {
    MedicalRepTheme { FooterBar(currentStep = PlanVisitStep.Result, onEvent = {}) }
}
