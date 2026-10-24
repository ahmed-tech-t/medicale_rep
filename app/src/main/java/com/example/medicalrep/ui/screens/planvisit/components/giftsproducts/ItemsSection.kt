package com.example.medicalrep.ui.screens.planvisit.components.giftsproducts
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.screens.planvisit.components.StepCardTitle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.InfoBorder
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.OnWarningContainer
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.WarningBorder
import com.example.medicalrep.ui.ui.theme.WarningTint

data class PillColors(val container: Color, val border: Color, val content: Color)

@Composable
internal fun ItemsSection(
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

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ItemsSectionGiftPreview() {
    MedicalRepTheme {
        ItemsSection(
            title = "هدايا",
            addLabel = "هدية",
            pillColors = PillColors(container = WarningTint, border = WarningBorder, content = OnWarningContainer),
            items = PlanVisitFakes.state().visit.gifts,
            onAdd = {},
            onChange = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemsSectionProductEmptyPreview() {
    MedicalRepTheme {
        ItemsSection(
            title = "Products",
            addLabel = "Product",
            pillColors = PillColors(container = BrandTint, border = InfoBorder, content = Brand),
            items = emptyList(),
            onAdd = {},
            onChange = { _, _ -> },
        )
    }
}
