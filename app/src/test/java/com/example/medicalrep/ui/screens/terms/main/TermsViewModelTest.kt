package com.example.medicalrep.ui.screens.terms.main

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
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the seven terms are listed with distinct titles.
 * 2. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TermsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: TermsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = TermsViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `lists seven terms with distinct titles`() {
        val sections = viewModel.state.value.sections
        assertEquals(7, sections.size)
        assertEquals(sections.size, sections.map { it.titleRes }.distinct().size)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(TermsEvents.Back)
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
