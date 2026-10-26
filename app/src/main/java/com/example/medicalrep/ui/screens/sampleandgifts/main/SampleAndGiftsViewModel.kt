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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SampleAndGiftsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    productRepo: ProductRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(SampleAndGiftsState())
    val state: StateFlow<SampleAndGiftsState> = _state.asStateFlow()

    /** The rep's custody of the selected tab, loaded page by page; `remaining` already counts visits not uploaded yet. */
    val items: Flow<PagingData<CustodyUi>> = _state.map { it.selectedTab }
        .distinctUntilChanged()
        .flatMapLatest { tab -> productRepo.custody(kind = tab.kind) }
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    fun onEvent(event: SampleAndGiftsEvents) {
        when (event) {
            is SampleAndGiftsEvents.SelectTab -> _state.update { it.copy(selectedTab = event.tab) }
        }
    }
}
