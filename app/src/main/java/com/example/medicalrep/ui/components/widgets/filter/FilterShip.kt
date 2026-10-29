package com.example.medicalrep.ui.components.widgets.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate300

@Composable
fun FilterShip(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    text: String,
    shape: Shape = RoundedCornerShape(radii.pill),
    /** Space between the border and the text; none for a fixed-size circle. */
    contentPadding: PaddingValues = PaddingValues(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall),

    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(
                    alpha = .15f
                ) else Color.Unspecified
            )
            .border(
                width = if (isSelected) DividerDefaults.Thickness * 2 else DividerDefaults.Thickness,
                color = if (isSelected) Brand else Slate300,
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        NexoraText(
            modifier = Modifier.padding(contentPadding),
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) Brand else Ink,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterShipPreview() {
    MedicalRepTheme {
        Row(
            modifier = Modifier.padding(spacing.spaceSmall),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            FilterShip(
                isSelected = true,
                text = "A dsdf sdffsd",
                onClick = {}
            )
            FilterShip(
                isSelected = false,
                text = "B",
                onClick = {}
            )
        }
    }
}
