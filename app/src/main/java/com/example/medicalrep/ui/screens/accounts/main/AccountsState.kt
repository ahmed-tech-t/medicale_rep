package com.example.medicalrep.ui.screens.accounts.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.AccountType

sealed interface AccountsOverlay {
    data object None : AccountsOverlay
    data object TypeFilter : AccountsOverlay
}

/** The list itself is paged and comes from the ViewModel's `accounts` flow; this holds what surrounds it. */
@Immutable
data class AccountsState(
    /** null means All. */
    val selectedType: AccountType? = null,
    val resultCount: Int = 0,
    val overlay: AccountsOverlay = AccountsOverlay.None,
) {
    val isFiltered: Boolean get() = selectedType != null
}
