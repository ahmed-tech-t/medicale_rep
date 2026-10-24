package com.example.medicalrep.ui.screens.visitplanning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.widgets.DateField
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningEvents
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningOverlay
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningState
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleVisitSheetHost(
    state: VisitPlanningState,
    onEvent: (VisitPlanningEvents) -> Unit,
) {
    if (state.overlay != VisitPlanningOverlay.ScheduleVisit) return

    ModalBottomSheet(
        onDismissRequest = { onEvent(VisitPlanningEvents.DismissOverlay) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Slate50,
    ) {
        ScheduleVisitSheetContent(
            dateMillis = state.scheduleDateMillis,
            onDateChange = { onEvent(VisitPlanningEvents.DateChanged(it)) },
            onRequest = { onEvent(VisitPlanningEvents.RequestVisit) },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = spacing.spaceMediumPlus)
                .padding(bottom = spacing.spaceMedium),
        )
    }
}

/** Figma 57:11724 sheet: title, subtitle, date field (opens a date picker) and the request button. */
@Composable
fun ScheduleVisitSheetContent(
    dateMillis: Long,
    onDateChange: (Long) -> Unit,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {
        Column {
            NexoraText(
                text = stringResource(R.string.schedule_sheet_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
            )
            NexoraText(
                text = stringResource(R.string.schedule_sheet_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Slate400,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            NexoraText(
                text = stringResource(R.string.schedule_sheet_date_label),
                style = MaterialTheme.typography.labelLarge,
                color = Slate700,
            )
            DateField(dateMillis = dateMillis, onDateChange = onDateChange)
        }
        NexoraButton(text = stringResource(R.string.schedule_sheet_request), onClick = onRequest)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ScheduleVisitSheetContentPreview() {
    MedicalRepTheme {
        ScheduleVisitSheetContent(
            dateMillis = VisitPlanningFakes.state().scheduleDateMillis,
            onDateChange = {},
            onRequest = {},
            modifier = Modifier.padding(spacing.spaceMediumPlus),
        )
    }
}
