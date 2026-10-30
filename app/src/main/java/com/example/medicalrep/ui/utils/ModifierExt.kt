package com.example.medicalrep.ui.utils

import androidx.compose.ui.Modifier

/** Applies [block] to the modifier only when [condition] is true. */
inline fun Modifier.thenIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier =
    if (condition) block() else this
