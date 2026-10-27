package com.example.medicalrep.ui.screens.orders.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.repo.OrderRepo
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.OrderUi
import com.example.medicalrep.ui.utils.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    orderRepo: OrderRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(OrdersState())
    val state: StateFlow<OrdersState> = _state.asStateFlow()

    /** The orders, newest first, loaded page by page from Room. */
    val orders: Flow<PagingData<OrderUi>> = orderRepo.orders()
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    fun onEvent(event: OrdersEvents) {
        when (event) {
            OrdersEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is OrdersEvents.ToggleOrder -> _state.update { it.copy(expandedIds = it.expandedIds.toggle(event.id)) }
        }
    }
}
