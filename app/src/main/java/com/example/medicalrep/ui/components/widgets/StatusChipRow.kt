package com.example.medicalrep.ui.components.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 591:2917 chip row: one chip per option, the selected one filled with the result count beside its label. */
@Composable
fun StatusChipRow(
    labels: List<String>,
    selectedIndex: Int,
    selectedCount: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = spacing.spaceMedium),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        itemsIndexed(labels, key = { index, _ -> index }) { index, label ->
            StatusChip(
                label = label,
                selected = index == selectedIndex,
                count = selectedCount,
                onClick = { onSelect(index) },
            )
        }
    }
}

@Composable
private fun StatusChip(label: String, selected: Boolean, count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(radii.pill)
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .background(if (selected) Brand else Slate50)
            .then(if (selected) Modifier else Modifier.border(DividerDefaults.Thickness, Slate200, shape))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            color = if (selected) Slate50 else Ink,
        )
        if(selected) {
            NexoraText(
                text = count.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = Slate50,
                modifier = Modifier
                    .defaultMinSize(minWidth = 21.dp, minHeight = 20.dp)
                    .background(Slate50.copy(alpha = 0.2f), RoundedCornerShape(radii.pill))
                    .padding(horizontal = 7.dp),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun StatusChipRowPreview() {
    MedicalRepTheme {
        StatusChipRow(
            labels = listOf("الكل", "قيد المراجعة", "تمت الموافقة", "مرفوضة"),
            selectedIndex = 0,
            selectedCount = 3,
            onSelect = {},
            modifier = Modifier.padding(vertical = spacing.spaceMedium),
        )
    }
}
