package com.example.medicalrep.ui.screens.root

import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import com.appcore.core.domain.usecases.bootstrap.BootstrapUseCase
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.example.medicalrep.ui.navigation.model.AuthGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * TDD cases (Given / When / Then):
 * 1. Given the app is running, When the session expires with a server message,
 *    Then the user is sent to the auth graph and sees that message.
 * 2. Given the app is running, When the session expires without a message,
 *    Then the user is sent to the auth graph and sees "Session expired".
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RootViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var sessionManager: AuthSessionManager
    private lateinit var viewModel: RootViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        val settingsRepo = FakeSettingsRepo
        sessionManager = AuthSessionManager(FakeSessionRepo, emptySet())
        val settingsManager = AppSettingsManager(
            settingsRepo,
            CoreConfig(baseUrl = "", isDebug = false, cryptoKeyAlias = "", defaultLanguage = "ar"),
        )
        viewModel = RootViewModel(
            bootstrapUseCase = BootstrapUseCase(sessionManager, settingsManager, settingsRepo),
            authSessionManager = sessionManager,
            appSettingsManager = settingsManager,
        )
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `session expiry with message goes to auth graph and shows the message`() = runTest(dispatcher) {
        val events = expireSession("Account blocked")
        assertEquals(
            listOf(UiEvent.NavigateGraph(AuthGraph.Root), UiEvent.ShowSnackBar(UiText.DynamicText("Account blocked"))),
            events,
        )
    }

    @Test
    fun `session expiry without message shows the default text`() = runTest(dispatcher) {
        val events = expireSession(null)
        assertEquals(
            listOf(UiEvent.NavigateGraph(AuthGraph.Root), UiEvent.ShowSnackBar(UiText.DynamicText("Session expired"))),
            events,
        )
    }

    private suspend fun kotlinx.coroutines.test.TestScope.expireSession(message: String?): List<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.state.toList(mutableListOf()) } // starts bootstrap + session observer
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        sessionManager.onSessionExpired(message)
        return events
    }
}

private object FakeSessionRepo : AuthSessionLocalRepo {
    override suspend fun getAccessToken() = "token"
    override suspend fun getRefreshToken() = "refresh"
    override suspend fun saveSession(accessToken: String, refreshToken: String) = Unit
    override suspend fun clearSession() = Unit
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = flowOf(AppSettings(language = "ar", country = "EG", showOnBoarding = false))
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
