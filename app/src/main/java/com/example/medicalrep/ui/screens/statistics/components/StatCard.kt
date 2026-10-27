package com.example.medicalrep.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate500

/** A name and the value shown under it. */
@Immutable
data class StatItem(val label: String, val value: String)

/** One number with its name above it. */
@Composable
fun StatCard(item: StatItem, modifier: Modifier = Modifier) {
    NexoraCard(modifier = modifier, borderColor = Slate200) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            NexoraText(text = item.label, style = MaterialTheme.typography.labelLarge, color = Slate500)
            NexoraText(
                text = item.value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun StatCardPreview() {
    MedicalRepTheme { StatCard(item = StatItem("الأطباء", "14"), modifier = Modifier.padding(spacing.spaceMedium)) }
}
