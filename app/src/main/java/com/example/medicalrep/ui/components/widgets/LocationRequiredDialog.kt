package com.example.medicalrep.ui.components.widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.example.medicalrep.R
import com.example.medicalrep.ui.common.location.LocationStatus
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** Cannot be dismissed: it stays until the location permission is granted and GPS is on; the button opens the right settings. */
@Composable
fun LocationRequiredDialog(status: LocationStatus, onActionClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        title = {
            NexoraText(
                text = stringResource(R.string.location_required_title),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            NexoraText(
                text = stringResource(R.string.location_warning),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            NexoraButton(
                text = stringResource(
                    if (status == LocationStatus.PermissionDenied) R.string.location_grant_permission
                    else R.string.location_turn_on_gps,
                ),
                onClick = onActionClick,
                fillMaxWidth = false,
                compact = true,
            )
        },
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun LocationRequiredDialogPermissionPreview() {
    MedicalRepTheme { LocationRequiredDialog(status = LocationStatus.PermissionDenied, onActionClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun LocationRequiredDialogGpsPreview() {
    MedicalRepTheme { LocationRequiredDialog(status = LocationStatus.GpsOff, onActionClick = {}) }
}
