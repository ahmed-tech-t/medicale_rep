package com.example.medicalrep.ui.screens.orders.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.repo.FakeOrderRepo
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. A new screen has no card open.
 * 2. When a card is tapped, Then it opens; tapped again, Then it closes; several cards can be open at once.
 * 3. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = OrdersViewModel(FakeSettingsRepo, FakeOrderRepo())
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `no card is open at first`() {
        assertTrue(viewModel.state.value.expandedIds.isEmpty())
    }

    @Test
    fun `tapping a card opens it and tapping again closes it`() {
        viewModel.onEvent(OrdersEvents.ToggleOrder("o1"))
        assertEquals(setOf("o1"), viewModel.state.value.expandedIds)
        viewModel.onEvent(OrdersEvents.ToggleOrder("o1"))
        assertTrue(viewModel.state.value.expandedIds.isEmpty())
    }

    @Test
    fun `several cards can be open at once`() {
        viewModel.onEvent(OrdersEvents.ToggleOrder("o1"))
        viewModel.onEvent(OrdersEvents.ToggleOrder("o2"))
        assertEquals(setOf("o1", "o2"), viewModel.state.value.expandedIds)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(OrdersEvents.Back)
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
