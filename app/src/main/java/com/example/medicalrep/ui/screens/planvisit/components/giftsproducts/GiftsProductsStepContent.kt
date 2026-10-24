package com.example.medicalrep.ui.screens.planvisit.components.giftsproducts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitEvents
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.InfoBorder
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.OnWarningContainer
import com.example.medicalrep.ui.ui.theme.WarningBorder
import com.example.medicalrep.ui.ui.theme.WarningTint

/** Step 2 (Figma 33:6110): gifts section and products section, each with add pill and stepper rows. */
@Composable
fun GiftsProductsStepContent(
    state: PlanVisitState,
    onEvent: (PlanVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {
        ItemsSection(
            title = stringResource(R.string.unplanned_section_gifts),
            addLabel = stringResource(R.string.unplanned_add_gift),
            pillColors = PillColors(container = WarningTint, border = WarningBorder, content = OnWarningContainer),
            items = state.visit.gifts,
            onAdd = { onEvent(PlanVisitEvents.OpenAddItem(ProductType.Gift)) },
            onChange = { id, delta -> onEvent(PlanVisitEvents.ChangeQuantity(ProductType.Gift, id, delta)) },
        )
        ItemsSection(
            title = stringResource(R.string.unplanned_section_products),
            addLabel = stringResource(R.string.unplanned_add_product),
            pillColors = PillColors(container = BrandTint, border = InfoBorder, content = Brand),
            items = state.visit.products,
            onAdd = { onEvent(PlanVisitEvents.OpenAddItem(ProductType.Product)) },
            onChange = { id, delta -> onEvent(PlanVisitEvents.ChangeQuantity(ProductType.Product, id, delta)) },
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 700)
@Composable
private fun GiftsProductsStepContentPreview() {
    MedicalRepTheme {
        GiftsProductsStepContent(
            state = PlanVisitFakes.state(),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
private fun GiftsProductsStepContentEmptyPreview() {
    MedicalRepTheme {
        val state = PlanVisitFakes.state()
        GiftsProductsStepContent(
            state = state.copy(visit = state.visit.copy(gifts = emptyList(), products = emptyList())),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
