package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25

@Composable
fun NexoraOutlinedSurface(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") elevated: Boolean = false,
    containerColor: Color = Slate25,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit,
) {
    NexoraCard(
        modifier = modifier,
        borderColor = borderColor,
        containerColor = containerColor,
        contentPadding = PaddingValues(spacing.spaceSmallPlus),
        content = content,
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraOutlinedSurfacePreview() {
    MedicalRepTheme {
        NexoraOutlinedSurface(elevated = true) {
            NexoraText("بطاقة مشتركة")
        }
    }
}
