package com.example.medicalrep.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.domain.models.ClassShare
import com.example.medicalrep.domain.models.Statistics
import com.example.medicalrep.ui.screens.statistics.utils.StatisticsFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate600

/** The accounts of each class: its name, how many accounts, their share in percent and a bar of that share. */
@Composable
fun ClassDistributionCard(statistics: Statistics, modifier: Modifier = Modifier) {
    NexoraCard(modifier = modifier, borderColor = Slate200) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)) {
            statistics.classes.forEach { share -> ClassRow(share = share, percent = statistics.percentOf(share)) }
        }
    }
}

@Composable
private fun ClassRow(share: ClassShare, percent: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraChip(
                text = stringResource(R.string.statistics_class_format, share.grade),
                container = Slate100,
                content = Slate600,
                shape = RoundedCornerShape(radii.pill),
                border = Slate200,
            )
            NexoraText(
                text = pluralStringResource(R.plurals.statistics_accounts_count, share.accounts, share.accounts),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Ink,
                modifier = Modifier.weight(1f),
            )
            NexoraText(text = "$percent%", style = MaterialTheme.typography.labelMedium, color = Slate400)
        }
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(radii.pill)),
            color = Brand,
            trackColor = Slate100,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ClassDistributionCardPreview() {
    MedicalRepTheme { ClassDistributionCard(statistics = StatisticsFakes.state().statistics, modifier = Modifier.padding(spacing.spaceMedium)) }
}
