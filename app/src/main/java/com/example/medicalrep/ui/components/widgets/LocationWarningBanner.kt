package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.common.location.LocationStatus
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.ErrorBright
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.OnErrorContainer

/** Warning that also fills the status bar, fading from red into the header's primary color; the button opens the settings that fix [status]. */
@Composable
fun LocationWarningBanner(
    status: LocationStatus,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(OnErrorContainer)
            .statusBarsPadding()
            .padding(horizontal = spacing.spaceMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(
            text = stringResource(R.string.location_warning),
            color = MaterialTheme.colorScheme.onError,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        NexoraButton(
            text = stringResource(
                if (status == LocationStatus.PermissionDenied) R.string.location_grant_permission
                else R.string.location_turn_on_gps,
            ),
            onClick = onActionClick,
            variant = NexoraButtonVariant.Text,
            fillMaxWidth = false,
            compact = true,
            textColor = MaterialTheme.colorScheme.onError,
            textStyle = MaterialTheme.typography.labelSmall,
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun LocationWarningBannerPermissionPreview() {
    MedicalRepTheme {
        LocationWarningBanner(
            status = LocationStatus.PermissionDenied,
            onActionClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationWarningBannerGpsPreview() {
    MedicalRepTheme { LocationWarningBanner(status = LocationStatus.GpsOff, onActionClick = {}) }
}
