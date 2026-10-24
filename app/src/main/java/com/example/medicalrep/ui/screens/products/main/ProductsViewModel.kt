package com.example.medicalrep.ui.screens.products.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.products.utils.ProductsFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load products from Room.
    private val _state = MutableStateFlow(ProductsFakes.state())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

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
            state.copy(
                products = state.products.map {
                    if (it.id == id) it.copy(quantity = (it.quantity + delta).coerceAtLeast(0)) else it
                },
            )
        }
    }
}
