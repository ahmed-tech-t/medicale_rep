package com.example.medicalrep.ui.screens.unplannedvisit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitEvents
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitState
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitStep
import com.example.medicalrep.ui.screens.unplannedvisit.utils.UnplannedVisitFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50

object UnplannedVisitTestTags {
    const val Content = "unplanned_content"
    const val Next = "unplanned_next"
    const val Previous = "unplanned_previous"
    const val EndVisit = "unplanned_end_visit"
    const val AddItemSheet = "unplanned_add_item_sheet"
}

@Composable
fun UnplannedVisitUi(
    state: UnplannedVisitState,
    onEvent: (UnplannedVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate25),
    ) {
        Header(
            title = stringResource(
                if (state.currentStep == UnplannedVisitStep.Entity) R.string.unplanned_visit_data_title
                else R.string.unplanned_visit_title,
            ),
            onBack = { onEvent(UnplannedVisitEvents.Back) },
        )
        StepProgress(currentStep = state.currentStep)
        HorizontalDivider(color = Slate200)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val contentModifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
                .testTag(UnplannedVisitTestTags.Content)
            when (state.currentStep) {
                UnplannedVisitStep.Entity -> EntityStepContent(state, onEvent, contentModifier)
                UnplannedVisitStep.GiftsProducts -> GiftsProductsStepContent(state, onEvent, contentModifier)
                UnplannedVisitStep.Result -> ResultStepContent(state, onEvent, contentModifier)
            }
        }

        FooterBar(currentStep = state.currentStep, onEvent = onEvent)
    }

    AddVisitItemSheetHost(state = state, onEvent = onEvent)
}

@Composable
private fun Header(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brand)
            .statusBarsPadding()
            .padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraIconButton(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_back),
            onClick = onBack,
            tint = Slate50,
        )
        NexoraText(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate50,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StepProgress(currentStep: UnplannedVisitStep) {
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
        verticalAlignment = Alignment.Top,
    ) {
        UnplannedVisitStep.entries.forEachIndexed { index, step ->
            val isDone = step.ordinal < currentStep.ordinal
            val isCurrent = step == currentStep
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    StepConnector(visible = index > 0, active = isDone || isCurrent, modifier = Modifier.weight(1f))
                    StepDot(isDone = isDone, isCurrent = isCurrent)
                    StepConnector(
                        visible = index < labels.lastIndex,
                        active = isDone,
                        modifier = Modifier.weight(1f),
                    )
                }
                NexoraText(
                    text = labels[index],
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    ),
                    color = if (isDone || isCurrent) Ink else Slate400,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun StepConnector(visible: Boolean, active: Boolean, modifier: Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = DividerDefaults.Thickness * 2,
        color = when {
            !visible -> Slate50
            active -> Brand
            else -> Slate200
        },
    )
}

@Composable
private fun StepDot(isDone: Boolean, isCurrent: Boolean) {
    Box(
        modifier = Modifier
            .size(dim.smallIconSize)
            .background(if (isDone || isCurrent) Brand else Slate100, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isDone -> NexoraIcon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Slate50,
                modifier = Modifier.size(spacing.spaceSmallPlus),
            )
            isCurrent -> Box(
                modifier = Modifier
                    .size(spacing.spaceSmall)
                    .background(Slate50, CircleShape),
            )
        }
    }
}

@Composable
private fun FooterBar(currentStep: UnplannedVisitStep, onEvent: (UnplannedVisitEvents) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate50)
            .navigationBarsPadding()
            .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        if (currentStep != UnplannedVisitStep.Entity) {
            NexoraButton(
                text = stringResource(R.string.action_previous),
                onClick = { onEvent(UnplannedVisitEvents.Previous) },
                variant = NexoraButtonVariant.Secondary,
                border = BorderStroke(DividerDefaults.Thickness, Slate200),
                modifier = Modifier
                    .weight(1f)
                    .height(dim.primaryButtonHeight)
                    .testTag(UnplannedVisitTestTags.Previous),
            )
        }
        if (currentStep == UnplannedVisitStep.Result) {
            NexoraButton(
                text = stringResource(R.string.unplanned_visit_end),
                onClick = { onEvent(UnplannedVisitEvents.EndVisit) },
                modifier = Modifier
                    .weight(1f)
                    .height(dim.primaryButtonHeight)
                    .testTag(UnplannedVisitTestTags.EndVisit),
            )
        } else {
            NexoraButton(
                text = stringResource(R.string.action_next),
                onClick = { onEvent(UnplannedVisitEvents.Next) },
                modifier = Modifier
                    .weight(1f)
                    .height(dim.primaryButtonHeight)
                    .testTag(UnplannedVisitTestTags.Next),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar", device = Devices.PIXEL_7)
@Composable
private fun UnplannedVisitUiEntityPreview() {
    MedicalRepTheme {
        UnplannedVisitUi(state = UnplannedVisitFakes.state(), onEvent = {})
    }
}

@Preview(showBackground = true, locale = "ar", device = Devices.PIXEL_7)
@Composable
private fun UnplannedVisitUiGiftsPreview() {
    MedicalRepTheme {
        UnplannedVisitUi(
            state = UnplannedVisitFakes.state().copy(currentStep = UnplannedVisitStep.GiftsProducts),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun UnplannedVisitUiResultEnglishPreview() {
    MedicalRepTheme {
        UnplannedVisitUi(
            state = UnplannedVisitFakes.state().copy(currentStep = UnplannedVisitStep.Result),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, locale = "ar", device = Devices.TABLET)
@Composable
private fun UnplannedVisitUiTabletPreview() {
    MedicalRepTheme {
        UnplannedVisitUi(
            state = UnplannedVisitFakes.state().copy(currentStep = UnplannedVisitStep.Result),
            onEvent = {},
        )
    }
}
