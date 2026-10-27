package com.example.medicalrep.ui.screens.orders.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.HandleUiEvents
import com.example.medicalrep.ui.screens.orders.components.OrdersUi

@Composable
fun OrdersScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    navigationUp: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    viewModel.uiEvent.HandleUiEvents(
        snackbarHostState = snackbarHostState,
        onNavigate = onNavigate,
        navigationUp = navigationUp,
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val orders = viewModel.orders.collectAsLazyPagingItems()

    OrdersUi(
        state = state,
        orders = orders,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
