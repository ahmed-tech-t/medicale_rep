package com.appcore.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.appcore.core.domain.utils.UiText

@Composable
fun UiText.asString(): String {
    val context = LocalContext.current
    return asString(context)
}
