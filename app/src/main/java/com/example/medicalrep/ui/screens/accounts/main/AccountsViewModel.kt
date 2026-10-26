package com.example.medicalrep.ui.screens.accounts.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.repo.AccountRepo
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.AccountUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AccountsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    accountRepo: AccountRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(AccountsState())
    val state: StateFlow<AccountsState> = _state.asStateFlow()

    private val selectedType = _state.map { it.selectedType }.distinctUntilChanged()

    /** The accounts of the selected type, loaded page by page from Room. */
    val accounts: Flow<PagingData<AccountUi>> = selectedType
        .flatMapLatest { type -> accountRepo.accounts(type = type) }
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            selectedType.flatMapLatest { type -> accountRepo.accountCount(type = type) }
                .collect { count -> _state.update { it.copy(resultCount = count) } }
        }
    }

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
