package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.appcore.core.ui.theme.radii
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.Slate600

private val CompactButtonHeight = 36.dp

enum class NexoraButtonVariant {
    Primary,
    Secondary,

    /** Filled chip surface — cancel / low-emphasis actions. */
    Muted,
    Text,
}

/**
 * Controlled button — label/enabled/clicks come from parent/ViewModel.
 */
@Composable
fun NexoraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: NexoraButtonVariant = NexoraButtonVariant.Primary,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = true,
    compact: Boolean = false,
    border: BorderStroke? = null,
    textStyle: TextStyle? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
) {

    val shape = RoundedCornerShape(radii.extraLarge)
    val widthModifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier
    val minHeight = if (compact) CompactButtonHeight else dim.primaryButtonHeight
    val minSize = Modifier.defaultMinSize(minHeight = minHeight)
    val contentPadding = if (compact) {
        PaddingValues(horizontal = spacing.spaceSmallPlus, vertical = spacing.spaceSmall)
    } else {
        PaddingValues(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmallPlus)
    }
    val labelStyle = textStyle ?: if (compact) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }

    when (variant) {
        NexoraButtonVariant.Primary -> Button(
            onClick = onClick,
            enabled = enabled,
            border = border,
            modifier = widthModifier.then(minSize),
            shape = shape,
            contentPadding = contentPadding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        ) {
            NexoraButtonLabel(text = text, style = labelStyle, leadingIcon = leadingIcon)
        }


        NexoraButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = widthModifier.then(minSize),
            shape = shape,
            contentPadding = contentPadding,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = Ink,
            ),
        ) {
            NexoraButtonLabel(text = text, style = labelStyle, leadingIcon = leadingIcon)
        }

        NexoraButtonVariant.Muted -> Button(
            onClick = onClick,
            enabled = enabled,
            modifier = widthModifier.then(minSize),
            shape = shape,
            contentPadding = contentPadding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = Slate600,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        ) {
            NexoraButtonLabel(
                text = text,
                style = labelStyle,
                color = Slate600,
                leadingIcon = leadingIcon,
            )
        }

        NexoraButtonVariant.Text -> TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = widthModifier,
            contentPadding = contentPadding,
        ) {
            NexoraButtonLabel(
                text = text,
                style = labelStyle,
                color = MaterialTheme.colorScheme.primary,
                leadingIcon = leadingIcon,
            )
        }
    }
}

@Composable
private fun NexoraButtonLabel(
    text: String,
    style: TextStyle,
    color: Color = Color.Unspecified,
    leadingIcon: (@Composable () -> Unit)?,
) {
    if (leadingIcon == null) {
        NexoraText(text = text, style = style, color = color)
        return
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall, Alignment.CenterHorizontally),
    ) {
        leadingIcon()
        NexoraText(text = text, style = style, color = color)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun NexoraButtonPreview() {
    com.example.medicalrep.ui.ui.theme.MedicalRepTheme {
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
        ) {
            NexoraButton(
                text = "Primary Button",
                onClick = {},
                variant = NexoraButtonVariant.Primary
            )
            NexoraButton(
                text = "Secondary Button",
                onClick = {},
                variant = NexoraButtonVariant.Secondary
            )
            NexoraButton(
                text = "Muted Button",
                onClick = {},
                variant = NexoraButtonVariant.Muted
            )
            NexoraButton(
                text = "Text Button",
                onClick = {},
                variant = NexoraButtonVariant.Text
            )
            NexoraButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false
            )
            NexoraButton(
                text = "Compact Button",
                onClick = {},
                compact = true
            )
        }
    }
}

