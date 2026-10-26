package com.example.medicalrep.ui.screens.products.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.domain.repo.ProductRepo
import com.example.medicalrep.ui.mapper.toProductUi
import com.example.medicalrep.ui.models.ProductUi
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
class ProductsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    productRepo: ProductRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    /** The products matching the search text, loaded page by page from Room. */
    val products: Flow<PagingData<ProductUi>> = _state.map { it.query }
        .distinctUntilChanged()
        .flatMapLatest { query -> productRepo.products(query) }
        .map { page -> page.map { it.toProductUi() } }
        .cachedIn(viewModelScope)

    fun onEvent(event: ProductsEvents) {
        when (event) {
            ProductsEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is ProductsEvents.SearchChanged -> _state.update { it.copy(query = event.query) }
            is ProductsEvents.ChangeQuantity -> changeQuantity(event.id, event.delta)
            ProductsEvents.Order -> if (_state.value.hasSelection) {
                sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.placeholder_coming_soon)))
            }
        }
    }

    /** Quantity never goes below 0; reaching 0 puts the product back to its "Add" state. */
    private fun changeQuantity(id: String, delta: Int) {
        _state.update { state ->
            val quantity = ((state.quantities[id] ?: 0) + delta).coerceAtLeast(0)
            state.copy(quantities = if (quantity == 0) state.quantities - id else state.quantities + (id to quantity))
        }
    }
}
