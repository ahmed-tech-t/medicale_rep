package com.example.medicalrep.ui.components.widgets.filter

import androidx.annotation.StringRes
import com.example.medicalrep.R

/** The kind of account to list; [All] does not narrow the list. */
sealed interface AccountTypeFilter {
    @get:StringRes
    val valueRes: Int

    data object All : AccountTypeFilter {
        override val valueRes = R.string.filter_all
    }

    data object Clinics : AccountTypeFilter {
        override val valueRes = R.string.accounts_filter_clinics
    }

    data object Hospitals : AccountTypeFilter {
        override val valueRes = R.string.accounts_filter_hospitals
    }

    data object Pharmacies : AccountTypeFilter {
        override val valueRes = R.string.accounts_filter_pharmacies
    }

    data object MedicalCenters : AccountTypeFilter {
        override val valueRes = R.string.accounts_filter_medical_centers
    }

    companion object {
        val entries: List<AccountTypeFilter> = listOf(All, Clinics, Hospitals, Pharmacies, MedicalCenters)
    }
}