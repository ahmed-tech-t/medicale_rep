package com.example.medicalrep.ui.components.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** One row of a [FilterMenu]; [iconRes] is optional. */
data class FilterOption<T>(
    val value: T,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int? = null,
)

/** Popup under a filter icon: one row per option, a check on the [selected] one. */
@Composable
fun <T> FilterMenu(
    expanded: Boolean,
    options: List<FilterOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = Color.White,
        shape = RoundedCornerShape(radii.large),
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { NexoraText(text = stringResource(option.labelRes), color = Ink) },
                onClick = { onSelect(option.value) },
                leadingIcon = {
                    // Keeps the labels aligned: the check is invisible when the option is not selected.
                    NexoraIcon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (option.value == selected) Ink else Color.Transparent,
                    )
                },
                trailingIcon = option.iconRes?.let { icon ->
                    { NexoraIcon(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(16.dp), tint = Ink) }
                },
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 320)
@Composable
private fun FilterMenuPreview() {
    MedicalRepTheme {
        FilterMenu(
            expanded = true,
            options = listOf(
                FilterOption("all", R.string.filter_all),
                FilterOption("morning", R.string.filter_morning),
                FilterOption("evening", R.string.filter_evening),
            ),
            selected = "morning",
            onSelect = {},
            onDismiss = {},
        )
    }
}
