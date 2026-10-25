package com.example.medicalrep.ui.components.primitives

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.ui.theme.BrandFocus
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate25

/**
 * Labeled text field on [TextFieldState] — the state (text, selection) is owned by the ViewModel,
 * focus/error styling by the parent.
 */
@Composable
fun NexoraTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isPassword: Boolean = false,
    isFocusedStyle: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
    style: TextStyle =MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Start,
    ),
    singleLine: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val shape = RoundedCornerShape(radii.large)
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocusedStyle -> BrandFocus
        else -> MaterialTheme.colorScheme.outline
    }
    val borderWidth =
        if (isFocusedStyle || isError) spacing.spaceExtraExtraSmall else DividerDefaults.Thickness
    val container = Slate25
    val fieldModifier = Modifier
        .fillMaxWidth()
        .border(borderWidth, borderColor, shape)
        .heightIn(min = dim.primaryButtonHeight)
    val placeholderContent: (@Composable () -> Unit)? = placeholder?.let {
        {
            NexoraText(
                text = it,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Start,
                modifier = Modifier,
            )
        }
    }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = container,
        unfocusedContainerColor = container,
        disabledContainerColor = container,
        errorContainerColor = container,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,

        )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        if (!label.isNullOrBlank()) {
            NexoraLabel(text = label, style = MaterialTheme.typography.bodyMedium)
        }
        if (isPassword) {
            var passwordVisible by rememberSaveable { mutableStateOf(false) }
            SecureTextField(
                state = state,
                modifier = fieldModifier,
                enabled = enabled,
                textStyle = style,
                placeholder = placeholderContent,
                trailingIcon = trailing ?: {
                    NexoraIconButton(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(
                            if (passwordVisible) R.string.cd_hide_password else R.string.cd_show_password
                        ),
                        onClick = { passwordVisible = !passwordVisible },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                textObfuscationMode = if (passwordVisible) {
                    TextObfuscationMode.Visible
                } else {
                    TextObfuscationMode.Hidden
                },
                isError = isError,
                keyboardOptions = keyboardOptions,
                shape = shape,
                colors = colors,
            )
        } else {
            TextField(
                state = state,
                modifier = fieldModifier,
                enabled = enabled,
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                    top = spacing.spaceSmall,
                    bottom = spacing.spaceSmall,
                ),
                textStyle = style,
                placeholder = placeholderContent,
                trailingIcon = trailing,
                isError = isError,
                keyboardOptions = keyboardOptions,
                lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else TextFieldLineLimits.Default,
                shape = shape,
                colors = colors,
            )
        }
    }
}

/**
 * Bare text field on [TextFieldState] — chrome/icons owned by parent when nested.
 */
@Composable
fun NexoraBareTextField(
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
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
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
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
            NexoraTextField(
                state = rememberTextFieldState("9999"),
                label = "كود الشركة",
                isFocusedStyle = true,
            )
            NexoraTextField(
                state = rememberTextFieldState(""),
                placeholder = "اسم المستخدم",
            )
            NexoraTextField(
                state = rememberTextFieldState("secret"),
                label = "كلمة المرور",
                isPassword = true,
            )
            NexoraBareTextField(
                state = rememberTextFieldState("مج حراري"),
                placeholder = "ابحث…",
            )
        }
    }
}
