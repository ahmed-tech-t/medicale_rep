package com.example.medicalrep.ui.screens.terms.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.screens.terms.models.TermsSectionUi
import com.example.medicalrep.ui.screens.terms.utils.TermsSections

@Immutable
data class TermsState(
    val sections: List<TermsSectionUi> = TermsSections.all,
)
