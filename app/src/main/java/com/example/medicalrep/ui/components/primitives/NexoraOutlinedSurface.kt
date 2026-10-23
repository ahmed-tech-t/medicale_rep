package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.NexoraDimens

@Composable
fun NexoraOutlinedSurface(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") elevated: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit,
) {
    NexoraCard(
        modifier = modifier,
        borderColor = borderColor,
        containerColor = containerColor,
        contentPadding = PaddingValues(NexoraDimens.spaceMd),
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
