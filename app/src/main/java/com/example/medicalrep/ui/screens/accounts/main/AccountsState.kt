package com.example.medicalrep.ui.screens.accounts.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.ui.models.AccountUi

sealed interface AccountsOverlay {
    data object None : AccountsOverlay
    data object TypeFilter : AccountsOverlay
}

@Immutable
data class AccountsState(
    val accounts: List<AccountUi> = emptyList(),
    /** null means All. */
    val selectedType: AccountType? = null,
    val overlay: AccountsOverlay = AccountsOverlay.None,
) {
    val visibleAccounts: List<AccountUi>
        get() = if (selectedType == null) accounts else accounts.filter { it.type == selectedType }

    val resultCount: Int get() = visibleAccounts.size
    val isFiltered: Boolean get() = selectedType != null
}
