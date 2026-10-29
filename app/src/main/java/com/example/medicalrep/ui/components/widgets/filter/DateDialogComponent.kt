package com.example.medicalrep.ui.components.widgets.filter

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.medicalrep.ui.components.primitives.NexoraText

@Composable
fun DateDialogComponent(
    modifier: Modifier = Modifier,
    showStartDialog: Boolean,
    selectedDate: (Long?) -> Unit,
    state: DatePickerState,
    onDismiss: () -> Unit,
) {
    if (!showStartDialog) return
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate(state.selectedDateMillis)
                    onDismiss()
                }
            ) {
                NexoraText("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                NexoraText("Cancel")
            }
        }
    ) {
        DatePicker(state = state)
    }
}