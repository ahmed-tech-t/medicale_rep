package com.example.medicalrep.ui.components.widgets.visit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Ink

@Composable
fun PlannedVisitCard(
    model: VisitUi,
    /** The Start button; null hides it (a missed visit can no longer be started). */
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .visitCardChrome(elevation = 2.dp, shadowAlpha = 0.02f)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            ) {
                NexoraText(
                    text = model.resolvedDisplayName(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    ),
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (model.specialty.isNotBlank()) {
                    VisitTagChip(text = model.specialty)
                }
            }
            if (model.address.isNotBlank()) {
                NexoraText(
                    text = model.address,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Slate500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (onAction != null) {
            NexoraButton(
                text = stringResource(R.string.visit_action_start),
                onClick = onAction,
                fillMaxWidth = false,
                compact = true,
                leadingIcon = {
                    NexoraIcon(
                        painter = painterResource(R.drawable.ic_arrow_visit),
                        contentDescription = null,
                        modifier = Modifier.size(11.dp),
                        tint = Color.White,
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PlannedVisitCardPreview() {
    MedicalRepTheme {
        PlannedVisitCard(
            model = VisitSamples.unplanned().toUi(),
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
