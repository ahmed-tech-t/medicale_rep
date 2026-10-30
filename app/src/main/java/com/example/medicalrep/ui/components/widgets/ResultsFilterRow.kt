package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** "19 results" with the filter button at the opposite end; the button turns primary while a filter is on. */
@Composable
fun ResultsFilterRow(
    count: Int,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFiltered: Boolean = false,
    filterMenu: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(
            text = stringResource(R.string.visit_log_results_format, count),
            style = MaterialTheme.typography.labelLarge,
            color = Ink,
        )
        // The menu sits in the same Box so it opens right under the icon.
        Box {
            NexoraIconButton(
                painter = painterResource(R.drawable.ic_filter_list),
                contentDescription = stringResource(R.string.cd_filter),
                onClick = onFilterClick,
                tint = if (isFiltered) MaterialTheme.colorScheme.primary else Ink,
            )
            filterMenu()
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ResultsFilterRowPreview() {
    MedicalRepTheme { ResultsFilterRow(count = 19, onFilterClick = {}, modifier = Modifier.padding(spacing.spaceMedium)) }
}
