package com.example.medicalrep.ui.screens.login.main

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.local.settings.LocaleHelper
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TDD cases (Given / When / Then):
 * 1. Given a new screen, When it opens, Then all fields are blank and no errors show.
 * 2. Given any field, When the user types, Then the ViewModel's TextFieldState holds that text.
 * 3. Given blank fields, When Sign in is tapped, Then every field shows its error.
 * 4. Given only whitespace in a field, When Sign in is tapped, Then that field shows its error.
 * 5. Given all fields filled, When Sign in is tapped, Then no field shows an error.
 * 5b. Given errors are showing, When the user types in the blank field, Then its error clears.
 * 6. Given errors are showing, When Sign in is tapped, Then the keyboard is hidden (UiEvent.HideKeyboard).
 * 7. Given the saved language is Arabic, When the screen opens, Then state.language is "ar" and the pill offers "EN".
 * 8. Given Arabic, When the language pill is tapped, Then the language becomes "en" and the settings manager
 *    publishes "en" (the root re-applies direction and fades in, without restarting the activity).
 * 9. Given English, When the language pill is tapped, Then it switches back to "ar" (double toggle is stable).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var settingsManager: AppSettingsManager
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        settingsManager = AppSettingsManager(
            FakeAppSettingsRepo,
            CoreConfig(baseUrl = "", isDebug = false, cryptoKeyAlias = "", defaultLanguage = "ar"),
        )
        viewModel = LoginViewModel(FakeAppSettingsRepo, settingsManager, LocaleHelper(settingsManager))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** The text fields live in the state (TextFieldState instances are stable across updates). */
    private val fields get() = viewModel.state.value

    private fun fill(company: String, user: String, password: String) {
        fields.companyCode.setTextAndPlaceCursorAtEnd(company)
        fields.username.setTextAndPlaceCursorAtEnd(user)
        fields.password.setTextAndPlaceCursorAtEnd(password)
    }

    /** Same rule the screen uses: errors show only after Sign in and only for blank fields. */
    private fun showsError(blank: Boolean) = viewModel.state.value.showErrors && blank

    @Test
    fun `initial fields are blank without errors`() {
        assertFalse(viewModel.state.value.showErrors)
        assertEquals("ar", viewModel.state.value.language)
        assertTrue(fields.companyCodeBlank && fields.usernameBlank && fields.passwordBlank)
        assertFalse(showsError(fields.companyCodeBlank))
    }

    @Test
    fun `fields keep what the user typed in the view model`() {
        fill("9999", "ahmed", "secret")
        assertEquals("9999", fields.companyCode.text.toString())
        assertEquals("ahmed", fields.username.text.toString())
        assertEquals("secret", fields.password.text.toString())
    }

    @Test
    fun `sign in with blank fields shows all errors`() {
        viewModel.onEvent(LoginEvents.SignInClicked)
        assertTrue(showsError(fields.companyCodeBlank) && showsError(fields.usernameBlank) && showsError(fields.passwordBlank))
    }

    @Test
    fun `whitespace only field counts as blank`() {
        fill("9999", "   ", "secret")
        viewModel.onEvent(LoginEvents.SignInClicked)
        assertFalse(showsError(fields.companyCodeBlank))
        assertTrue(showsError(fields.usernameBlank))
        assertFalse(showsError(fields.passwordBlank))
    }

    @Test
    fun `sign in with all fields filled shows no errors`() {
        fill("9999", "ahmed", "secret")
        viewModel.onEvent(LoginEvents.SignInClicked)
        assertFalse(showsError(fields.companyCodeBlank) || showsError(fields.usernameBlank) || showsError(fields.passwordBlank))
    }

    @Test
    fun `error clears as soon as the user types`() {
        viewModel.onEvent(LoginEvents.SignInClicked)
        assertTrue(showsError(fields.usernameBlank))
        fields.username.setTextAndPlaceCursorAtEnd("a")
        assertFalse(showsError(fields.usernameBlank))
    }

    @Test
    fun `sign in hides the keyboard`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(LoginEvents.SignInClicked)
        assertEquals(listOf<UiEvent>(UiEvent.HideKeyboard), events)
    }

    @Test
    fun `initial language comes from settings and pill offers the other one`() {
        assertEquals("ar", viewModel.state.value.language)
        assertEquals("EN", viewModel.state.value.otherLanguageCode)
    }

    @Test
    fun `language pill switches to english and publishes it`() {
        viewModel.onEvent(LoginEvents.LanguageClicked)
        assertEquals("en", viewModel.state.value.language)
        assertEquals("AR", viewModel.state.value.otherLanguageCode)
        assertEquals("en", settingsManager.getLanguageSync())
    }

    @Test
    fun `language pill twice returns to arabic`() {
        viewModel.onEvent(LoginEvents.LanguageClicked)
        viewModel.onEvent(LoginEvents.LanguageClicked)
        assertEquals("ar", viewModel.state.value.language)
        assertEquals("ar", settingsManager.getLanguageSync())
    }
}

private object FakeAppSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
