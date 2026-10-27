package com.example.medicalrep.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandSoft
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

/** A shortcut: a title and a hint. The [primary] one is filled; the others are outlined with an arrow. */
@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
) {
    NexoraCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = if (primary) Brand else Slate50,
        borderColor = Brand,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            NexoraText(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = if (primary) Slate50 else Brand,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NexoraText(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (primary) BrandSoft else Slate500,
                )
                if (!primary) NexoraIcon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Brand,
                    modifier = Modifier.size(dim.smallIconSize)
                )

            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun QuickActionCardPreview() {
    MedicalRepTheme {
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
        ) {
            QuickActionCard(
                title = "إضافة زيارة",
                subtitle = "سجّل زيارة جديدة",
                onClick = {},
                primary = true
            )
            QuickActionCard(title = "تخطيط الزيارات", subtitle = "خطّط ليومك", onClick = {})
        }
    }
}
