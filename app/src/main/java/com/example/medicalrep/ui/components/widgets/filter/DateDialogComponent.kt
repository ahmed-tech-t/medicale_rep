package com.example.medicalrep.ui.components.widgets.filter

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate300
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

@Composable
fun DateDialogComponent(
    modifier: Modifier = Modifier,
    showStartDialog: Boolean,
    selectedDate: (Long?) -> Unit,
    state: DatePickerState,
    onDismiss: () -> Unit,
) {
    if (!showStartDialog) return
    val colors = DatePickerDefaults.colors(
        containerColor = Slate50,
        titleContentColor = Slate500,
        headlineContentColor = Ink,
        weekdayContentColor = Slate500,
        subheadContentColor = Ink,
        navigationContentColor = Ink,
        yearContentColor = Ink,
        currentYearContentColor = Brand,
        selectedYearContentColor = Slate50,
        selectedYearContainerColor = Brand,
        dayContentColor = Ink,
        disabledDayContentColor = Slate300,
        selectedDayContentColor = Slate50,
        selectedDayContainerColor = Brand,
        todayContentColor = Brand,
        todayDateBorderColor = Brand,
        dividerColor = Slate200,
    )
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        colors = colors,
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate(state.selectedDateMillis)
                    onDismiss()
                }
            ) {
                NexoraText("OK", color = Brand)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                NexoraText("Cancel", color = Slate500)
            }
        }
    ) {
        DatePicker(state = state, colors = colors)
    }
}