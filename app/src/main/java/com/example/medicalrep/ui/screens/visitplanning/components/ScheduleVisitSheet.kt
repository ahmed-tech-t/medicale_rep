package com.example.medicalrep.ui.screens.visitplanning.components

import android.text.format.DateFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningEvents
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningOverlay
import com.example.medicalrep.ui.screens.visitplanning.main.VisitPlanningState
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate700
import java.util.Date

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
    var showPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dateLabel = remember(dateMillis) { DateFormat.getDateFormat(context).format(Date(dateMillis)) }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(DividerDefaults.Thickness, Slate200, RoundedCornerShape(radii.extraLarge))
                    .clickable { showPicker = true }
                    .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NexoraText(text = dateLabel, style = MaterialTheme.typography.bodyLarge, color = Ink)
                NexoraIcon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = stringResource(R.string.cd_calendar),
                    tint = Slate500,
                )
            }
        }
        NexoraButton(text = stringResource(R.string.schedule_sheet_request), onClick = onRequest)
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        val pickerColors = DatePickerDefaults.colors(containerColor = Slate50)
        DatePickerDialog(
            colors = pickerColors,
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let(onDateChange)
                        showPicker = false
                    },
                ) { NexoraText(text = stringResource(android.R.string.ok)) }
            },
        ) { DatePicker(state = pickerState, colors = pickerColors) }
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
