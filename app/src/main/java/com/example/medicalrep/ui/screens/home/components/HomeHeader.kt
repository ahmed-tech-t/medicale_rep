package com.example.medicalrep.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.UserUi
import com.example.medicalrep.ui.preview.HomeSamples
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandSoft
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Success

/** Figma home header (node 33:8060): menu + name/role at the start, connection badge at the end. */
@Composable
fun HomeHeader(
    user: UserUi,
    connection: ConnectionState,
    onMenuClick: () -> Unit,
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
            NexoraIconButton(
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.cd_menu),
                onClick = onMenuClick,
                tint = Slate50,
                modifier = Modifier
                    .size(dim.iconButtonSize)
                    .border(
                        DividerDefaults.Thickness,
                        Slate50.copy(alpha = 0.12f),
                        RoundedCornerShape(radii.large)
                    )
                    .background(Slate50.copy(alpha = 0.06f), RoundedCornerShape(radii.large)),
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
        ConnectionBadge(
            isOnline = connection == ConnectionState.Online,
        )
    }
}



@Preview(showBackground = true, locale = "ar")
@Composable
private fun HomeHeaderOnlinePreview() {
    MedicalRepTheme {
        HomeHeader(
            user = HomeSamples.user().toUi(),
            connection = ConnectionState.Online,
            onMenuClick = {},
        )
    }
}

@Preview(showBackground = true, locale = "ar", widthDp = 720)
@Composable
private fun HomeHeaderOfflineTabletPreview() {
    MedicalRepTheme {
        HomeHeader(
            user = HomeSamples.user().toUi(),
            connection = ConnectionState.Offline,
            onMenuClick = {},
        )
    }
}
