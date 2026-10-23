package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate600

@Composable
fun NexoraLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    alignEnd: Boolean = false,
) {
    NexoraText(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = style,
        color = Ink,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
    )
}

@Composable
fun NexoraSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    alignEnd: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
    ) {
        NexoraText(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color =Ink  ,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        if (!subtitle.isNullOrBlank()) {
            NexoraText(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraLabelPreview() {
    MedicalRepTheme {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
            NexoraLabel(text = "كود الشركة")
            NexoraSectionTitle(
                title = "اسم المكان والتخصص",
                subtitle = "تظهر القائمة بناءً على المنطقة والنوع المختارين",
            )
        }
    }
}
