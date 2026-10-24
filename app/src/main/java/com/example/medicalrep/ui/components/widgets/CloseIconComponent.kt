package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.appcore.core.ui.theme.dim
import com.example.medicalrep.ui.components.primitives.NexoraIcon

@Composable
fun CloseIconComponent(modifier: Modifier = Modifier) {
    NexoraIcon(
        imageVector = Icons.Default.Close,
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier.size(dim.smallIconSize),
    )
}