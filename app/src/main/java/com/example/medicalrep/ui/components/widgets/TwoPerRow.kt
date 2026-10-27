package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.appcore.core.ui.theme.spacing

/** The cards two to a row; a last card alone keeps half the width. [card] gets the weight modifier it must use. */
@Composable
fun <T> TwoPerRow(items: List<T>, modifier: Modifier = Modifier, card: @Composable (T, Modifier) -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
        items.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
                row.forEach { item -> card(item, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
