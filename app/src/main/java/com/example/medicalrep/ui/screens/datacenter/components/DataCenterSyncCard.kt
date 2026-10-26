package com.example.medicalrep.ui.screens.datacenter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncStatus
import com.example.medicalrep.ui.screens.datacenter.utils.DataCenterFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.CardBorderSoft
import com.example.medicalrep.ui.ui.theme.InfoBorder
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate900

@Composable
fun DataCenterSyncCard(
    item: DataCenterSyncItemUi,
    onSync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(radii.extraLarge)
    val background = remember(item.isHighlighted) {
        if (item.isHighlighted) Brush.verticalGradient(listOf(Slate50, BrandTint))
        else Brush.verticalGradient(listOf(Slate50, Slate50))
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .border(DividerDefaults.Thickness, if (item.isHighlighted) InfoBorder else CardBorderSoft, shape)
            .padding(spacing.spaceSmallPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
        ) {
            NexoraText(
                text = stringResource(item.titleRes),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate900,
            )
            NexoraText(
                text = stringResource(item.subtitleRes),
                style = MaterialTheme.typography.labelMedium,
                color = Slate500,
            )
            NexoraText(
                text = stringResource(R.string.data_center_last_updated_minutes_format, item.lastUpdatedMinutesAgo),
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                modifier = Modifier.padding(top = spacing.spaceExtraExtraSmall),
            )
        }
        SyncButton(item = item, onSync = onSync)
    }
}

@Composable
private fun SyncButton(item: DataCenterSyncItemUi, onSync: () -> Unit) {
    val shape = RoundedCornerShape(radii.large)
    val syncing = item.status == DataCenterSyncStatus.Syncing
    Box(
        modifier = Modifier
            .size(dim.thumbnailSize)
            .clip(shape)
            .background(if (item.isHighlighted) Brand else BrandTint)
            .then(
                if (item.isHighlighted) Modifier
                else Modifier.border(DividerDefaults.Thickness, InfoContainer, shape)
            )
            .clickable(enabled = !syncing, role = Role.Button, onClick = onSync),
        contentAlignment = Alignment.Center,
    ) {
        val contentColor = if (item.isHighlighted) Slate50 else Brand
        if (syncing) {
            CircularProgressIndicator(
                color = contentColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(dim.smallIconSize),
            )
        } else {
            NexoraIcon(
                painter = painterResource(R.drawable.ic_sync_download),
                contentDescription = stringResource(R.string.cd_sync),
                tint = contentColor,
                modifier = Modifier.size(dim.smallIconSize),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun DataCenterSyncCardHighlightedPreview() {
    MedicalRepTheme {
        DataCenterSyncCard(
            item = DataCenterFakes.items().first(),
            onSync = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun DataCenterSyncCardPreview() {
    MedicalRepTheme {
        DataCenterSyncCard(
            item = DataCenterFakes.items()[1],
            onSync = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DataCenterSyncCardSyncingPreview() {
    MedicalRepTheme {
        DataCenterSyncCard(
            item = DataCenterFakes.items()[1].copy(status = DataCenterSyncStatus.Syncing),
            onSync = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
