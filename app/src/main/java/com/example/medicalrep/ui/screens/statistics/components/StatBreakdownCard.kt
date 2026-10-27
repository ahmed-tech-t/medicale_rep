package com.example.medicalrep.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400

/** A number with its name above it; [StatBreakdownCard] shows several side by side. */
@Immutable
data class StatColumn(val label: String, val value: Int)

/** A title and a few labelled numbers in a row, for a stock: what came in, what went out, what is left. */
@Composable
fun StatBreakdownCard(title: String, columns: List<StatColumn>, modifier: Modifier = Modifier) {
    NexoraCard(modifier = modifier, borderColor = Slate200) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)) {
            NexoraText(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                columns.forEach { column ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
                        NexoraText(text = column.label, style = MaterialTheme.typography.labelSmall, color = Slate400)
                        NexoraText(
                            text = column.value.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Ink,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun StatBreakdownCardPreview() {
    MedicalRepTheme {
        StatBreakdownCard(
            title = "عينات الأدوية",
            columns = listOf(StatColumn("المستلم", 250), StatColumn("المستخدم", 165), StatColumn("المتبقي", 85)),
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
