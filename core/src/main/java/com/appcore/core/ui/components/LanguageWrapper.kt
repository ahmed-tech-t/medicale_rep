package com.appcore.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** Direction follows the locale the strings are loaded from, so text and RTL/LTR always flip together. */
@Composable
fun LanguageWrapper(content: @Composable () -> Unit) {
    val language = LocalConfiguration.current.locales[0].language
    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        content()
    }
}
