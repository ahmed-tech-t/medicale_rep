package com.example.medicalrep.ui.screens.orders.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.OrderItemUi
import com.example.medicalrep.ui.models.OrderUi
import com.example.medicalrep.ui.screens.orders.utils.OrdersFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.utils.formatMoney

/** An order: number, date and total; tapping it shows its lines (product, price, discount and quantity). */
@Composable
fun OrderCard(
    order: OrderUi,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val total = remember(order.total) { formatMoney(order.total) }
    NexoraCard(modifier = modifier, onClick = onToggle, borderColor = Slate200) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NexoraText(
                    text = stringResource(R.string.orders_order_number, order.number),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Ink,
                )
                NexoraText(
                    text = stringResource(R.string.orders_date_time, order.date, order.time),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                )
            }
            NexoraText(
                text = stringResource(R.string.money_egp, total),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
            )
            NexoraIcon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(if (expanded) R.string.cd_collapse else R.string.cd_expand),
                tint = Slate500,
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
                HorizontalDivider(color = Slate200, modifier = Modifier.padding(top = spacing.spaceSmall))
                order.items.forEach { OrderLine(it) }
            }
        }
    }
}

@Composable
private fun OrderLine(item: OrderItemUi, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
        NexoraText(
            text = item.name,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Ink,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus)) {
            val details = listOf(
                stringResource(R.string.orders_price, item.unitPrice),
                stringResource(R.string.orders_discount, item.discountPercent),
                stringResource(R.string.orders_quantity, item.quantity),
            )
            details.forEach { NexoraText(text = it, style = MaterialTheme.typography.labelSmall, color = Slate500) }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun OrderCardExpandedPreview() {
    MedicalRepTheme {
        OrderCard(order = OrdersFakes.orders().first(), expanded = true, onToggle = {}, modifier = Modifier.padding(spacing.spaceMedium))
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun OrderCardCollapsedPreview() {
    MedicalRepTheme {
        OrderCard(order = OrdersFakes.orders()[1], expanded = false, onToggle = {}, modifier = Modifier.padding(spacing.spaceMedium))
    }
}
