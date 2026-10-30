package com.example.medicalrep.ui.screens.visitplanning.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.mapper.resolvedAccountSpecialtyLine
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.utils.PlanDay
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.screens.visitplanning.utils.planDays
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 591:3150 visit card: name, account and class, then the days the rep can plan it for (pick one or more). */
@Composable
fun PlanVisitCard(
    visit: VisitUi,
    days: List<PlanDay>,
    selectedDays: Set<Long>,
    onToggleDay: (Long) -> Unit,
    modifier: Modifier = Modifier,
    bookedDays: Set<Long> = emptySet(),
) {
    NexoraCard(
        modifier = modifier,
        containerColor = Slate50,
        borderColor = Slate200,
        contentPadding = PaddingValues(vertical = spacing.spaceMedium),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spaceMedium),
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceMediumPlus - 1.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    NexoraText(
                        text = visit.resolvedDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                    )
                    NexoraText(
                        text = visit.resolvedAccountSpecialtyLine(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = Slate400,
                    )
                }
                visit.classCategory?.let { ClassChip(it.name) }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = spacing.spaceMedium),
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            ) {
                items(days, key = { it.millis }) { day ->
                    DayTile(
                        day = day,
                        selected = day.millis in selectedDays,
                        booked = day.millis in bookedDays,
                        onToggle = { onToggleDay(day.millis) },
                    )
                }
            }
            if (selectedDays.isNotEmpty()) {
                NexoraText(
                    text = pluralStringResource(R.plurals.plan_days_selected, selectedDays.size, selectedDays.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand,
                    modifier = Modifier.padding(horizontal = spacing.spaceMedium),
                )
            }
        }
    }
}

@Composable
private fun ClassChip(name: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(radii.pill)
    NexoraText(
        text = stringResource(R.string.plan_class_format, name),
        style = MaterialTheme.typography.bodySmall,
        color = Ink,
        modifier = modifier
            .background(Slate100, shape)
            .border(DividerDefaults.Thickness, Slate200, shape)
            .padding(horizontal = 10.dp, vertical = 1.dp),
    )
}

@Composable
private fun DayTile(day: PlanDay, selected: Boolean, booked: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .size(width = 58.dp, height = 76.dp)
            .alpha(if (booked) 0.4f else 1f)
            .clip(shape)
            .background(if (selected) BrandTint else Slate50)
            .border(DividerDefaults.Thickness, if (selected) Brand else Slate200, shape)
            .toggleable(value = selected, enabled = !booked, role = Role.Checkbox, onValueChange = { if (!booked) onToggle() })
            .padding(vertical = spacing.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        NexoraText(text = day.weekday, style = MaterialTheme.typography.bodySmall, color = if (selected) Brand else Ink)
        NexoraText(
            text = day.date,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
            color = if (selected) Brand else Slate400,
        )
        NexoraIcon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (selected) Brand else Slate400,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PlanVisitCardPreview() {
    MedicalRepTheme {
        val days = planDays(1_792_000_000_000L)
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            val visit = VisitPlanningFakes.visits().first()
            PlanVisitCard(visit = visit, days = days, selectedDays = emptySet(), onToggleDay = {})
            PlanVisitCard(visit = visit, days = days, selectedDays = setOf(days[1].millis), onToggleDay = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayTilePreview() {
    MedicalRepTheme {
        Row(
            modifier = Modifier.padding(spacing.spaceMedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            val day = planDays(1_792_000_000_000L).first()
            DayTile(day = day, selected = false, booked = false, onToggle = {})
            DayTile(day = day, selected = true, booked = false, onToggle = {})
            DayTile(day = day, selected = false, booked = true, onToggle = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClassChipPreview() {
    MedicalRepTheme {
        Row(
            modifier = Modifier.padding(spacing.spaceMedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            ClassChip(name = "A")
            ClassChip(name = "B")
            ClassChip(name = "C")
        }
    }
}
