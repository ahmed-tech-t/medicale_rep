package com.example.medicalrep.ui.screens.visitdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.DetailsLabel
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate800

/** Muted [label] at the start and any [content] (text or chip) at the end. */
@Composable
fun InfoRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(text = label, style = MaterialTheme.typography.bodyMedium, color = DetailsLabel)
        content()
    }
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    InfoRow(label = label, modifier = modifier) {
        NexoraText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate800,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun InfoRowPreview() {
    MedicalRepTheme { InfoRow(label = "المنطقة", value = "Heliopolis", modifier = Modifier.padding(spacing.spaceMedium)) }
}
