package com.example.medicalrep.ui.screens.unplannedvisit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** Figma "Class A" chip next to the place name. */
@Composable
fun ClassBadge(text: String, modifier: Modifier = Modifier) {
    NexoraText(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = Info,
        modifier = modifier
            .clip(RoundedCornerShape(radii.small))
            .background(InfoContainer)
            .padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceExtraExtraSmall),
    )
}

@Preview(showBackground = true)
@Composable
private fun ClassBadgePreview() {
    MedicalRepTheme {
        ClassBadge(text = "Class A", modifier = Modifier.padding(spacing.spaceSmall))
    }
}
