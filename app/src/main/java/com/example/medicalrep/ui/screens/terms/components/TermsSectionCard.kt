package com.example.medicalrep.ui.screens.terms.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.terms.models.TermsSectionUi
import com.example.medicalrep.ui.screens.terms.utils.TermsSections
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandBorder
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.CardBorderCool
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate900

/** One numbered term: round number badge, bold title and the body text. */
@Composable
fun TermsSectionCard(number: Int, section: TermsSectionUi, modifier: Modifier = Modifier) {
    NexoraCard(modifier = modifier, containerColor = Slate50, borderColor = CardBorderCool) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
            Box(
                modifier = Modifier
                    .size(spacing.spaceLarge)
                    .background(BrandTint, CircleShape)
                    .border(1.dp, BrandBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                NexoraText(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Brand,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
                NexoraText(
                    text = stringResource(section.titleRes),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate900,
                )
                NexoraText(
                    text = stringResource(section.bodyRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate500,
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun TermsSectionCardPreview() {
    MedicalRepTheme { TermsSectionCard(number = 1, section = TermsSections.all.first()) }
}
