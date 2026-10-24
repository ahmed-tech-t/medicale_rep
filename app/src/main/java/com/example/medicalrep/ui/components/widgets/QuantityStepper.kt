package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
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


@Preview(showBackground = true, locale = "ar")
@Composable
private fun QuantityStepperPreview() {
    MedicalRepTheme {
        Row(
            modifier = Modifier.padding(spacing.spaceMedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            QuantityStepper(quantity = 2, onIncrement = {}, onDecrement = {})
            QuantityStepper(quantity = 10, onIncrement = {}, onDecrement = {}, canIncrease = false)
        }
    }
}
