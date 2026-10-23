package com.example.medicalrep.ui.screens.root

import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.domain.usecases.bootstrap.BootstrapUseCase
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.root.BaseRootViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
@HiltViewModel
class RootViewModel @Inject constructor(
    bootstrapUseCase: BootstrapUseCase,
    authSessionManager: AuthSessionManager,
    appSettingsManager: AppSettingsManager,
) : BaseRootViewModel(bootstrapUseCase, authSessionManager, appSettingsManager) {

    // Refresh token rejected: navigate to the sign-in screen.
    override suspend fun onSessionExpired(message: String?) {
        sendEvent(UiEvent.ShowSnackBar(UiText.DynamicText(message ?: "Session expired")))
    }
}