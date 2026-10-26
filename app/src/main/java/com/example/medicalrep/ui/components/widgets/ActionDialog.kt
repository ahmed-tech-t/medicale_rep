package com.example.medicalrep.ui.components.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500

/** One full-width button of an [ActionDialog]; a [destructive] one is red. */
data class DialogAction(
    @StringRes val labelRes: Int,
    val onClick: () -> Unit,
    val destructive: Boolean = false,
)

/** Title, message, then one grey rounded button per action. */
@Composable
fun ActionDialog(
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    actions: List<DialogAction>,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(radii.extraLarge), color = Slate50) {
            Column(
                modifier = Modifier.padding(spacing.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            ) {
                NexoraText(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                )
                NexoraText(text = stringResource(messageRes), style = MaterialTheme.typography.bodySmall, color = Slate500)
                actions.forEach { action ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(radii.large))
                            .background(Slate100)
                            .clickable(onClick = action.onClick)
                            .padding(vertical = spacing.spaceSmallPlus),
                        contentAlignment = Alignment.Center,
                    ) {
                        NexoraText(
                            text = stringResource(action.labelRes),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (action.destructive) MaterialTheme.colorScheme.error else Ink,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ActionDialogPreview() {
    MedicalRepTheme {
        ActionDialog(
            titleRes = R.string.draft_save_title,
            messageRes = R.string.draft_save_message,
            actions = listOf(
                DialogAction(R.string.draft_save, {}),
                DialogAction(R.string.draft_dont_save, {}, destructive = true),
                DialogAction(R.string.draft_cancel, {}),
            ),
            onDismiss = {},
        )
    }
}
