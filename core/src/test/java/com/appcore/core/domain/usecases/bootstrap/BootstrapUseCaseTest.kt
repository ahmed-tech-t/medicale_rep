package com.appcore.core.domain.usecases.bootstrap

import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.models.UserAuthStatus
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BootstrapUseCaseTest {

    private class FakeSessionRepo(
        var access: String = "",
        var refresh: String = "",
    ) : AuthSessionLocalRepo {
        override suspend fun getAccessToken() = access
        override suspend fun getRefreshToken() = refresh
        override suspend fun saveSession(accessToken: String, refreshToken: String) {
            access = accessToken
            refresh = refreshToken
        }
        override suspend fun clearSession() { access = ""; refresh = "" }
    }

    private class FakeSettingsRepo(showOnBoarding: Boolean) : AppSettingsRepo {
        private val settings = MutableStateFlow(AppSettings("ar", "egypt", showOnBoarding))
        override fun getSettings(): Flow<AppSettings> = settings
        override suspend fun updateLanguage(language: String) { settings.value = settings.value.copy(language = language) }
        override suspend fun updateCountry(country: String) { settings.value = settings.value.copy(country = country) }
        override suspend fun getShowOnBoarding() = settings.value.showOnBoarding
        override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) {
            settings.value = settings.value.copy(showOnBoarding = showOnBoarding)
        }
    }

    private val config = CoreConfig(
        baseUrl = "https://example.com/api/",
        isDebug = false,
        cryptoKeyAlias = "test",
    )

    private fun useCase(sessionRepo: FakeSessionRepo, showOnBoarding: Boolean = false): BootstrapUseCase {
        val settingsRepo = FakeSettingsRepo(showOnBoarding)
        return BootstrapUseCase(
            authSessionManager = AuthSessionManager(sessionRepo, emptySet()),
            appSettingsManager = AppSettingsManager(settingsRepo, config),
            appSettingsRepo = settingsRepo,
        )
    }

    @Test
    fun `stored access token starts authenticated`() = runTest {
        val result = useCase(FakeSessionRepo(access = "a", refresh = "r"))()

        assertEquals(UserAuthStatus.Authenticated, result.authStatus)
        assertEquals("ar", result.language)
    }

    @Test
    fun `no stored token starts not authenticated`() = runTest {
        val result = useCase(FakeSessionRepo())()

        assertEquals(UserAuthStatus.NotAuthenticated, result.authStatus)
    }

    @Test
    fun `show onboarding is passed through`() = runTest {
        val result = useCase(FakeSessionRepo(), showOnBoarding = true)()

        assertTrue(result.showOnBoarding)
    }
}
