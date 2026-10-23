package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.theme.radii
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/**
 * Shared card chrome — body slot + optional click; border/container colors customizable.
 */
@Composable
fun NexoraCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(spacing.spaceSmallPlus),
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radii.extraLarge)
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val border = BorderStroke(DividerDefaults.Thickness, borderColor)
    val body: @Composable () -> Unit = {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            border = border,
            content = { body() },
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            border = border,
            content = { body() },
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraCardPreview() {
    MedicalRepTheme {
        NexoraCard {
            NexoraText(text = "بطاقة افتراضية")
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraCardCustomBorderPreview() {
    MedicalRepTheme {
        NexoraCard(borderColor = MaterialTheme.colorScheme.primary) {
            NexoraText(text = "حدود مخصصة")
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraCardClickablePreview() {
    MedicalRepTheme {
        NexoraCard(onClick = {}) {
            NexoraText(text = "قابلة للضغط")
        }
    }
}
