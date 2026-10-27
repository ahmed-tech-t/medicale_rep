package com.example.medicalrep.ui.screens.products.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.OrderItem
import com.example.medicalrep.domain.repo.AccountRepo
import com.example.medicalrep.domain.repo.OrderRepo
import com.example.medicalrep.domain.repo.ProductRepo
import com.example.medicalrep.ui.mapper.toProductUi
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.navigation.model.MainGraph
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
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    productRepo: ProductRepo,
    private val orderRepo: OrderRepo,
    accountRepo: AccountRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    /** The products matching the search text, loaded page by page from Room. */
    val products: Flow<PagingData<ProductUi>> = _state.map { it.query }
        .distinctUntilChanged()
        .flatMapLatest { query -> productRepo.products(query) }
        .map { page -> page.map { it.toProductUi() } }
        .cachedIn(viewModelScope)

    /** The accounts an order can be made for (the picker's list), matching its search text. */
    val accounts: Flow<PagingData<AccountUi>> = _state.map { it.accountQuery }
        .distinctUntilChanged()
        .flatMapLatest { query -> accountRepo.accounts(query = query) }
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    fun onEvent(event: ProductsEvents) {
        when (event) {
            ProductsEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is ProductsEvents.SearchChanged -> _state.update { it.copy(query = event.query) }
            is ProductsEvents.ChangeQuantity -> changeQuantity(event.product, event.delta)
            ProductsEvents.Order -> _state.update {
                if (it.hasSelection) it.copy(overlay = ProductsOverlay.PickAccount, accountQuery = "") else it
            }
            ProductsEvents.DismissOverlay -> _state.update { it.copy(overlay = ProductsOverlay.None) }
            is ProductsEvents.AccountQueryChanged -> _state.update { it.copy(accountQuery = event.query) }
            is ProductsEvents.SelectAccount -> _state.update {
                if (it.hasSelection) it.copy(overlay = ProductsOverlay.ConfirmOrder(event.account)) else it.copy(overlay = ProductsOverlay.None)
            }
            ProductsEvents.ConfirmOrder -> saveOrder()
        }
    }

    /** Quantity never goes below 0; reaching 0 puts the product back to its "Add" state. */
    private fun changeQuantity(product: ProductUi, delta: Int) {
        _state.update { state ->
            val quantity = ((state.selected[product.id]?.quantity ?: 0) + delta).coerceAtLeast(0)
            state.copy(
                selected = if (quantity == 0) state.selected - product.id
                else state.selected + (product.id to product.copy(quantity = quantity)),
            )
        }
    }

    /** One order for the confirmed account: a line per selected product with its name, price and discount copied; uploaded later. Then the Orders screen opens. */
    private fun saveOrder() {
        val state = _state.value
        val account = (state.overlay as? ProductsOverlay.ConfirmOrder)?.account ?: return
        val selected = state.selected.values
        if (selected.isEmpty()) return
        viewModelScope.launch {
            orderRepo.save(
                Order(
                    id = UUID.randomUUID().toString(),
                    accountId = account.id,
                    createdAtMillis = System.currentTimeMillis(),
                    items = selected.map { OrderItem(it.id, it.name, it.price, it.discountPercent, it.quantity) },
                ),
            )
            _state.update { it.copy(selected = emptyMap(), overlay = ProductsOverlay.None) }
            sendEvent(UiEvent.Navigate(MainGraph.Orders))
        }
    }
}
