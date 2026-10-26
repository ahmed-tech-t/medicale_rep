package com.example.medicalrep.ui.screens.datacenter.main

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncStatus
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
 * 1. Given the screen opens, Then all six categories are listed, idle, full update first.
 * 2. When Back, Then NavigationUp.
 * 3. When OnSync for a category, Then the list is unchanged (server call is a TODO) and nothing crashes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DataCenterViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: DataCenterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = DataCenterViewModel(FakeSettingsRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts with six idle categories and the full update first`() {
        val items = viewModel.state.value.items
        assertEquals(6, items.size)
        assertEquals(
            listOf(
                DataCenterSyncItemUi.ID_FULL,
                DataCenterSyncItemUi.ID_DOCTORS,
                DataCenterSyncItemUi.ID_PRODUCTS,
                DataCenterSyncItemUi.ID_SAMPLES,
                DataCenterSyncItemUi.ID_PLANNED,
                DataCenterSyncItemUi.ID_UNPLANNED,
            ),
            items.map { it.id },
        )
        assertEquals(true, items.all { it.status == DataCenterSyncStatus.Idle })
        assertEquals(items.size, items.map { it.id }.distinct().size)
    }

    @Test
    fun `back navigates up`() = runTest(dispatcher) {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(dispatcher) { viewModel.uiEvent.toList(events) }
        viewModel.onEvent(DataCenterEvents.OnBack)
        assertEquals(listOf<UiEvent>(UiEvent.NavigationUp), events)
    }

    @Test
    fun `sync leaves the list unchanged until the server call exists`() {
        val before = viewModel.state.value
        viewModel.onEvent(DataCenterEvents.OnSync(DataCenterSyncItemUi.ID_DOCTORS))
        assertEquals(before, viewModel.state.value)
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
