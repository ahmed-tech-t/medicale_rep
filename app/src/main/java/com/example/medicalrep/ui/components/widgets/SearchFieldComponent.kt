package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.appcore.core.ui.components.modifier_ext.notchesPadding
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/**
 * Read-only select (Figma "Background+Border" fields): value at the start, arrow at the end,
 * options in a dropdown. [label] sits above the field when given.
 */
@Composable
fun SearchFieldComponent(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    badge: (@Composable () -> Unit)? = null,
) {
    val state = rememberTextFieldState(value)
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    val query = state.text.toString()

    LaunchedEffect(value) {
        if (state.text.toString() != value) state.setTextAndPlaceCursorAtEnd(value)
    }

    val result = remember(query, options) {
        options.filter { it.contains(query, ignoreCase = true) }
    }
    val showPopup = query.isNotBlank() && query != value && result.isNotEmpty()
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
        NexoraTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size
                },
            state = state,
            enabled = enabled,
            placeholder = placeholder,
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                    modifier = Modifier.padding(horizontal = spacing.spaceMedium)
                ) {
                    if (badge != null) {
                        badge()
                    }
                    if (state.text.isNotBlank() && enabled) CloseIconComponent(
                        modifier = Modifier.clickable(
                            onClick = {
                                state.clearText()
                                onSelect("")
                            })
                    ) else if (!enabled) Unit else SearchIcon()

                }

            },

            )
        if (showPopup) {
            Popup(
                // focusable = false ensures the text field keeps focus while the user types
                properties = PopupProperties(focusable = false),
                offset = IntOffset(x = 0, y = textFieldSize.height),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        // Convert the pixel width we captured back to Dp
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(radii.medium)), // Small gap between text field and dropdown
                    shadowElevation = 4.dp,
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp)
                    ) {
                        items(result) { item ->
                            NexoraText(
                                text = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        state.setTextAndPlaceCursorAtEnd(item)
                                        onSelect(item)
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, locale = "ar")
@Composable
private fun SearchFieldComponentPreview() {
    MedicalRepTheme {
        Column(
            modifier = Modifier
                .padding(spacing.spaceMedium)
                .notchesPadding(),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            SearchFieldComponent(
                modifier = Modifier.fillMaxWidth(),
                value = "شبين الكوم",
                options = listOf(
                    "ahmed mohamfed",
                    "ahmed mohamfed",
                    "ahmed mohamfed",
                    "ahmed mohamfed"
                ),
                onSelect = {},
            )

            SearchFieldComponent(
                value = "عيادة د. محمود عبد الرحمن عبد الله الطويل جدا",
                options = emptyList(),
                onSelect = {},
                badge = { NexoraChip(text = "Class A", container = InfoContainer, content = Info) },
            )
            SearchFieldComponent(
                value = "عيادة د. محمود عبد الرحمن عبد الله الطويل جدا",
                options = emptyList(),
                onSelect = {},
                enabled = false,
                badge = { NexoraChip(text = "Class A", container = InfoContainer, content = Info) },
            )
        }
    }
}
