package com.example.medicalrep.ui.screens.root

import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.domain.usecases.bootstrap.BootstrapUseCase
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.root.BaseRootViewModel
import com.example.medicalrep.ui.navigation.model.AuthGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
@HiltViewModel
class RootViewModel @Inject constructor(
    bootstrapUseCase: BootstrapUseCase,
    authSessionManager: AuthSessionManager,
    appSettingsManager: AppSettingsManager,
) : BaseRootViewModel(bootstrapUseCase, authSessionManager, appSettingsManager) {

    // Refresh token rejected: back to the sign-in screen with a cleared back stack.
    override suspend fun onSessionExpired(message: String?) {
        sendEvent(UiEvent.NavigateGraph(AuthGraph.Root))
        sendEvent(UiEvent.ShowSnackBar(UiText.DynamicText(message ?: "Session expired")))
    }
}
