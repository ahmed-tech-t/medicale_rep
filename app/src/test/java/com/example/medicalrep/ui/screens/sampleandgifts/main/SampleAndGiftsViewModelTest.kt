package com.example.medicalrep.ui.screens.sampleandgifts.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.example.medicalrep.ui.screens.sampleandgifts.models.SampleAndGiftsItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the Samples tab is selected and only samples are visible.
 * 2. When the Gifts tab is selected, Then only gifts are visible; selecting Samples again restores samples.
 * 3. Given an empty list, When any tab is selected, Then nothing is visible and nothing crashes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SampleAndGiftsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SampleAndGiftsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = SampleAndGiftsViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts on samples tab showing only samples`() {
        val state = viewModel.state.value
        assertEquals(SampleAndGiftsTab.Samples, state.selectedTab)
        assertTrue(state.visibleItems.isNotEmpty())
        assertTrue(state.visibleItems.all { it.type == SampleAndGiftsItemType.Sample })
    }

    @Test
    fun `selecting a tab switches the visible items`() {
        viewModel.onEvent(SampleAndGiftsEvents.SelectTab(SampleAndGiftsTab.Gifts))
        assertTrue(viewModel.state.value.visibleItems.all { it.type == SampleAndGiftsItemType.Gift })

        viewModel.onEvent(SampleAndGiftsEvents.SelectTab(SampleAndGiftsTab.Samples))
        assertTrue(viewModel.state.value.visibleItems.all { it.type == SampleAndGiftsItemType.Sample })
    }

    @Test
    fun `empty list shows nothing on either tab`() {
        val empty = SampleAndGiftsState()
        assertTrue(empty.visibleItems.isEmpty())
        assertTrue(empty.copy(selectedTab = SampleAndGiftsTab.Gifts).visibleItems.isEmpty())
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
