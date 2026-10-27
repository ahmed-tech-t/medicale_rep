package com.example.medicalrep.ui.screens.sampleandgifts.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.repo.ProductRepo
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.CustodyUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SampleAndGiftsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    productRepo: ProductRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(SampleAndGiftsState())
    val state: StateFlow<SampleAndGiftsState> = _state.asStateFlow()

    /** One paged list per tab: a tab never shows the items of the tab before it, and a tab seen before shows its page at once. */
    private val itemsByTab = SampleAndGiftsTab.entries.associateWith { tab ->
        // Asked when the tab is first shown, not when the screen opens.
        flow { emitAll(productRepo.custody(kind = tab.kind)) }
            .map { page -> page.map { it.toUi() } }
            .cachedIn(viewModelScope)
    }

    /** The rep's custody of [tab], loaded page by page; `remaining` already counts visits not uploaded yet. */
    fun itemsOf(tab: SampleAndGiftsTab): Flow<PagingData<CustodyUi>> = itemsByTab.getValue(tab)

    fun onEvent(event: SampleAndGiftsEvents) {
        when (event) {
            is SampleAndGiftsEvents.SelectTab -> _state.update { it.copy(selectedTab = event.tab) }
        }
    }
}
