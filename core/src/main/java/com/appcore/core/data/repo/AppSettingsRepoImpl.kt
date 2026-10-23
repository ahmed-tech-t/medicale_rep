package com.appcore.core.data.repo

import androidx.datastore.core.DataStore
import com.appcore.core.data.local.datastore.model.AppSettingsDS
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AppSettingsRepoImpl @Inject constructor(
    private val dataStore: DataStore<AppSettingsDS>
) : AppSettingsRepo {

    override fun getSettings(): Flow<AppSettings> = dataStore.data.map {
        AppSettings(language = it.language, country = it.country, showOnBoarding = it.showOnBoarding)
    }

    override suspend fun updateLanguage(language: String) {
        dataStore.updateData { it.copy(language = language) }
    }

    override suspend fun updateCountry(country: String) {
        dataStore.updateData { it.copy(country = country) }
    }

    override suspend fun getShowOnBoarding(): Boolean = dataStore.data.first().showOnBoarding

    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) {
        dataStore.updateData { it.copy(showOnBoarding = showOnBoarding) }
    }
}
