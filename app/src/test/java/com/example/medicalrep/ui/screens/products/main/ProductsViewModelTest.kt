package com.example.medicalrep.ui.screens.products.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.repo.FakeOrderRepo
import com.example.medicalrep.domain.repo.FakeProductRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
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
 * 1. Given the screen opens, Then the repo is asked for all products (blank search) and nothing is selected.
 * 2. When searching, Then the repo is asked for that text; clearing asks for all again.
 * 3. When "+" on a product, Then it has quantity 1; "+" again raises it; "-" lowers it.
 * 4. When "-" at 1, Then it returns to 0 and leaves the selection; "-" at 0 never goes negative.
 * 5. Selected quantities stay while the search text changes.
 * 6. Given nothing selected, When Order, Then nothing happens; Given a selection, Then one snackbar.
 * 7. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = FakeProductRepo(products = listOf(Product(id = "p1", name = "بانادول"), Product(id = "p2", name = "كونكور")))
    private lateinit var viewModel: ProductsViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProductsViewModel(FakeSettingsRepo, repo, FakeOrderRepo())
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Starts collecting and lets the collector begin (runCurrent also runs background work, unlike advanceUntilIdle). */
    private fun TestScope.collectProducts() {
        backgroundScope.launch(dispatcher) { viewModel.products.collect { } }
        runCurrent()
    }

    private fun TestScope.collectEvents(): List<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        runCurrent()
        return events
    }

    @Test
    fun `starts asking for all products with nothing selected`() = runTest(dispatcher) {
        collectProducts()
        runCurrent()
        assertEquals(listOf(""), repo.requestedQueries)
        assertEquals("", state.query)
        assertFalse(state.hasSelection)
    }

    @Test
    fun `searching asks the repo for the text and clearing asks for all`() = runTest(dispatcher) {
        collectProducts()
        runCurrent()
        viewModel.onEvent(ProductsEvents.SearchChanged("بانا"))
        runCurrent()
        viewModel.onEvent(ProductsEvents.SearchChanged(""))
        runCurrent()
        assertEquals(listOf("", "بانا", ""), repo.requestedQueries)
    }

    @Test
    fun `plus and minus change the quantity of one product`() {
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", 1))
        assertEquals(1, state.quantities["p1"])
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", 1))
        assertEquals(2, state.quantities["p1"])
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", -1))
        assertEquals(1, state.quantities["p1"])
        assertEquals(null, state.quantities["p2"])
    }

    @Test
    fun `minus at one removes the product from the selection and never goes negative`() {
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", 1))
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", -1))
        assertEquals(null, state.quantities["p1"])
        assertFalse(state.hasSelection)
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", -1))
        assertEquals(null, state.quantities["p1"])
    }

    @Test
    fun `quantities stay while the search changes`() = runTest(dispatcher) {
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", 2))
        viewModel.onEvent(ProductsEvents.SearchChanged("كونكور"))
        runCurrent()
        assertEquals(2, state.quantities["p1"])
        assertTrue(state.hasSelection)
    }

    @Test
    fun `order does nothing without a selection and shows one snackbar with one`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(ProductsEvents.Order)
        runCurrent()
        assertEquals(0, events.size)
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", 1))
        viewModel.onEvent(ProductsEvents.Order)
        runCurrent()
        assertEquals(1, events.size)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(ProductsEvents.Back)
        runCurrent()
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
