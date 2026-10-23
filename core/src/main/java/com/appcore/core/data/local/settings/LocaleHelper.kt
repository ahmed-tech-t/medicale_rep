package com.appcore.core.data.local.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class LocaleHelper @Inject constructor(
    private val appSettingsManager: AppSettingsManager,
) {

    fun setLocale(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    /**
     * Call once from `Application.onCreate`. Loads the saved settings into [AppSettingsManager]
     * (so no later startup read is needed) and applies the saved language.
     */
    fun applySavedLocale() {
        // Blocking on purpose: the locale must be set before the first Activity inflates.
        runBlocking { runCatching { appSettingsManager.initialize() } }
        setLocale(appSettingsManager.getLanguageSync())
    }
}
