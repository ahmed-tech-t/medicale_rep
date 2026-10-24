package com.example.medicalrep.ui.components.chrome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandSoft
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate800
import com.example.medicalrep.ui.ui.theme.TabIndicator

private val IndicatorThickness = 2.dp

/** Tab row for the Brand header: selected tab is white with a blue underline. */
@Composable
fun NexoraTabs(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Brand)
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawLine(
                    color = Slate800,
                    start = Offset(0f, size.height - stroke / 2),
                    end = Offset(size.width, size.height - stroke / 2),
                    strokeWidth = stroke,
                )
            }
            .padding(horizontal = spacing.spaceSmall),
    ) {
        labels.forEachIndexed { index, label ->
            NexoraTab(
                label = label,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun NexoraTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .drawBehind {
                if (selected) {
                    val stroke = IndicatorThickness.toPx()
                    drawLine(
                        color = TabIndicator,
                        start = Offset(0f, size.height - stroke / 2),
                        end = Offset(size.width, size.height - stroke / 2),
                        strokeWidth = stroke,
                    )
                }
            }
            .padding(horizontal = spacing.spaceExtraSmall, vertical = spacing.spaceSmallPlus),
        contentAlignment = Alignment.Center,
    ) {
        NexoraText(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = if (selected) Slate50 else BrandSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraTabsFirstSelectedPreview() {
    MedicalRepTheme { NexoraTabs(labels = listOf("العينات", "الهدايا"), selectedIndex = 0, onSelect = {}) }
}

@Preview(showBackground = true)
@Composable
private fun NexoraTabsSecondSelectedPreview() {
    MedicalRepTheme { NexoraTabs(labels = listOf("Samples", "Gifts"), selectedIndex = 1, onSelect = {}) }
}
