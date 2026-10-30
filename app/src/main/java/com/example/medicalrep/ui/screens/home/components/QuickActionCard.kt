package com.example.medicalrep.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Ink

/** A shortcut: an icon over a title. The [primary] one is filled; the others are outlined. */
@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
) {
    NexoraCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = if (primary) Brand else Slate50,
        borderColor = if (primary) Brand else BrandTint,
        contentPadding = PaddingValues(spacing.spaceSmall),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            Box(
                modifier = Modifier
                    .size(dim.iconButtonSize)
                    .background(if (primary) Slate50.copy(alpha = 0.16f) else BrandTint, RoundedCornerShape(radii.large)),
                contentAlignment = Alignment.Center,
            ) {
                NexoraIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (primary) Slate50 else Brand,
                    modifier = Modifier.size(dim.smallIconSize),
                )
            }
            NexoraText(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = if (primary) Slate50 else Ink,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun QuickActionCardPreview() {
    MedicalRepTheme {
        Row(
            modifier = Modifier.padding(spacing.spaceMedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            QuickActionCard(title = "إضافة زيارة", icon = Icons.Outlined.Add, onClick = {}, primary = true, modifier = Modifier.weight(1f))
            QuickActionCard(title = "تخطيط الزيارات", icon = Icons.Outlined.CalendarMonth, onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}
