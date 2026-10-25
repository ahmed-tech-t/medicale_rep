package com.example.medicalrep.ui.screens.planvisit.components.giftsproducts

import com.example.medicalrep.ui.screens.planvisit.components.PlanVisitTestTags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.LabelWrapperComponent
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitEvents
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitOverlay
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVisitItemSheetHost(
    state: PlanVisitState,
    onEvent: (PlanVisitEvents) -> Unit,
) {
    val overlay = state.overlay as? PlanVisitOverlay.AddItem ?: return
    val catalog = if (overlay.kind == ProductType.Gift) state.giftCatalog else state.productCatalog

    ModalBottomSheet(
        onDismissRequest = { onEvent(PlanVisitEvents.DismissOverlay) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Slate50,
    ) {
        AddVisitItemSheetContent(
            kind = overlay.kind,
            catalog = catalog,
            onConfirm = { onEvent(PlanVisitEvents.ConfirmAddItems(it)) },
            onDismiss = { onEvent(PlanVisitEvents.DismissOverlay) },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = spacing.spaceMediumPlus)
                .padding(bottom = spacing.spaceMedium),
        )
    }
}

/** Figma 33:6522: title + close, item select, picked items with steppers, save. */
@Composable
fun AddVisitItemSheetContent(
    kind: ProductType,
    catalog: List<ProductUi>,
    onConfirm: (List<ProductUi>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val picked = remember { mutableStateListOf<ProductUi>() }
    var lastPicked by remember { mutableStateOf("") }
    val catalogNames = remember(catalog) { catalog.map { it.name } }
    val isGift = kind == ProductType.Gift
    var showPopup by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .testTag(PlanVisitTestTags.AddItemSheet),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraText(
                text = stringResource(
                    if (isGift) R.string.unplanned_sheet_title_gifts else R.string.unplanned_sheet_title_products,
                ),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
                modifier = Modifier.weight(1f),
            )
            NexoraIconButton(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.cd_close),
                onClick = onDismiss,
                tint = Slate600,
            )
        }

        LabelWrapperComponent(
            title = stringResource(R.string.unplanned_sheet_search_label),
            modifier = Modifier.fillMaxWidth(),
        ) {
            SearchFieldComponent(
                modifier = Modifier.fillMaxWidth(),
                value = lastPicked,
                options = catalogNames,
                onSelect = { name ->
                    if (name.isBlank()) return@SearchFieldComponent
                    lastPicked = name
                    val item =
                        catalog.firstOrNull { it.name == name } ?: return@SearchFieldComponent
                    if (picked.none { it.id == item.id } && item.remaining > 0) picked.add(
                        item.copy(
                            quantity = 1
                        )
                    )
                    showPopup = false
                },
                placeholder = stringResource(
                    if (isGift) R.string.unplanned_sheet_search_hint_gift else R.string.unplanned_sheet_search_hint_product,
                ),
                showPopup = showPopup,
                onClick = { showPopup = !showPopup }
            )
        }

        if (picked.isEmpty()) {
            NexoraText(
                text = stringResource(R.string.unplanned_no_items),
                style = MaterialTheme.typography.labelMedium,
                color = Slate500,
            )
        }
        picked.forEach { item ->
            ProductQuantityRow(
                item = item,
                stockLine = availableLine(item),
                onIncrement = { picked.changeQuantity(item.id, +1) },
                onDecrement = { picked.changeQuantity(item.id, -1) },
            )
        }

        NexoraButton(
            text = stringResource(R.string.unplanned_sheet_save),
            onClick = { onConfirm(picked.toList()) },
            enabled = picked.isNotEmpty(),
        )
    }
}

private fun SnapshotStateList<ProductUi>.changeQuantity(id: String, delta: Int) {
    val index = indexOfFirst { it.id == id }
    if (index < 0) return
    val item = this[index]
    val quantity = (item.quantity + delta).coerceAtMost(item.remaining)
    if (quantity <= 0) removeAt(index) else this[index] = item.copy(quantity = quantity)
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun AddVisitItemSheetContentPreview() {
    MedicalRepTheme {
        AddVisitItemSheetContent(
            kind = ProductType.Gift,
            catalog = PlanVisitFakes.giftCatalog,
            onConfirm = {},
            onDismiss = {},
            modifier = Modifier.padding(spacing.spaceMediumPlus),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddVisitItemSheetContentProductsEnglishPreview() {
    MedicalRepTheme {
        AddVisitItemSheetContent(
            kind = ProductType.Product,
            catalog = PlanVisitFakes.productCatalog,
            onConfirm = {},
            onDismiss = {},
            modifier = Modifier.padding(spacing.spaceMediumPlus),
        )
    }
}
