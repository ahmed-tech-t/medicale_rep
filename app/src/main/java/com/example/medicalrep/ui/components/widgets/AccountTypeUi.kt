package com.example.medicalrep.ui.components.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.AccountType

/** Singular name, as shown on a card. */
@get:StringRes
val AccountType.labelRes: Int
    get() = when (this) {
        AccountType.Clinic -> R.string.statics_account_clinic
        AccountType.Hospital -> R.string.statics_account_hospital
        AccountType.Pharmacy -> R.string.statics_account_pharmacy
        AccountType.MedicalCenter -> R.string.account_type_medical_center
    }

/** Plural name, as shown in the filter popup. */
@get:StringRes
val AccountType.filterLabelRes: Int
    get() = when (this) {
        AccountType.Clinic -> R.string.accounts_filter_clinics
        AccountType.Hospital -> R.string.accounts_filter_hospitals
        AccountType.Pharmacy -> R.string.accounts_filter_pharmacies
        AccountType.MedicalCenter -> R.string.accounts_filter_medical_centers
    }

@get:DrawableRes
val AccountType.iconRes: Int
    get() = when (this) {
        AccountType.Clinic -> R.drawable.ic_statics_clinic
        AccountType.Hospital -> R.drawable.ic_statics_hospital
        AccountType.Pharmacy -> R.drawable.ic_statics_pharmacy
        AccountType.MedicalCenter -> R.drawable.ic_statics_health_unit
    }

/** All, then every [AccountType] with its icon; null stands for All. */
val accountTypeFilterOptions: List<FilterOption<AccountType?>> =
    listOf(FilterOption<AccountType?>(null, R.string.filter_all, R.drawable.ic_grid_all)) +
        AccountType.entries.map { FilterOption<AccountType?>(it, it.filterLabelRes, it.iconRes) }
