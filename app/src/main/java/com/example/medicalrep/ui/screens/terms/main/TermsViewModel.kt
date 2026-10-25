package com.example.medicalrep.ui.screens.terms.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TermsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(TermsState())
    val state: StateFlow<TermsState> = _state.asStateFlow()

    fun onEvent(event: TermsEvents) {
        when (event) {
            TermsEvents.Back -> sendEvent(UiEvent.NavigationUp)
        }
    }
}
