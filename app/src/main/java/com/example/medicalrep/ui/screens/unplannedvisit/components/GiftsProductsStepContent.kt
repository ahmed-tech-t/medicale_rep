package com.example.medicalrep.ui.screens.unplannedvisit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitEvents
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitState
import com.example.medicalrep.ui.screens.unplannedvisit.utils.UnplannedVisitFakes
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.OnInfoContainer
import com.example.medicalrep.ui.ui.theme.OnWarningContainer
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Warning
import com.example.medicalrep.ui.ui.theme.WarningContainer

/** Step 2 (Figma 33:6110): gifts section and products section, each with add pill and stepper rows. */
@Composable
fun GiftsProductsStepContent(
    state: UnplannedVisitState,
    onEvent: (UnplannedVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {
        ItemsSection(
            title = stringResource(R.string.unplanned_section_gifts),
            addLabel = stringResource(R.string.unplanned_add_gift),
            pillColors = PillColors(container = WarningContainer, border = Warning, content = OnWarningContainer),
            items = state.visit.gifts,
            onAdd = { onEvent(UnplannedVisitEvents.OpenAddItem(ProductType.Gift)) },
            onChange = { id, delta -> onEvent(UnplannedVisitEvents.ChangeQuantity(ProductType.Gift, id, delta)) },
        )
        ItemsSection(
            title = stringResource(R.string.unplanned_section_products),
            addLabel = stringResource(R.string.unplanned_add_product),
            pillColors = PillColors(container = InfoContainer, border = Info, content = OnInfoContainer),
            items = state.visit.products,
            onAdd = { onEvent(UnplannedVisitEvents.OpenAddItem(ProductType.Product)) },
            onChange = { id, delta -> onEvent(UnplannedVisitEvents.ChangeQuantity(ProductType.Product, id, delta)) },
        )
    }
}

private data class PillColors(val container: Color, val border: Color, val content: Color)

@Composable
private fun ItemsSection(
    title: String,
    addLabel: String,
    pillColors: PillColors,
    items: List<ProductUi>,
    onAdd: () -> Unit,
    onChange: (id: String, delta: Int) -> Unit,
) {
    StepCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepCardTitle(title)
            val shape = RoundedCornerShape(radii.small)
            NexoraText(
                text = stringResource(R.string.unplanned_add_format, addLabel),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = pillColors.content,
                modifier = Modifier
                    .clip(shape)
                    .background(pillColors.container)
                    .border(DividerDefaults.Thickness, pillColors.border, shape)
                    .clickable(role = Role.Button, onClick = onAdd)
                    .padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceExtraSmall),
            )
        }
        HorizontalDivider(color = Slate100)
        if (items.isEmpty()) {
            NexoraText(
                text = stringResource(R.string.unplanned_no_items),
                style = MaterialTheme.typography.labelMedium,
                color = Slate500,
            )
        }
        items.forEach { item ->
            ProductQuantityRow(
                item = item,
                stockLine = remainingAfterLine(item),
                onIncrement = { onChange(item.id, +1) },
                onDecrement = { onChange(item.id, -1) },
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 700)
@Composable
private fun GiftsProductsStepContentPreview() {
    MedicalRepTheme {
        GiftsProductsStepContent(
            state = UnplannedVisitFakes.state(),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
private fun GiftsProductsStepContentEmptyPreview() {
    MedicalRepTheme {
        val state = UnplannedVisitFakes.state()
        GiftsProductsStepContent(
            state = state.copy(visit = state.visit.copy(gifts = emptyList(), products = emptyList())),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
