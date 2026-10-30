package com.example.medicalrep.ui.screens.sampleandgifts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.models.CustodyUi
import com.example.medicalrep.ui.screens.sampleandgifts.utils.SampleAndGiftsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate900

/** Figma 57:12182 "Inventory card": item name, received / dispatched counts and a remaining-pieces pill. */
@Composable
fun SampleAndGiftsItemCard(
    item: CustodyUi,
    modifier: Modifier = Modifier,
) {
    NexoraCard(
        modifier = modifier,
        containerColor = Slate50,
        borderColor = Slate200,
        contentPadding = PaddingValues(spacing.spaceSmallPlus),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NexoraText(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Slate900,
                )
                ReceivedDispatchedRow(received = item.received, dispatched = item.received - item.remaining)
            }
            NexoraChip(
                text = stringResource(R.string.sample_and_gifts_pieces_format, item.remaining),
                container = Slate100,
                content = Slate600,
                shape = RoundedCornerShape(radii.pill),
            )
        }
    }
}

@Composable
private fun ReceivedDispatchedRow(received: Int, dispatched: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CountLabel(stringResource(R.string.sample_and_gifts_received_label))
        CountValue(received)
        CountLabel(stringResource(R.string.sample_and_gifts_dispatched_label))
        CountValue(dispatched)
    }
}

@Composable
private fun CountLabel(text: String) {
    NexoraText(text = text, style = MaterialTheme.typography.labelSmall, color = Slate500)
}

@Composable
private fun CountValue(value: Int) {
    NexoraText(
        text = value.toString(),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = Slate500,
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SampleAndGiftsItemCardSamplePreview() {
    MedicalRepTheme {
        SampleAndGiftsItemCard(
            item = SampleAndGiftsFakes.samples.first(),
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleAndGiftsItemCardGiftPreview() {
    MedicalRepTheme {
        SampleAndGiftsItemCard(
            item = SampleAndGiftsFakes.gifts.first(),
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
