package com.example.medicalrep.ui.screens.officework.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraLabel
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.components.widgets.DateField
import com.example.medicalrep.ui.screens.officework.main.OfficeWorkEvents
import com.example.medicalrep.ui.screens.officework.main.OfficeWorkState
import com.example.medicalrep.ui.screens.officework.main.OfficeWorkType
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.CardBorderCool
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600

private val shifts = listOf(Shift.Full, Shift.Pm, Shift.Am)

private fun shiftLabel(shift: Shift) = when (shift) {
    Shift.Am -> R.string.shift_am
    Shift.Pm -> R.string.shift_pm
    Shift.Full -> R.string.shift_full
}

@Composable
fun OfficeWorkContent(
    state: OfficeWorkState,
    onEvent: (OfficeWorkEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(
            title = stringResource(R.string.home_office_work),
            onBack = { onEvent(OfficeWorkEvents.Back) },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dim.screenPaddingHorizontal)
                .padding(vertical = spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
        ) {
            // Data card
            NexoraCard {
                NexoraText(
                    text = stringResource(R.string.office_work_data_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                NexoraLabel(text = stringResource(R.string.office_work_date_label))
                DateField(
                    dateMillis = state.dateMillis,
                    onDateChange = { onEvent(OfficeWorkEvents.OnDateChange(it)) },
                )
                NexoraLabel(text = stringResource(R.string.office_work_type_label))
                WorkTypeField(
                    selected = state.workType,
                    expanded = state.showWorkTypeMenu,
                    onToggle = { onEvent(OfficeWorkEvents.ToggleWorkTypeMenu) },
                    onSelect = { onEvent(OfficeWorkEvents.OnWorkTypeSelect(it)) },
                )
            }

            // Shift card
            NexoraCard {
                NexoraText(
                    text = stringResource(R.string.office_work_period_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                ShiftRow(
                    selected = state.shift,
                    onSelect = { onEvent(OfficeWorkEvents.OnShiftSelect(it)) },
                )
            }

            // Notes card
            NexoraCard {
                NexoraText(
                    text = stringResource(R.string.office_work_notes_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                NexoraTextField(
                    state = state.notes,
                    singleLine = false,
                    minLines = 3,
                    placeholder = stringResource(R.string.office_work_notes_hint),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dim.screenPaddingHorizontal)
                .padding(bottom = spacing.spaceMedium),
        ) {
            NexoraButton(
                text = stringResource(R.string.office_work_submit),
                onClick = { onEvent(OfficeWorkEvents.Submit) },
            )
        }
    }
}

@Composable
private fun WorkTypeField(
    selected: OfficeWorkType,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (OfficeWorkType) -> Unit,
) {
    val shape = RoundedCornerShape(radii.large)
    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    fieldWidth = with(density) { coordinates.size.width.toDp() }
                }
                .clip(shape)
                .background(Slate25, shape)
                .border(DividerDefaults.Thickness, CardBorderCool, shape)
                .clickable(onClick = onToggle)
                .padding(
                    horizontal = spacing.spaceSmallPlus,
                    vertical = spacing.spaceSmallPlus,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraText(
                text = stringResource(selected.labelRes),
                style = MaterialTheme.typography.bodyLarge,
                color = Ink
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Slate500,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onToggle,
            modifier = Modifier
                .width(fieldWidth)
                .background(Color.White)
        ) {
            OfficeWorkType.all.forEach { type ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        NexoraText(
                            text = stringResource(type.labelRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink
                        )
                    },
                    onClick = {
                        onSelect(type)
                        onToggle()
                    },
                    trailingIcon = {
                        if (type == selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Brand
                            )
                        }
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Ink,
                        trailingIconColor = Brand
                    )
                )
            }
        }
    }
}

@Composable
private fun ShiftRow(
    selected: Shift,
    onSelect: (Shift) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radii.medium))
            .background(Slate100)
            .padding(spacing.spaceExtraSmall),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
    ) {
        shifts.forEach { shift ->
            NexoraText(
                text = stringResource(shiftLabel(shift)),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (shift == selected) Slate50 else Slate600,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(radii.small))
                    .background(if (shift == selected) Brand else Slate100)
                    .clickable { onSelect(shift) }
                    .padding(horizontal = spacing.spaceSmallPlus, vertical = spacing.spaceExtraSmall),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun OfficeWorkContentPreview() {
    MedicalRepTheme {
        OfficeWorkContent(state = OfficeWorkState(), onEvent = {})
    }
}
