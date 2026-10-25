package com.example.medicalrep.ui.screens.terms.models

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class TermsSectionUi(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
)
