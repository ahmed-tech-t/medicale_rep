package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200

/** The search box with the square filter button at its end (Figma 622:1187); the button turns primary while a filter is on. */
@Composable
fun SearchFilterRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isFiltered: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchBarComponent(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = placeholder,
            modifier = Modifier.weight(1f),
        )
        val shape = RoundedCornerShape(radii.large)
        Box(
            modifier = Modifier
                .size(dim.iconButtonSize)
                .clip(shape)
                .border(DividerDefaults.Thickness, if (isFiltered) MaterialTheme.colorScheme.primary else Slate200, shape)
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center,
        ) {
            NexoraIcon(
                painter = painterResource(R.drawable.ic_filter_list),
                contentDescription = stringResource(R.string.cd_filter),
                tint = if (isFiltered) MaterialTheme.colorScheme.primary else Ink,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SearchFilterRowPreview() {
    MedicalRepTheme {
        SearchFilterRow(
            query = "",
            onQueryChange = {},
            onFilterClick = {},
            placeholder = "ابحث بالطبيب، التخصص أو المنشأة...",
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchFilterRowFilteredPreview() {
    MedicalRepTheme {
        SearchFilterRow(query = "Mona", onQueryChange = {}, onFilterClick = {}, isFiltered = true, modifier = Modifier.padding(spacing.spaceMedium))
    }
}
