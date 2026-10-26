package com.example.medicalrep.ui.screens.accounts.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.repo.FakeAccountRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Given the screen opens, Then every account shows (All), the count is the number of accounts, no popup.
 * 2. When the filter icon is tapped, Then the popup opens; when dismissed, Then it closes.
 * 3. When a type is selected, Then the repo is asked for that type, the count follows and the popup closes.
 * 4. When All is selected again, Then the repo is asked for every type again.
 * 5. Given a type with no accounts, Then the count is 0.
 * 6. The filter icon is on only while a type is selected.
 * 7. Given the stored accounts change, Then the list and the count follow.
 * 8. When Back, Then NavigationUp.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repo = FakeAccountRepo(
        listOf(
            Account("a1", name = "عيادة د. محمود", entityType = "عيادة"),
            Account("a2", name = "عيادة د. هاني", entityType = "عيادة"),
            Account("a3", name = "مستشفى الدلتا", entityType = "مستشفى"),
            Account("a4", name = "صيدلية الشفاء", entityType = "صيدلية"),
        ),
    )
    private lateinit var viewModel: AccountsViewModel
    private val state get() = viewModel.state.value

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = AccountsViewModel(FakeSettingsRepo, repo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.collectEvents(): List<UiEvent> =
        mutableListOf<UiEvent>().also { backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(it) } }

    /** The screen collects the paged flow; the fake records which type each collection asked the repo for. */
    private fun TestScope.collectAccounts() {
        backgroundScope.launch(dispatcher) { viewModel.accounts.collect { } }
    }

    @Test
    fun `starts showing every account`() = runTest(dispatcher) {
        collectAccounts()
        advanceUntilIdle()
        assertEquals(listOf<AccountType?>(null), repo.requestedTypes)
        assertEquals(4, state.resultCount)
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
    fun `selecting a type asks the repo for that type, updates the count and closes the popup`() = runTest(dispatcher) {
        collectAccounts()
        advanceUntilIdle()
        viewModel.onEvent(AccountsEvents.OpenFilter)
        viewModel.onEvent(AccountsEvents.SelectType(AccountType.Clinic))
        advanceUntilIdle()
        assertEquals(listOf(null, AccountType.Clinic), repo.requestedTypes)
        assertEquals(2, state.resultCount)
        assertEquals(AccountsOverlay.None, state.overlay)
    }

    @Test
    fun `selecting all asks for every type again`() = runTest(dispatcher) {
        collectAccounts()
        advanceUntilIdle()
        viewModel.onEvent(AccountsEvents.SelectType(AccountType.Clinic))
        advanceUntilIdle()
        viewModel.onEvent(AccountsEvents.SelectType(null))
        advanceUntilIdle()
        assertEquals(listOf(null, AccountType.Clinic, null), repo.requestedTypes)
        assertEquals(4, state.resultCount)
    }

    @Test
    fun `a type without accounts has a count of zero`() = runTest(dispatcher) {
        collectAccounts()
        viewModel.onEvent(AccountsEvents.SelectType(AccountType.MedicalCenter))
        advanceUntilIdle()
        assertEquals(0, state.resultCount)
    }

    @Test
    fun `filter icon is on only while a type is selected`() {
        assertEquals(false, state.isFiltered)
        viewModel.onEvent(AccountsEvents.SelectType(AccountType.Clinic))
        assertEquals(true, state.isFiltered)
        viewModel.onEvent(AccountsEvents.SelectType(null))
        assertEquals(false, state.isFiltered)
    }

    @Test
    fun `the count follows the stored accounts`() = runTest(dispatcher) {
        advanceUntilIdle()
        repo.save(Account("a5", name = "عيادة د. جديد", entityType = "عيادة"))
        advanceUntilIdle()
        assertEquals(5, state.resultCount)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = collectEvents()
        viewModel.onEvent(AccountsEvents.Back)
        advanceUntilIdle()
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
