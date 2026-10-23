package com.appcore.core.ui.components.modifier_ext

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.notchesPadding(): Modifier {
    return this.windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.displayCutout))
}