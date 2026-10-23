package com.appcore.core.data.local.settings

import com.appcore.core.config.CoreConfig
import com.appcore.core.domain.repo.AppSettingsRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/** In-memory language/country so OkHttp interceptors can read them synchronously. */
@Singleton
class AppSettingsManager @Inject constructor(
    private val appSettingsRepo: AppSettingsRepo,
    config: CoreConfig,
) {
    @Volatile
    private var language = config.defaultLanguage



    suspend fun initialize() {
        val settings = appSettingsRepo.getSettings().first()
        language = settings.language
    }

    suspend fun updateLanguage(value: String) {
        appSettingsRepo.updateLanguage(value)
        language = value
    }

    fun getLanguageSync() = language
}
