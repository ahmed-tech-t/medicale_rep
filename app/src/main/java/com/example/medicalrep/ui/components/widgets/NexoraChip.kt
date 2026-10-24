package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.medicalrep.ui.ui.theme.BrandBorder
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.ErrorBright
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Small rounded label with custom container/content colors and an optional border. */
@Composable
fun NexoraChip(
    text: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(radii.small),
    border: Color? = null,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(container)
            .then(
                if (border != null) Modifier.border(DividerDefaults.Thickness, border, shape)
                else Modifier
            )
            .padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceExtraSmall),
        contentAlignment = Alignment.Center,
    ) {
        NexoraText(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NexoraChipPreview() {
    MedicalRepTheme {
        Row(
            modifier = Modifier.padding(spacing.spaceSmall),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            NexoraChip(text = "Class A", container = InfoContainer, content = Info)
            NexoraChip(text = "AM", container = BrandTint, content = Info, border = BrandBorder)
            NexoraChip(text = "View location", container = ErrorBright, content = Slate50)
        }
    }
}
