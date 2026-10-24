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
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
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
            items(items = state.visibleProducts, key = { it.id }) { product ->
                ProductItemCard(
                    product = product,
                    onChangeQuantity = { onEvent(ProductsEvents.ChangeQuantity(product.id, it)) },
                )
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
}

@Preview(showBackground = true, locale = "ar", heightDp = 760)
@Composable
private fun ProductsUiArabicPreview() {
    MedicalRepTheme { ProductsUi(state = ProductsFakes.state(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun ProductsUiNoSelectionPreview() {
    MedicalRepTheme {
        ProductsUi(
            state = ProductsFakes.state().let { s -> s.copy(products = s.products.map { it.copy(quantity = 0) }) },
            onEvent = {},
        )
    }
}
