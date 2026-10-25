package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50

/**
 * Select without typing: the chosen value with an arrow, options in a dropdown menu.
 * [optionLabel] maps a stored option to the text shown for it.
 */
@Composable
fun SelectFieldComponent(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    optionLabel: @Composable (String) -> String = { it },
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(radii.large)
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(Slate25)
                .border(DividerDefaults.Thickness, MaterialTheme.colorScheme.outline, shape)
                .heightIn(min = dim.primaryButtonHeight)
                .clickable(role = Role.DropdownList) { expanded = true }
                .padding(horizontal = spacing.spaceSmallPlus),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraText(
                text = if (value.isBlank()) placeholder.orEmpty() else optionLabel(value),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (value.isBlank()) Slate400 else MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            NexoraIcon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Slate400,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Slate50,
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { NexoraText(text = optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SelectFieldComponentPreview() {
    MedicalRepTheme {
        Row(modifier = Modifier.padding(spacing.spaceMedium)) {
            SelectFieldComponent(value = "A", options = listOf("A", "B", "C"), onSelect = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectFieldComponentEmptyPreview() {
    MedicalRepTheme {
        Row(modifier = Modifier.padding(spacing.spaceMedium)) {
            SelectFieldComponent(value = "", options = listOf("A", "B", "C"), onSelect = {}, placeholder = "Class")
        }
    }
}
