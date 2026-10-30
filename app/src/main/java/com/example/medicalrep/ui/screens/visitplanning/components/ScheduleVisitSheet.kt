package com.example.medicalrep.ui.screens.visitplanning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningEvents
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningOverlay
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningState
import com.example.medicalrep.ui.screens.visitplanning.utils.PlanDay
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleVisitSheetHost(
    state: VisitPlanningState,
    onEvent: (VisitPlanningEvents) -> Unit,
) {
    if (state.overlay != VisitPlanningOverlay.ScheduleVisit) return
    val visit = state.rescheduleVisit ?: return

    ModalBottomSheet(
        onDismissRequest = { onEvent(VisitPlanningEvents.DismissOverlay) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Slate50,
    ) {
        ScheduleVisitSheetContent(
            visit = visit,
            days = state.days,
            selectedDays = state.selectedDays[visit.id].orEmpty(),
            bookedDays = state.bookedDays[visit.accountId].orEmpty(),
            onToggleDay = { onEvent(VisitPlanningEvents.ToggleDay(visit.id, it)) },
            onRequest = { onEvent(VisitPlanningEvents.RequestVisit) },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = spacing.spaceMediumPlus)
                .padding(bottom = spacing.spaceMedium),
        )
    }
}

/** The rejected visit's day-tile card (same rules as the Visits tab) plus a Request button. */
@Composable
fun ScheduleVisitSheetContent(
    visit: VisitUi,
    days: List<PlanDay>,
    selectedDays: Set<Long>,
    bookedDays: Set<Long>,
    onToggleDay: (Long) -> Unit,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {
        PlanVisitCard(
            visit = visit,
            days = days,
            selectedDays = selectedDays,
            bookedDays = bookedDays,
            onToggleDay = onToggleDay,
        )
        if (selectedDays.isNotEmpty()) {
            NexoraButton(
                text = stringResource(R.string.schedule_sheet_request),
                onClick = onRequest,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ScheduleVisitSheetContentPreview() {
    MedicalRepTheme {
        val state = VisitPlanningFakes.state()
        ScheduleVisitSheetContent(
            visit = VisitPlanningFakes.pendingVisits().first(),
            days = state.days,
            selectedDays = setOf(state.days[2].millis),
            bookedDays = setOf(state.days[0].millis),
            onToggleDay = {},
            onRequest = {},
            modifier = Modifier.padding(spacing.spaceMediumPlus),
        )
    }
}
