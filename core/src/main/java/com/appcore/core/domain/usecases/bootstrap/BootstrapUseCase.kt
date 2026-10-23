package com.appcore.core.domain.usecases.bootstrap

import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.domain.models.AppBootstrap
import com.appcore.core.domain.repo.AppSettingsRepo
import javax.inject.Inject

class BootstrapUseCase @Inject constructor(
    private val authSessionManager: AuthSessionManager,
    private val appSettingsManager: AppSettingsManager,
    private val appSettingsRepo: AppSettingsRepo,
) {
    suspend operator fun invoke(): AppBootstrap {
        authSessionManager.initialize()
        appSettingsManager.initialize()

        val showOnBoarding = runCatching {
            appSettingsRepo.getShowOnBoarding()
        }.getOrDefault(true)

        return AppBootstrap(
            authStatus = authSessionManager.getUserAuthStatus(),
            showOnBoarding = showOnBoarding,
            language = appSettingsManager.getLanguageSync(),
        )
    }
}
