package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.ui.ui.theme.BrandFocus
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.NexoraDimens

/**
 * Controlled text field — value/focus/error owned by parent/ViewModel.
 */
@Composable
fun NexoraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isPassword: Boolean = false,
    isFocusedStyle: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val shape = RoundedCornerShape(NexoraDimens.radiusMd)
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocusedStyle -> BrandFocus
        else -> MaterialTheme.colorScheme.outline
    }
    val borderWidth = if (isFocusedStyle || isError) NexoraDimens.borderFocus else NexoraDimens.borderThin
    val container = if (isFocusedStyle) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(NexoraDimens.spaceXs),
    ) {
        if (!label.isNullOrBlank()) {
            NexoraLabel(text = label)
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(borderWidth, borderColor, shape),
            enabled = enabled,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.titleSmall.copy(
                color = Ink,
                textAlign = TextAlign.Start,
            ),
            placeholder = placeholder?.let {
                {
                    NexoraText(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            trailingIcon = trailing,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            shape = shape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = container,
                unfocusedContainerColor = container,
                disabledContainerColor = container,
                errorContainerColor = container,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            isError = isError,
        )
    }
}

/**
 * Bare text field on [TextFieldState] — chrome/icons owned by parent when nested.
 */
@Composable
fun NexoraTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink),
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    BasicTextField(
        state = state,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        cursorBrush = SolidColor(BrandFocus),
        lineLimits = if (singleLine) {
            TextFieldLineLimits.SingleLine
        } else {
            TextFieldLineLimits.Default
        },
        decorator = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(NexoraDimens.spaceSm),
            ) {
                leadingIcon?.invoke()
                Box(modifier = Modifier.weight(1f)) {
                    if (state.text.isEmpty() && !placeholder.isNullOrBlank()) {
                        NexoraText(
                            text = placeholder,
                            style = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        )
                    }
                    innerTextField()
                }
                trailingIcon?.invoke()
            }
        },
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NexoraTextFieldPreview() {
    MedicalRepTheme {
        Column(verticalArrangement = Arrangement.spacedBy(NexoraDimens.spaceMd)) {
            NexoraTextField(
                value = "9999",
                onValueChange = {},
                label = "كود الشركة",
                isFocusedStyle = true,
            )
            NexoraTextField(
                value = "ahmed.medrep",
                onValueChange = {},
                label = "اسم المستخدم",
            )
            NexoraTextField(
                state = rememberTextFieldState("مج حراري"),
                placeholder = "ابحث…",
            )
        }
    }
}
