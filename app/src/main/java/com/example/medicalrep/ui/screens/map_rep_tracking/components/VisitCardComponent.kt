package com.example.medicalrep.ui.screens.map_rep_tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.StatusBadge
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate500

@Composable
fun VisitCartComponent(
    visit: VisitUi,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NexoraCard(modifier = modifier, borderColor = Color.Unspecified) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            NexoraText(
                text = visit.placeName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            StatusBadge(visit.time)
        }
        NexoraText(
            visit.specialty?.name.orEmpty(),
            color = Slate500,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        NexoraButton(
            text = stringResource(R.string.visit_details_title),
            onClick = onViewDetails,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun VisitCartComponentPreview() {
    MedicalRepTheme {
        VisitCartComponent(
            visit = VisitSamples.planned().toUi(),
            onViewDetails = {},
        )
    }
}