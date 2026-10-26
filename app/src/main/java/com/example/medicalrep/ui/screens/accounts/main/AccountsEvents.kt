package com.example.medicalrep.ui.screens.accounts.main

import com.example.medicalrep.domain.models.AccountType

sealed interface AccountsEvents {
    data object Back : AccountsEvents
    data object OpenFilter : AccountsEvents
    data object CloseFilter : AccountsEvents

    /** [type] null means All. */
    data class SelectType(val type: AccountType?) : AccountsEvents
}
