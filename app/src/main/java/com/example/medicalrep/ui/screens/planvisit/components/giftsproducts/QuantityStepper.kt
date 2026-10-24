package com.example.medicalrep.ui.screens.planvisit.components.giftsproducts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

/** Figma stepper: filled "+" (Brand), count, muted "-". "+" is disabled at [canIncrease] = false. */
@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    canIncrease: Boolean = true,
) {
    val shape = RoundedCornerShape(radii.medium)
    Row(
        modifier = modifier
            .background(Slate50, shape)
            .border(DividerDefaults.Thickness, Slate200, shape)
            .padding(spacing.spaceExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        StepperButton(
            isIncrement = true,
            enabled = canIncrease,
            onClick = onIncrement,
            contentDescription = stringResource(R.string.cd_increase_quantity),
        )
        NexoraText(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Ink,
            textAlign = TextAlign.Center,
        )
        StepperButton(
            isIncrement = false,
            enabled = true,
            onClick = onDecrement,
            contentDescription = stringResource(R.string.cd_decrease_quantity),
        )
    }
}

@Composable
private fun StepperButton(
    isIncrement: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(radii.small),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isIncrement) Brand else Slate100,
            contentColor = if (isIncrement) Slate50 else Slate500,
            disabledContainerColor = Slate100,
            disabledContentColor = Slate400,
        ),
        modifier = Modifier.size(dim.smallIconSize + spacing.spaceSmall),
    ) {
        NexoraIcon(
            imageVector = if (isIncrement) Icons.Filled.Add else Icons.Filled.Remove,
            contentDescription = contentDescription,
            modifier = Modifier.size(dim.smallIconSize),
        )
    }
}

/**
 * Figma item card: name + custody line at the start, stepper at the end.
 * [stockLine] is the already-formatted custody text (after-dispense on the step, available in the sheet).
 */
@Composable
fun ProductQuantityRow(
    item: ProductUi,
    stockLine: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(radii.large)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate25, shape)
            .border(DividerDefaults.Thickness, Slate200, shape)
            .padding(spacing.spaceSmallPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
        ) {
            NexoraText(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Ink,
            )
            NexoraText(
                text = stockLine,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500,
            )
        }
        QuantityStepper(
            quantity = item.quantity,
            onIncrement = onIncrement,
            onDecrement = onDecrement,
            canIncrease = item.quantity < item.remaining,
        )
    }
}

/** "Left after handing out" line for an item already on the visit. */
@Composable
fun remainingAfterLine(item: ProductUi): String = stringResource(
    if (item.kind == ProductType.Gift) R.string.unplanned_remaining_after_gift_format
    else R.string.unplanned_remaining_after_product_format,
    item.remaining - item.quantity,
)

/** "Available in your custody now" line for an item picked in the sheet. */
@Composable
fun availableLine(item: ProductUi): String = stringResource(
    if (item.kind == ProductType.Gift) R.string.unplanned_available_gift_format
    else R.string.unplanned_available_product_format,
    item.remaining,
)

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ProductQuantityRowPreview() {
    MedicalRepTheme {
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            val gift = PlanVisitFakes.giftCatalog[0]
            ProductQuantityRow(item = gift, stockLine = remainingAfterLine(gift), onIncrement = {}, onDecrement = {})
            val maxed = PlanVisitFakes.productCatalog[1].copy(quantity = 10)
            ProductQuantityRow(item = maxed, stockLine = remainingAfterLine(maxed), onIncrement = {}, onDecrement = {})
        }
    }
}
