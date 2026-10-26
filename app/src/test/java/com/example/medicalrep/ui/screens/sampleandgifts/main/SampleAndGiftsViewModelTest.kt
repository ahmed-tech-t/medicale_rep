package com.example.medicalrep.ui.screens.sampleandgifts.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.repo.FakeProductRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the Samples tab is selected and the repo is asked for samples only.
 * 2. When the Gifts tab is selected, Then the repo is asked for gifts; selecting Samples again asks for samples.
 * 3. Samples are the custody of kind Product, gifts the custody of kind Gift.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SampleAndGiftsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = FakeProductRepo()
    private lateinit var viewModel: SampleAndGiftsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = SampleAndGiftsViewModel(FakeSettingsRepo, repo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Starts collecting and lets the collector begin (runCurrent also runs background work, unlike advanceUntilIdle). */
    private fun TestScope.collectItems() {
        backgroundScope.launch(dispatcher) { viewModel.items.collect { } }
        runCurrent()
    }

    @Test
    fun `starts on the samples tab asking for samples`() = runTest(dispatcher) {
        collectItems()
        runCurrent()
        assertEquals(SampleAndGiftsTab.Samples, viewModel.state.value.selectedTab)
        assertEquals(listOf<ProductType?>(ProductType.Product), repo.requestedKinds)
    }

    @Test
    fun `selecting a tab asks for that kind`() = runTest(dispatcher) {
        collectItems()
        runCurrent()
        viewModel.onEvent(SampleAndGiftsEvents.SelectTab(SampleAndGiftsTab.Gifts))
        runCurrent()
        viewModel.onEvent(SampleAndGiftsEvents.SelectTab(SampleAndGiftsTab.Samples))
        runCurrent()
        assertEquals(listOf<ProductType?>(ProductType.Product, ProductType.Gift, ProductType.Product), repo.requestedKinds)
    }

    @Test
    fun `tabs map to the custody kinds`() {
        assertEquals(ProductType.Product, SampleAndGiftsTab.Samples.kind)
        assertEquals(ProductType.Gift, SampleAndGiftsTab.Gifts.kind)
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
