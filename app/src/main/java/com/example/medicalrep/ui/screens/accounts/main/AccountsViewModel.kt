package com.example.medicalrep.ui.screens.accounts.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.ui.screens.accounts.utils.AccountsFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load accounts from AccountRepo.
    private val _state = MutableStateFlow(AccountsState(accounts = AccountsFakes.accounts()))
    val state: StateFlow<AccountsState> = _state.asStateFlow()

    fun onEvent(event: AccountsEvents) {
        when (event) {
            AccountsEvents.Back -> sendEvent(UiEvent.NavigationUp)
            AccountsEvents.OpenFilter -> _state.update { it.copy(overlay = AccountsOverlay.TypeFilter) }
            AccountsEvents.CloseFilter -> _state.update { it.copy(overlay = AccountsOverlay.None) }
            is AccountsEvents.SelectType -> _state.update {
                it.copy(selectedType = event.type, overlay = AccountsOverlay.None)
            }
        }
    }
}
