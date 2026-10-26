package com.example.medicalrep.ui.screens.planvisit.account_location.main

import androidx.lifecycle.SavedStateHandle
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.screens.planvisit.account_location.utils.directionsUrl
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
import org.junit.Before
import org.junit.Test

/**
 * 1. When Get directions, Then Google Maps directions to the account open (UiEvent.OpenUrl).
 * 2. When Back, Then NavigationUp.
 * Route args are not readable from SavedStateHandle in JVM tests, so the account stays at (0, 0).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AccountLocationViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AccountLocationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = AccountLocationViewModel(
            FakeSettingsRepo,
            SavedStateHandle(
                mapOf(
                    "accountLatitude" to 30.5657,
                    "accountLongitude" to 31.0043,
                    "accountName" to "Al-Iman Pharmacy",
                    "address" to "Shebin El-Kom - Stadium Square",
                    "classCategory" to "C",
                    "accountType" to "Medical Center",
                    "specialty" to "Orthopedics",
                )
            ),
        )
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `get directions opens Google Maps to the account`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        val account = viewModel.state.value.accountLocation
        viewModel.onEvent(AccountLocationEvents.GetDirections)
        assertEquals(
            listOf<UiEvent>(UiEvent.OpenUrl(directionsUrl(account.latitude, account.longitude))),
            events,
        )
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(AccountLocationEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
