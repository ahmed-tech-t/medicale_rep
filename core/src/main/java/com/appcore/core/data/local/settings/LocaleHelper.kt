package com.appcore.core.data.local.settings

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
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
     * Call once from `Application.onCreate`.
     *
     * Android 13+: the saved language (DataStore) is the source of truth and is pushed to the system before the
     * first Activity, so there is no late locale change. AppCompat can't be used here: before an Activity exists
     * its get/set are silent no-ops on 13+, so [LocaleManager] is called directly.
     *
     * Android 12 and older: AppCompat applies the saved language in memory before the first Activity.
     */
    fun syncSavedLocale(context: Context) {
        runBlocking { runCatching { appSettingsManager.initialize() } }
        val savedLanguage = appSettingsManager.getLanguageSync()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            setLocale(savedLanguage)
            return
        }
        val localeManager = context.getSystemService(LocaleManager::class.java)
        val systemLanguage = localeManager.applicationLocales[0]?.language
        if (systemLanguage != savedLanguage) {
            localeManager.applicationLocales = LocaleList.forLanguageTags(savedLanguage)
        }
    }
}
