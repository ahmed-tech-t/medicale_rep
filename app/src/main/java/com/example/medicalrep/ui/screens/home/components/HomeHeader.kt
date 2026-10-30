package com.example.medicalrep.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.models.UserUi
import com.example.medicalrep.ui.preview.HomeSamples
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandSoft
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.utils.thenIf

/** Figma home header (node 625:1318): menu + name/role at the start, the bell with its unread count at the end. */
@Composable
fun HomeHeader(
    user: UserUi,
    unreadCount: Int,
    onMenuClick: () -> Unit,
    onBellClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Brand)
            .statusBarsPadding()
            .padding(horizontal = spacing.spaceMediumPlus, vertical = spacing.spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            HeaderIconButton(
                Icons.Filled.Menu,
                showShadow = true,
                description = stringResource(R.string.cd_menu),
                onClick = onMenuClick
            )
            Column(horizontalAlignment = Alignment.Start) {
                NexoraText(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate50,
                    maxLines = 1,
                )
                NexoraText(
                    text = user.roleTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandSoft,
                    maxLines = 1,
                )
            }
        }
        HeaderIconButton(
            Icons.Outlined.Notifications,
            stringResource(R.string.cd_notifications),
            onClick = onBellClick,
            badge = unreadCount
        )
    }
}

/** A bordered icon button on the brand bar; [badge] (when above 0) is the number on its corner. */
@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    description: String,
    showShadow: Boolean = false,
    onClick: () -> Unit,
    badge: Int = 0
) {
    val shape = RoundedCornerShape(radii.large)
    Box {
        Box(
            modifier = Modifier
                .thenIf(showShadow) { Modifier.shadow(1.dp, RoundedCornerShape(radii.large)) }
                .size(dim.iconButtonSize)
                .clip(shape)
                .background(Slate50.copy(alpha = 0.06f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            NexoraIcon(
                imageVector = icon,
                contentDescription = description,
                tint = Slate50,
                modifier = Modifier.size(dim.smallIconSize),
            )
        }
        if (badge > 0) {
            NexoraText(
                text = if (badge > 9) "9+" else badge.toString(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate50,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
                    .padding(horizontal = spacing.spaceExtraSmall),
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun HomeHeaderPreview() {
    MedicalRepTheme {
        HomeHeader(user = HomeSamples.user(), unreadCount = 3, onMenuClick = {}, onBellClick = {})
    }
}

@Preview(showBackground = true, locale = "ar", widthDp = 720)
@Composable
private fun HomeHeaderNoUnreadTabletPreview() {
    MedicalRepTheme {
        HomeHeader(user = HomeSamples.user(), unreadCount = 0, onMenuClick = {}, onBellClick = {})
    }
}
