package com.appcore.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String = "",
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp, vertical = 12.dp
    ),
    shape: Shape = RoundedCornerShape(8.dp),
    imeAction: ImeAction = ImeAction.Next,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall
        .copy(
            textAlign = TextAlign.Start,
            color = if (enabled) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),

    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    onKeyboardAction: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val containerColor = MaterialTheme.colorScheme.background

    BasicTextField(
        modifier = modifier
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.onSecondaryFixed, shape)
            .background(containerColor, shape),

        state = state,
        readOnly = !enabled,
        interactionSource = interactionSource,
        inputTransformation = inputTransformation,
        textStyle = textStyle,
        lineLimits = lineLimits,
        cursorBrush = SolidColor(textStyle.color),
        outputTransformation = outputTransformation,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            autoCorrect = false,
            keyboardType = keyboardType
        ),
        onKeyboardAction = onKeyboardAction?.let { action ->
            KeyboardActionHandler { action() }
        },
        decorator = { innerTextField ->
            Row(
                modifier = Modifier.padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (leadingIcon != null) leadingIcon()

                Box(modifier = Modifier.weight(1f), propagateMinConstraints = true) {
                    if (state.text.isEmpty()) {
                        CText(
                            maxLines = 1,
                            text = placeholder,
                            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Start),
                            textColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    innerTextField()
                }

                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}

@Composable
fun CPasswordTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String = "",
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp, vertical = 12.dp
    ),
    shape: Shape = RoundedCornerShape(8.dp),
    iconSize: Dp = 20.dp,
    imeAction: ImeAction = ImeAction.Next,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall
        .copy(
            textAlign = TextAlign.Start,
            color = if (enabled) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),
) {
    val containerColor = MaterialTheme.colorScheme.background
    var visible by remember { mutableStateOf(false) }

    BasicSecureTextField(
        modifier = modifier
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.onSecondaryFixed, shape)
            .background(containerColor, shape),

        state = state,
        readOnly = !enabled,
        textStyle = textStyle,
        textObfuscationMode = if (visible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.Hidden
        },
        cursorBrush = SolidColor(textStyle.color),
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            autoCorrect = false,
        ),
        decorator = { innerTextField ->
            Row(
                modifier = Modifier.padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconComponent(
                    size = iconSize,
                    imageVector = Icons.Default.Lock,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (state.text.isEmpty()) {
                        CText(
                            text = placeholder,
                            style = MaterialTheme.typography.bodySmall,
                            textColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    innerTextField()
                }

                SmallIconComponent(
                    modifier = Modifier
                        .size(iconSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { visible = !visible }),
                    size = iconSize,
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    )
}


@Preview(showBackground = false, name = "With Leading Icon")
@Composable
private fun CustomTextFieldLeadingIconPreview() {
    MaterialTheme {
        val state = rememberTextFieldState("")
        CTextField(
            state = state,
            placeholder = "Search...",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Preview(showBackground = false, name = "With Trailing Icon")
@Composable
private fun CustomTextFieldTrailingIconPreview() {
    MaterialTheme {
        val state = rememberTextFieldState("user@example.com")
        CTextField(
            state = state,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}

@Preview(showBackground = true, name = "Password TextField")
@Composable
private fun CPasswordTextFieldPreview() {
    MaterialTheme {
        val state = rememberTextFieldState("hfghfg")
        CPasswordTextField(
            state = state,
            placeholder = "Password",
        )
    }
}
