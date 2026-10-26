package com.example.medicalrep.ui.screens.products.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.repo.FakeAccountRepo
import com.example.medicalrep.domain.repo.FakeOrderRepo
import com.example.medicalrep.domain.repo.FakeProductRepo
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.models.ProductUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then the repo is asked for all products (blank search) and nothing is selected.
 * 2. When searching, Then the repo is asked for that text; clearing asks for all again.
 * 3. When "+" on a product, Then it is selected with quantity 1; "+" again raises it; "-" lowers it; at 1 "-" removes it;
 *    it never goes negative. The selection keeps each product's name, price and discount.
 * 4. Selected products stay while the search text changes.
 * 5. Order with nothing selected does nothing; with a selection it opens the account picker.
 * 6. The picker searches accounts through the repo; dismissing closes it and keeps the selection.
 * 7. Choosing an account asks to confirm (nothing saved yet; Cancel closes it and keeps the selection). Confirming saves one order for that account: a line per selected product (name, price, discount, quantity
 *    copied), not uploaded, with the time; the selection and picker clear and one snackbar shows.
 * 8. Choosing an account with nothing selected saves nothing. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = FakeProductRepo(products = listOf(Product(id = "p1", name = "بانادول"), Product(id = "p2", name = "كونكور")))
    private val orderRepo = FakeOrderRepo()
    private val accountRepo = FakeAccountRepo(listOf(Account("a1", name = "عيادة د. محمود"), Account("a2", name = "صيدلية الشفاء")))
    private lateinit var viewModel: ProductsViewModel
    private val state get() = viewModel.state.value

    private val panadol = ProductUi("p1", "بانادول", 0, ProductType.Product, 0, "", price = 100, discountPercent = 10)
    private val concor = ProductUi("p2", "كونكور", 0, ProductType.Product, 0, "", price = 50, discountPercent = 0)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProductsViewModel(FakeSettingsRepo, repo, orderRepo, accountRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Starts collecting and lets the collector begin (runCurrent also runs background work). */
    private fun TestScope.collectProducts() {
        backgroundScope.launch(dispatcher) { viewModel.products.collect { } }
        runCurrent()
    }

    private fun TestScope.collectAccounts() {
        backgroundScope.launch(dispatcher) { viewModel.accounts.collect { } }
        runCurrent()
    }

    private fun TestScope.collectEvents(): List<UiEvent> {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        runCurrent()
        return events
    }

    private fun quantityOf(id: String) = state.selected[id]?.quantity

    @Test
    fun `starts asking for all products with nothing selected`() = runTest(dispatcher) {
        collectProducts()
        assertEquals(listOf(""), repo.requestedQueries)
        assertEquals("", state.query)
        assertFalse(state.hasSelection)
    }

    @Test
    fun `searching asks the repo for the text and clearing asks for all`() = runTest(dispatcher) {
        collectProducts()
        viewModel.onEvent(ProductsEvents.SearchChanged("بانا"))
        runCurrent()
        viewModel.onEvent(ProductsEvents.SearchChanged(""))
        runCurrent()
        assertEquals(listOf("", "بانا", ""), repo.requestedQueries)
    }

    @Test
    fun `plus and minus change the quantity of one product`() {
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 1))
        assertEquals(1, quantityOf("p1"))
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 1))
        assertEquals(2, quantityOf("p1"))
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, -1))
        assertEquals(1, quantityOf("p1"))
        assertNull(quantityOf("p2"))
    }

    @Test
    fun `the selection keeps name, price and discount`() {
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 2))
        val selected = state.selected.getValue("p1")
        assertEquals("بانادول", selected.name)
        assertEquals(100, selected.price)
        assertEquals(10, selected.discountPercent)
    }

    @Test
    fun `minus at one removes the product from the selection and never goes negative`() {
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 1))
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, -1))
        assertNull(quantityOf("p1"))
        assertFalse(state.hasSelection)
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, -1))
        assertNull(quantityOf("p1"))
    }

    @Test
    fun `selected products stay while the search changes`() = runTest(dispatcher) {
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 2))
        viewModel.onEvent(ProductsEvents.SearchChanged("كونكور"))
        runCurrent()
        assertEquals(2, quantityOf("p1"))
        assertTrue(state.hasSelection)
    }

    @Test
    fun `order does nothing without a selection and opens the account picker with one`() {
        viewModel.onEvent(ProductsEvents.Order)
        assertEquals(ProductsOverlay.None, state.overlay)
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 1))
        viewModel.onEvent(ProductsEvents.Order)
        assertEquals(ProductsOverlay.PickAccount, state.overlay)
    }

    @Test
    fun `the picker searches accounts and dismissing keeps the selection`() = runTest(dispatcher) {
        collectAccounts()
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 1))
        viewModel.onEvent(ProductsEvents.Order)
        viewModel.onEvent(ProductsEvents.AccountQueryChanged("صيدلية"))
        runCurrent()
        assertEquals("صيدلية", accountRepo.requestedQueries.last())
        viewModel.onEvent(ProductsEvents.DismissOverlay)
        assertEquals(ProductsOverlay.None, state.overlay)
        assertEquals(1, quantityOf("p1"))
    }

    @Test
    fun `choosing an account saves one order with a line per selected product`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 2))
        viewModel.onEvent(ProductsEvents.ChangeQuantity(concor, 1))
        viewModel.onEvent(ProductsEvents.Order)
        val account = AccountUi(id = "a1", name = "عيادة د. محمود")
        viewModel.onEvent(ProductsEvents.SelectAccount(account))
        runCurrent()
        assertEquals(ProductsOverlay.ConfirmOrder(account), state.overlay)
        assertTrue(orderRepo.current.value.isEmpty())
        viewModel.onEvent(ProductsEvents.ConfirmOrder)
        runCurrent()

        val order = orderRepo.current.value.single()
        assertEquals("a1", order.accountId)
        assertEquals(SyncStatus.NotSynced, order.syncStatus)
        assertTrue(order.createdAtMillis > 0)
        assertEquals(listOf("p1", "p2"), order.items.map { it.productId })
        val line = order.items.first()
        assertEquals(listOf("بانادول", 100, 10, 2), listOf(line.productName, line.unitPrice, line.discountPercent, line.quantity))
        assertEquals(180 + 50, order.total)

        assertFalse(state.hasSelection)
        assertEquals(ProductsOverlay.None, state.overlay)
        assertEquals(1, events.size)
        assertTrue(events[0] is UiEvent.ShowSnackBar)
    }

    @Test
    fun `cancelling the confirmation saves nothing and keeps the selection`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(ProductsEvents.ChangeQuantity(panadol, 1))
        viewModel.onEvent(ProductsEvents.Order)
        viewModel.onEvent(ProductsEvents.SelectAccount(AccountUi(id = "a1", name = "x")))
        viewModel.onEvent(ProductsEvents.DismissOverlay)
        viewModel.onEvent(ProductsEvents.ConfirmOrder)
        runCurrent()
        assertEquals(ProductsOverlay.None, state.overlay)
        assertEquals(1, quantityOf("p1"))
        assertTrue(orderRepo.current.value.isEmpty())
        assertEquals(0, events.size)
    }

    @Test
    fun `choosing an account with nothing selected saves nothing`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(ProductsEvents.SelectAccount(AccountUi(id = "a1", name = "x")))
        viewModel.onEvent(ProductsEvents.ConfirmOrder)
        runCurrent()
        assertEquals(ProductsOverlay.None, state.overlay)
        assertTrue(orderRepo.current.value.isEmpty())
        assertEquals(0, events.size)
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
