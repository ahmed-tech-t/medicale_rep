package com.example.medicalrep.ui.screens.home.components

import com.appcore.core.ui.theme.spacing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.StaticsGiftsFakes
import com.example.medicalrep.ui.models.StaticsGiftsItemUi
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Ink

@Composable
fun StaticsGiftsSummaryCard(
    items: List<StaticsGiftsItemUi>,
    modifier: Modifier = Modifier,
) {
    NexoraCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = Slate200.copy(alpha = 0.4f))
                }
                StaticsGiftsSummaryItemRow(item = item)
            }
        }
    }
}

@Composable
private fun StaticsGiftsSummaryItemRow(
    item: StaticsGiftsItemUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
        ) {
            NexoraText(
                text = stringResource(item.titleRes),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                ),
                color = Ink,
            )
            NexoraText(
                text = stringResource(
                    item.receivedFormatRes,
                    item.receivedQuantity,
                    item.skuCount,
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                ),
                color = Slate400,
            )
        }
        Column {
            NexoraText(
                text = stringResource(R.string.statics_custody_remaining_format, item.remaining),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                ),
                color = Ink,
            )
            NexoraText(
                text = stringResource(R.string.statics_custody_dispatched_format, item.dispatched),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                ),
                color = Slate400,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun StaticsGiftsSummaryCardPreview() {
    MedicalRepTheme {
        StaticsGiftsSummaryCard(items = StaticsGiftsFakes.items)
    }
}
