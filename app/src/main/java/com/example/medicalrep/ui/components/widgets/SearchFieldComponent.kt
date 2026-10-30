package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate300
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate800
import com.example.medicalrep.ui.ui.theme.Slate900

/**
 * Dropdown select with an integrated search popup (matching Figma design):
 * - Trigger: Closed search bar with leading magnifying glass, placeholder or chosen value, and trailing dropdown arrow.
 * - Dropdown: Floating Popup with a top pointer triangle, inner search input, and a divided list of options.
 * - Multi-select: when [onConfirm] is set, rows get a checkbox and a done button returns every ticked option;
 *   otherwise tapping a row returns it through [onSelect].
 */
@Composable
private fun <T : SearchableItem> SearchFieldBodyComponent(
    options: List<T>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: ((List<T>) -> Unit)? = null,
    selected: List<T> = emptyList(),
    subtitle: @Composable (T) -> String? = { null },
) {
    val state = rememberTextFieldState()
    val query = state.text.toString()
    val ticked = remember { mutableStateListOf<T>().apply { addAll(selected) } }

    val result = remember(query, options) {
        if (query.isBlank()) {
            options
        } else {
            options.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Inner outlined search input box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = Slate300,
                    shape = RoundedCornerShape(12.dp),
                )
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NexoraIcon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier.size(18.dp),
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (state.text.isEmpty()) {
                    NexoraText(
                        text = stringResource(R.string.products_search_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400,
                        maxLines = 1
                    )
                }
                BasicTextField(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate900,
                        fontWeight = FontWeight.Medium,
                    ),
                  lineLimits = TextFieldLineLimits.SingleLine,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                )
            }
            if (state.text.isNotEmpty()) {
                CloseIconComponent(
                    modifier = Modifier.clickable {
                        state.clearText()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Options List with dividers
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp),
        ) {
            if (result.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        NexoraText(
                            text = stringResource(R.string.no_result),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate400,
                        )
                    }
                }
            } else {
                itemsIndexed(result) { index, item ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (onConfirm == null) onSelect(item)
                                    else if (item in ticked) ticked.remove(item) else ticked.add(item)
                                }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
                        ) {
                            if (onConfirm != null) Checkbox(checked = item in ticked, onCheckedChange = null)
                            Column(modifier = Modifier.weight(1f)) {
                                NexoraText(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Start,
                                    ),
                                    color = Slate900,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                subtitle(item)?.let {
                                    NexoraText(
                                        text = it,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400,
                                    )
                                }
                            }
                        }
                        if (index < result.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = Slate100,
                            )
                        }
                    }
                }
            }
        }
        if (onConfirm != null) {
            NexoraButton(
                text = stringResource(R.string.action_done),
                onClick = { onConfirm(ticked.toList()) },
                enabled = ticked.isNotEmpty(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}

private val PopupSpace = 360.dp

/**
 * While the keyboard is up and the popup below this anchor is open, scrolls the parent so the anchor
 * and the popup space under it are on screen. The parent must have room to scroll (see PlanVisitUi).
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun Modifier.revealPopupAboveKeyboard(popupOpen: Boolean): Modifier {
    val requester = remember { BringIntoViewRequester() }
    var anchorSize by remember { mutableStateOf(IntSize.Zero) }
    val popupSpacePx = with(LocalDensity.current) { PopupSpace.toPx() }
    val keyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(popupOpen, keyboardVisible, anchorSize) {
        if (popupOpen && keyboardVisible && anchorSize != IntSize.Zero) {
            requester.bringIntoView(Rect(0f, 0f, anchorSize.width.toFloat(), anchorSize.height + popupSpacePx))
        }
    }
    return this
        .onSizeChanged { anchorSize = it }
        .bringIntoViewRequester(requester)
}

@Composable
fun <T : SearchableItem> SearchFieldComponent(
    value: String,
    options: List<T>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    showPopup: Boolean,
    badge: (@Composable () -> Unit)? = null,
    /** Set for a multi-select field: rows get checkboxes, [selected] starts ticked and Done returns every ticked option. */
    onConfirm: ((List<T>) -> Unit)? = null,
    selected: List<T> = emptyList(),
) {
    var triggerSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Box(modifier = modifier.revealPopupAboveKeyboard(showPopup && enabled)) {
        // Trigger field (matches the top field in design)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    triggerSize = coordinates.size
                }
                .clip(RoundedCornerShape(radii.medium))
                .background(Slate25)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(radii.medium),
                )
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = spacing.spaceMedium, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            NexoraText(
                text = value.ifBlank { placeholder.orEmpty() },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (value.isBlank()) FontWeight.Normal else FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                ),
                color = if (value.isBlank()) Slate400 else Slate800,
                modifier = Modifier.weight(1f),
            )
            if (badge != null) {
                badge()
            }
            NexoraIcon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier
                    .size(dim.smallIconSize)
                    .then(
                        if (showPopup) Modifier.rotate(180f) else Modifier
                    ),
            )
        }
        // Floating dropdown popup
        if (showPopup && enabled) {
            SearchFieldPopup(
                triggerSize = triggerSize,
                options = options,
                onClick = onClick,
                onSelect = onSelect,
                onConfirm = onConfirm,
                selected = selected,
            )
        }
    }
}

@Composable
fun <T : SearchableItem> SearchFieldPopup(
    triggerSize: IntSize,
    options: List<T>,
    onSelect: (T) -> Unit,
    onClick: () -> Unit,
    onConfirm: ((List<T>) -> Unit)? = null,
    selected: List<T> = emptyList(),
    subtitle: @Composable (T) -> String? = { null },
) {
    val density = LocalDensity.current

    val popupOffsetY = if (triggerSize.height > 0) {
        triggerSize.height + with(density) { 4.dp.roundToPx() }
    } else {
        with(density) { 56.dp.roundToPx() }
    }
    Popup(
        offset = IntOffset(x = 0, y = popupOffsetY),
        onDismissRequest = onClick,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Surface(
            shape = RoundedCornerShape(radii.medium),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = if (triggerSize.width > 0) {
                Modifier.width(with(density) { triggerSize.width.toDp() })
            } else {
                Modifier.fillMaxWidth()
            },
        ) {
            SearchFieldBodyComponent(
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
                options = options,
                onSelect = onSelect,
                onConfirm = onConfirm,
                selected = selected,
                subtitle = subtitle,
            )
        }
    }
}

