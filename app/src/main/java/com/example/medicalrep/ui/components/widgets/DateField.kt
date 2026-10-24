package com.example.medicalrep.ui.components.widgets

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.CardBorderCool
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import java.util.Date

/**
 * Bordered date box that opens a white Material date picker. [prefix] (e.g. "From:") is shown
 * before the date when given.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    dateMillis: Long,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String? = null,
) {
    var showPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dateLabel = remember(dateMillis) { DateFormat.getDateFormat(context).format(Date(dateMillis)) }
    val shape = RoundedCornerShape(radii.large)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate25, shape)
            .border(DividerDefaults.Thickness, CardBorderCool, shape)
            .clickable { showPicker = true }
            .padding(horizontal = spacing.spaceSmallPlus + spacing.spaceExtraExtraSmall, vertical = spacing.spaceSmallPlus),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            if (prefix != null) {
                NexoraText(text = prefix, style = MaterialTheme.typography.bodyMedium, color = Slate500)
            }
            NexoraText(text = dateLabel, style = MaterialTheme.typography.bodyMedium, color = Ink)
        }
        NexoraIcon(
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = stringResource(R.string.cd_calendar),
            tint = Slate500,
        )
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
private fun DateFieldPreview() {
    MedicalRepTheme {
        DateField(
            dateMillis = 1_792_000_000_000L,
            onDateChange = {},
            prefix = "من:",
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DateFieldNoPrefixPreview() {
    MedicalRepTheme {
        DateField(dateMillis = 1_792_000_000_000L, onDateChange = {}, modifier = Modifier.padding(spacing.spaceMedium))
    }
}
