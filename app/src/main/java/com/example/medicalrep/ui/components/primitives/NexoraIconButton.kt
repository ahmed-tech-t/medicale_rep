package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/**
 * Single controlled icon-button atom. Enabled/clicks owned by parent/ViewModel.
 */
@Composable
fun NexoraIconButton(
    imageVector: ImageVector,
    contentDescription: String?= null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(contentColor = tint),
    ) {
        NexoraIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}


@Composable
fun NexoraIconButton(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    tint: Color = LocalContentColor.current,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape ,
        colors = IconButtonDefaults.iconButtonColors(contentColor = tint),
    ) {
        NexoraIcon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
/** Text-label icon button (e.g. + / − steppers) — still controlled by parent. */
@Composable
fun NexoraIconButton(
    label: String,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(contentColor = tint),
    ) {
        NexoraText(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = tint,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NexoraIconButtonPreview() {
    MedicalRepTheme {
        NexoraIconButton(
            imageVector = Icons.Default.Add,
            contentDescription = "add",
            onClick = {},
        )
    }
}
