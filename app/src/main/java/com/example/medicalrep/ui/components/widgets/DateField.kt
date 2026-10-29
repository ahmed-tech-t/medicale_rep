package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.utils.Filter_Date_Formate
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.filter.DateDialogComponent
import com.example.medicalrep.ui.ui.theme.CardBorderCool
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate500
import java.util.Locale

/**
 * Bordered date box that opens a white Material date picker. [prefix] (e.g. "From:") is shown
 * before the date when given.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    state: DatePickerState,
    showPicker: Boolean,
    placeholder: String,
    onShowPicker: () -> Unit,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    prefix: String? = null,
) {
    val shape = RoundedCornerShape(radii.large)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate25, shape)
            .border(DividerDefaults.Thickness, CardBorderCool, shape)
            .clickable { onShowPicker() }
            .padding(
                horizontal = spacing.spaceSmallPlus + spacing.spaceExtraExtraSmall,
                vertical = spacing.spaceSmallPlus
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            if (prefix != null) {
                NexoraText(
                    text = prefix,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Slate500
                )
            }
            NexoraText(text = placeholder, style = MaterialTheme.typography.bodyLarge, color = Ink)
        }
        NexoraIcon(
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = stringResource(R.string.cd_calendar),
            tint = Slate500,
        )
    }

    DateDialogComponent(
        showStartDialog = showPicker,
        selectedDate = { millis ->
            onDateChange(millis ?: return@DateDialogComponent)
        },
        state = state,
        onDismiss = onDismiss
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    dateMillis: Long,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    prefix: String? = null,
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    var showPicker by remember { mutableStateOf(false) }
    val formattedDate = remember(dateMillis) {
        val sdf = java.text.SimpleDateFormat(Filter_Date_Formate, Locale.getDefault())
        sdf.format(java.util.Date(dateMillis))
    }

    DateField(
        state = datePickerState,
        showPicker = showPicker,
        placeholder = placeholder.ifEmpty { formattedDate },
        onShowPicker = { showPicker = true },
        onDateChange = { millis ->
            onDateChange(millis)
            showPicker = false
        },
        modifier = modifier,
        onDismiss = { showPicker = false },
        prefix = prefix
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun DateFieldPreview() {
    MedicalRepTheme {
        Box(modifier = Modifier.padding(spacing.spaceMedium)) {
            DateField(
                dateMillis = System.currentTimeMillis(),
                onDateChange = {},
                prefix = "From:"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DateFieldEmptyPreview() {
    MedicalRepTheme {
        Box(modifier = Modifier.padding(spacing.spaceMedium)) {
            DateField(
                dateMillis = System.currentTimeMillis(),
                onDateChange = {},
                placeholder = "Select Date"
            )
        }
    }
}



