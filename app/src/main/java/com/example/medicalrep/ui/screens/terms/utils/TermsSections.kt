package com.example.medicalrep.ui.screens.terms.utils

import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.terms.models.TermsSectionUi

/** The numbered terms, in display order. */
object TermsSections {
    val all: List<TermsSectionUi> = listOf(
        TermsSectionUi(R.string.terms_account_use_title, R.string.terms_account_use_body),
        TermsSectionUi(R.string.terms_device_binding_title, R.string.terms_device_binding_body),
        TermsSectionUi(R.string.terms_location_title, R.string.terms_location_body),
        TermsSectionUi(R.string.terms_offline_title, R.string.terms_offline_body),
        TermsSectionUi(R.string.terms_custody_title, R.string.terms_custody_body),
        TermsSectionUi(R.string.terms_confidentiality_title, R.string.terms_confidentiality_body),
        TermsSectionUi(R.string.terms_changes_title, R.string.terms_changes_body),
    )
}
