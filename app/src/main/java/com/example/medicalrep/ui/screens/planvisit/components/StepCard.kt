package com.example.medicalrep.ui.screens.planvisit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate50

/** White bordered card used by every unplanned-visit section (Figma "Background+Border"). */
@Composable
fun StepCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(spacing.spaceSmall),
    content: @Composable ColumnScope.() -> Unit,

) {
    NexoraCard(
        modifier = modifier,
        containerColor = Slate50,
        borderColor = Slate200,
        contentPadding = contentPadding,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus), content = content)
    }
}

@Composable
fun StepCardTitle(text: String, modifier: Modifier = Modifier) {
    NexoraText(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = Ink,
        modifier = modifier,
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun StepCardPreview() {
    MedicalRepTheme {
        StepCard(modifier = Modifier.padding(spacing.spaceMedium)) {
            StepCardTitle("تحديد المكان والمنطقة الجغرافية")
            NexoraText("محتوى")
        }
    }
}
