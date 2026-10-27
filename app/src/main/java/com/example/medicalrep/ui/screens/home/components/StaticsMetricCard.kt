package com.example.medicalrep.ui.screens.home.components

import com.appcore.core.ui.theme.spacing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Ink

@Composable
fun StaticsMetricCard(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
) {
    NexoraCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
        ) {
            NexoraText(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = Slate500,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            NexoraText(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                ),
                color = Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun StaticsMetricCardPreview() {
    MedicalRepTheme {
        StaticsMetricCard(label = "الأطباء", value = 26)
    }
}
