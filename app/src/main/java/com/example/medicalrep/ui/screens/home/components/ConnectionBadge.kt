package com.example.medicalrep.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Success

@Composable
fun ConnectionBadge(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val tint = if (isOnline) Success else Slate400
    Row(
        modifier = modifier
            .background(tint.copy(alpha = 0.18f), CircleShape)
            .border(DividerDefaults.Thickness, tint.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = spacing.spaceSmallPlus, vertical = spacing.spaceExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        NexoraText(
            text = stringResource(
                if (isOnline) R.string.connection_online else R.string.connection_offline
            ),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate50,
        )
        Box(
            modifier = Modifier
                .size(spacing.spaceSmall)
                .background(tint, CircleShape),
        )
    }
}