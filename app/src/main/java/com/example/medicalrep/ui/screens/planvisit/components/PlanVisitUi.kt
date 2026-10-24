package com.example.medicalrep.ui.screens.planvisit.components
import com.example.medicalrep.ui.screens.planvisit.components.entity.EntityStepContent
import com.example.medicalrep.ui.screens.planvisit.components.giftsproducts.GiftsProductsStepContent
import com.example.medicalrep.ui.screens.planvisit.components.giftsproducts.AddVisitItemSheetHost
import com.example.medicalrep.ui.screens.planvisit.components.result.ResultStepContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitEvents
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitStep
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25

object PlanVisitTestTags {
    const val Content = "unplanned_content"
    const val Next = "unplanned_next"
    const val Previous = "unplanned_previous"
    const val EndVisit = "unplanned_end_visit"
    const val AddItemSheet = "unplanned_add_item_sheet"
}

@Composable
fun PlanVisitUi(
    state: PlanVisitState,
    onEvent: (PlanVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate25),
    ) {
        Header(
            title = stringResource(
                if (state.currentStep == PlanVisitStep.Entity) R.string.unplanned_visit_data_title
                else R.string.unplanned_visit_title,
            ),
            onBack = { onEvent(PlanVisitEvents.Back) },
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
                .testTag(PlanVisitTestTags.Content)
            when (state.currentStep) {
                PlanVisitStep.Entity -> EntityStepContent(state, onEvent, contentModifier)
                PlanVisitStep.GiftsProducts -> GiftsProductsStepContent(state, onEvent, contentModifier)
                PlanVisitStep.Result -> ResultStepContent(state, onEvent, contentModifier)
            }
        }

        FooterBar(currentStep = state.currentStep, onEvent = onEvent)
    }

    AddVisitItemSheetHost(state = state, onEvent = onEvent)
}

@Preview(showBackground = true, locale = "ar", device = Devices.PIXEL_7)
@Composable
private fun PlanVisitUiEntityPreview() {
    MedicalRepTheme {
        PlanVisitUi(state = PlanVisitFakes.state(), onEvent = {})
    }
}

@Preview(showBackground = true, locale = "ar", device = Devices.PIXEL_7)
@Composable
private fun PlanVisitUiGiftsPreview() {
    MedicalRepTheme {
        PlanVisitUi(
            state = PlanVisitFakes.state().copy(currentStep = PlanVisitStep.GiftsProducts),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun PlanVisitUiResultEnglishPreview() {
    MedicalRepTheme {
        PlanVisitUi(
            state = PlanVisitFakes.state().copy(currentStep = PlanVisitStep.Result),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, locale = "ar", device = Devices.TABLET)
@Composable
private fun PlanVisitUiTabletPreview() {
    MedicalRepTheme {
        PlanVisitUi(
            state = PlanVisitFakes.state().copy(currentStep = PlanVisitStep.Result),
            onEvent = {},
        )
    }
}
