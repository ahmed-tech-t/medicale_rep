package com.appcore.core.ui.components.modifier_ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

fun Modifier.disabled(disabled: Boolean): Modifier {
    return if (disabled) this.alpha(0.5f) else this
}