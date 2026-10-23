package com.example.medicalrep.ui.screens.home.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.local.settings.LocaleHelper
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.ui.preview.HomeSamples
import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val appSettingsManager: AppSettingsManager,
    private val localeHelper: LocaleHelper,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(HomeSamples.homeState(language = appSettingsManager.getLanguageSync()))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun onEvent(event: HomeEvents) {
        when (event) {
            HomeEvents.OnMenuClick -> _state.update { it.copy(isDrawerOpen = true) }
            HomeEvents.OnDrawerDismiss -> _state.update { it.copy(isDrawerOpen = false) }
            is HomeEvents.OnDrawerItem -> {
                _state.update { it.copy(isDrawerOpen = false) }
                if (event.item == NavDrawerItem.Language) switchLanguage() else showComingSoon()
            }
            HomeEvents.OnSyncClick,
            HomeEvents.OnActiveContinue,
            HomeEvents.OnActiveCancel,
            is HomeEvents.OnStartVisit,
            HomeEvents.OnTodayMoreClick,
            HomeEvents.OnAddVisitClick -> showComingSoon()
        }
    }

    private fun showComingSoon() {
        sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.placeholder_coming_soon)))
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
