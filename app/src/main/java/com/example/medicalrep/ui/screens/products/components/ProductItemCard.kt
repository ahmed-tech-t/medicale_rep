package com.example.medicalrep.ui.screens.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.QuantityStepper
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.screens.products.utils.ProductsFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

/** Figma 307:2518 product card: name, price and discount; an Add button until it has a quantity, then a stepper. */
@Composable
fun ProductItemCard(
    product: ProductUi,
    onChangeQuantity: (delta: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NexoraCard(
        modifier = modifier,
        containerColor = Slate50,
        borderColor = Slate200,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
                NexoraText(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Ink,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceMedium)) {
                    InfoText(stringResource(R.string.product_price_format, product.price))
                    InfoText(stringResource(R.string.product_discount_format, product.discountPercent))
                }
            }
            if (product.quantity == 0) {
                NexoraButton(
                    text = stringResource(R.string.products_action_add),
                    onClick = { onChangeQuantity(1) },
                    fillMaxWidth = false,
                    compact = true,
                )
            } else {
                QuantityStepper(
                    quantity = product.quantity,
                    onIncrement = { onChangeQuantity(1) },
                    onDecrement = { onChangeQuantity(-1) },
                )
            }
        }
    }
}

@Composable
private fun InfoText(text: String) {
    NexoraText(text = text, style = MaterialTheme.typography.labelSmall, color = Slate500)
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ProductItemCardPreview() {
    MedicalRepTheme {
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            ProductItemCard(product = ProductsFakes.products()[0], onChangeQuantity = {})
            ProductItemCard(product = ProductsFakes.products()[1], onChangeQuantity = {})
        }
    }
}
