package com.example.medicalrep.ui.screens.products.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import com.example.medicalrep.ui.components.widgets.ConfirmOrderDialog
import com.example.medicalrep.ui.models.AccountUi
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.screens.accounts.utils.AccountsFakes
import com.example.medicalrep.ui.screens.products.main.ProductsOverlay
import com.example.medicalrep.ui.preview.pagingItemsOf
import com.example.medicalrep.ui.components.chrome.AppHeader
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.widgets.SearchBarComponent
import com.example.medicalrep.ui.screens.products.main.ProductsEvents
import com.example.medicalrep.ui.screens.products.main.ProductsState
import com.example.medicalrep.ui.screens.products.utils.ProductsFakes
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 307:2518: all products with a live search filter and an Order button. */
@Composable
fun ProductsUi(
    state: ProductsState,
    products: LazyPagingItems<ProductUi>,
    accounts: LazyPagingItems<AccountUi>,
    onEvent: (ProductsEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
    ) {
        AppHeader(title = stringResource(R.string.products), onBack = { onEvent(ProductsEvents.Back) })
        SearchBarComponent(
            query = state.query,
            onQueryChange = { onEvent(ProductsEvents.SearchChanged(it)) },
            placeholder = stringResource(R.string.products_search_hint),
            modifier = Modifier.padding(spacing.spaceSmallPlus),
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            items(count = products.itemCount, key = products.itemKey { it.id }) { index ->
                products[index]?.let { product ->
                    ProductItemCard(
                        product = state.selected[product.id] ?: product,
                        onChangeQuantity = { onEvent(ProductsEvents.ChangeQuantity(product, it)) },
                    )
                }
            }
        }
        NexoraButton(
            text = stringResource(R.string.products_action_order),
            onClick = { onEvent(ProductsEvents.Order) },
            enabled = state.hasSelection,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(spacing.spaceSmallPlus),
        )
    }

    if (state.overlay is ProductsOverlay.ConfirmOrder) {
        ConfirmOrderDialog(
            onConfirm = { onEvent(ProductsEvents.ConfirmOrder) },
            onDismiss = { onEvent(ProductsEvents.DismissOverlay) },
        )
    }
    if (state.overlay == ProductsOverlay.PickAccount) {
        AccountPickerSheet(
            accounts = accounts,
            query = state.accountQuery,
            onQueryChange = { onEvent(ProductsEvents.AccountQueryChanged(it)) },
            onSelect = { onEvent(ProductsEvents.SelectAccount(it)) },
            onDismiss = { onEvent(ProductsEvents.DismissOverlay) },
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 760)
@Composable
private fun ProductsUiArabicPreview() {
    MedicalRepTheme { ProductsUi(state = ProductsFakes.state(), products = pagingItemsOf(ProductsFakes.products()), accounts = pagingItemsOf(AccountsFakes.accounts()), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun ProductsUiNoSelectionPreview() {
    MedicalRepTheme { ProductsUi(state = ProductsState(), products = pagingItemsOf(ProductsFakes.products()), accounts = pagingItemsOf(AccountsFakes.accounts()), onEvent = {}) }
}
