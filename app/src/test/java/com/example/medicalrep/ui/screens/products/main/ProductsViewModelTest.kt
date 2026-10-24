package com.example.medicalrep.ui.screens.products.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
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
 * 1. Given the screen opens, Then all products show.
 * 2. When searching, Then only products whose name contains the text (any case) show; clearing restores all;
 *    no match shows nothing.
 * 3. When "+" on a product with 0, Then it has quantity 1; "+" again raises it; "-" lowers it.
 * 4. When "-" at 1, Then it returns to 0; "-" at 0 never goes negative.
 * 5. Given nothing selected, When Order, Then nothing happens; Given a selection, Then one snackbar.
 * 6. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ProductsViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProductsViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun quantityOf(id: String) = state.products.first { it.id == id }.quantity

    @Test
    fun `starts showing every product`() {
        assertEquals(state.products, state.visibleProducts)
        assertTrue(state.products.isNotEmpty())
    }

    @Test
    fun `search filters ignoring case and clearing restores all`() {
        viewModel.onEvent(ProductsEvents.SearchChanged("بانادول"))
        assertEquals(listOf("p1"), state.visibleProducts.map { it.id })

        viewModel.onEvent(ProductsEvents.SearchChanged("zzz-no-match"))
        assertTrue(state.visibleProducts.isEmpty())

        viewModel.onEvent(ProductsEvents.SearchChanged(""))
        assertEquals(state.products, state.visibleProducts)
    }

    @Test
    fun `add then plus raises quantity`() {
        assertEquals(0, quantityOf("p2"))
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p2", 1))
        assertEquals(1, quantityOf("p2"))
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p2", 1))
        assertEquals(2, quantityOf("p2"))
    }

    @Test
    fun `minus lowers quantity and never goes below zero`() {
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p3", -1))
        assertEquals(0, quantityOf("p3"))
        viewModel.onEvent(ProductsEvents.ChangeQuantity("p3", -1))
        assertEquals(0, quantityOf("p3"))
    }

    @Test
    fun `order does nothing without a selection and shows a snackbar with one`() = runTest(dispatcher) {
        val events = collectEvents()
        state.products.forEach { viewModel.onEvent(ProductsEvents.ChangeQuantity(it.id, -it.quantity)) }
        assertFalse(state.hasSelection)
        viewModel.onEvent(ProductsEvents.Order)
        assertTrue(events.isEmpty())

        viewModel.onEvent(ProductsEvents.ChangeQuantity("p1", 1))
        viewModel.onEvent(ProductsEvents.Order)
        assertEquals(1, events.size)
        assertTrue(events.single() is UiEvent.ShowSnackBar)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(ProductsEvents.Back)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    private fun TestScope.collectEvents(): MutableList<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        return events
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
