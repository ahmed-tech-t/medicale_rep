package com.example.medicalrep.ui.screens.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.models.OrderUi
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.screens.orders.main.OrdersEvents
import com.example.medicalrep.ui.screens.orders.main.OrdersState
import com.example.medicalrep.ui.screens.orders.utils.OrdersFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** The orders, newest first; each card opens to show its lines. */
@Composable
fun OrdersUi(
    state: OrdersState,
    orders: LazyPagingItems<OrderUi>,
    onEvent: (OrdersEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(title = stringResource(R.string.orders), onBack = { onEvent(OrdersEvents.Back) })
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            items(count = orders.itemCount, key = orders.itemKey { it.id }) { index ->
                orders[index]?.let { order ->
                    OrderCard(
                        order = order,
                        expanded = order.id in state.expandedIds,
                        onToggle = { onEvent(OrdersEvents.ToggleOrder(order.id)) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 760)
@Composable
private fun OrdersUiPreview() {
    MedicalRepTheme {
        OrdersUi(
            state = OrdersState(expandedIds = setOf("o1001")),
            orders = pagingItemsOf(OrdersFakes.orders()),
            onEvent = {},
        )
    }
}
