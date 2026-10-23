package com.appcore.core.domain.repo

import com.appcore.core.domain.models.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepo {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateLanguage(language: String)
    suspend fun updateCountry(country: String)
    suspend fun getShowOnBoarding(): Boolean
    suspend fun updateShowOnBoarding(showOnBoarding: Boolean)
}
