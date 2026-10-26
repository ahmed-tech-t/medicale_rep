package com.example.medicalrep.ui.screens.accounts.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.AccountType
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then every account shows (All), 10 results, no popup.
 * 2. When the filter icon is tapped, Then the popup opens; when dismissed, Then it closes.
 * 3. When a type is selected, Then only accounts of that type show, the count follows and the popup closes.
 * 4. When All is selected again, Then every account shows.
 * 5. Given a type with no accounts, Then nothing shows and the count is 0.
 * 6. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AccountsViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = AccountsViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.collectEvents(): List<UiEvent> =
        mutableListOf<UiEvent>().also { backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(it) } }

    @Test
    fun `starts showing every account`() {
        assertEquals(10, state.resultCount)
        assertEquals(null, state.selectedType)
        assertEquals(AccountsOverlay.None, state.overlay)
    }

    @Test
    fun `filter icon opens the popup and dismiss closes it`() {
        viewModel.onEvent(AccountsEvents.OpenFilter)
        assertEquals(AccountsOverlay.TypeFilter, state.overlay)
        viewModel.onEvent(AccountsEvents.CloseFilter)
        assertEquals(AccountsOverlay.None, state.overlay)
    }

    @Test
    fun `selecting a type filters the list and closes the popup`() {
        viewModel.onEvent(AccountsEvents.OpenFilter)
        viewModel.onEvent(AccountsEvents.SelectType(AccountType.Hospital))
        assertTrue(state.visibleAccounts.isNotEmpty())
        assertTrue(state.visibleAccounts.all { it.type == AccountType.Hospital })
        assertEquals(state.visibleAccounts.size, state.resultCount)
        assertEquals(AccountsOverlay.None, state.overlay)
    }

    @Test
    fun `selecting all restores every account`() {
        viewModel.onEvent(AccountsEvents.SelectType(AccountType.Clinic))
        viewModel.onEvent(AccountsEvents.SelectType(null))
        assertEquals(10, state.resultCount)
    }

    @Test
    fun `a type without accounts shows nothing`() {
        viewModel.onEvent(AccountsEvents.SelectType(AccountType.Hospital))
        // no account of this type once the list is replaced by an empty one
        val empty = AccountsState(accounts = emptyList(), selectedType = AccountType.Hospital)
        assertEquals(0, empty.resultCount)
        assertTrue(empty.visibleAccounts.isEmpty())
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(AccountsEvents.Back)
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
