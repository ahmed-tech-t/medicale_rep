package com.example.medicalrep.ui.screens.planvisit.components.entity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate600

@Immutable
data class SegmentOption(val label: String, val selected: Boolean)

/** Figma "فترة العمل" / "نوع الزيارة": bold label at the start, pill segments at the end. */
@Composable
fun SegmentedChoiceRow(
    label: String,
    options: List<SegmentOption>,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Ink,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(radii.medium))
                .background(Slate100)
                .padding(spacing.spaceExtraSmall),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
        ) {
            options.forEachIndexed { index, option ->
                NexoraText(
                    text = option.label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (option.selected) Slate50 else Slate600,
                    modifier = Modifier
                        .clip(RoundedCornerShape(radii.small))
                        .background(if (option.selected) Brand else Slate100)
                        .semantics { selected = option.selected }
                        .clickable(role = Role.Tab) { onSelectIndex(index) }
                        .padding(horizontal = spacing.spaceSmallPlus, vertical = spacing.spaceExtraSmall),
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SegmentedChoiceRowPreview() {
    MedicalRepTheme {
        SegmentedChoiceRow(
            label = "فترة العمل",
            options = listOf(SegmentOption("صباحا", selected = false), SegmentOption("مسائى", selected = true)),
            onSelectIndex = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
