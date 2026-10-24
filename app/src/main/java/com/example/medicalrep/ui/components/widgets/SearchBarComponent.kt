package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/**
 * Plain search bar (same look as [SearchFieldComponent] without the dropdown): starts with [query],
 * reports every typed change through [onQueryChange], shows a search icon, or a clear button once there is text.
 */
@Composable
fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
) {
    val state = rememberTextFieldState(query)
    val text = state.text.toString()

    LaunchedEffect(text) { onQueryChange(text) }

    NexoraTextField(
        modifier = modifier.fillMaxWidth(),
        state = state,
        placeholder = placeholder,
        trailing = {
            if (text.isNotBlank()) {
                CloseIconComponent(
                    modifier = Modifier
                        .padding(horizontal = spacing.spaceMedium)
                        .clickable { state.clearText() },
                )
            } else {
                SearchIcon(modifier = Modifier.padding(horizontal = spacing.spaceMedium))
            }
        },
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SearchBarComponentPreview() {
    MedicalRepTheme {
        SearchBarComponent(
            query = "",
            onQueryChange = {},
            placeholder = "ابحث هنا.....",
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarComponentWithTextPreview() {
    MedicalRepTheme {
        SearchBarComponent(
            query = "Panadol",
            onQueryChange = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
