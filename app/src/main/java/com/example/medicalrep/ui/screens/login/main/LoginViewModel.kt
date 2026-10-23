package com.example.medicalrep.ui.screens.login.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.local.settings.LocaleHelper
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val appSettingsManager: AppSettingsManager,
    private val localeHelper: LocaleHelper
) : BaseViewModel(appSettingsRepo) {
    private val _state = MutableStateFlow(LoginState(language = appSettingsManager.getLanguageSync()))
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvents) {
        when (event) {
            LoginEvents.SignInClicked -> {
                _state.update { it.copy(showErrors = true) }
                sendEvent(UiEvent.HideKeyboard)
            }

            LoginEvents.LanguageClicked -> switchLanguage()
        }
    }

    private fun switchLanguage() {
        val next = if (_state.value.language == "ar") "en" else "ar"
        _state.update { it.copy(language = next) }
        viewModelScope.launch {
            appSettingsManager.updateLanguage(next)
            localeHelper.setLocale(next)
        }
    }
}
