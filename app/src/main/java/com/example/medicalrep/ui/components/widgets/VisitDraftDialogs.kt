package com.example.medicalrep.ui.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicalrep.R
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

/** Leaving a visit in progress: Save (finish later), Don't save (red), Cancel (stay). */
@Composable
fun SaveDraftDialog(onSave: () -> Unit, onDiscard: () -> Unit, onCancel: () -> Unit) {
    ActionDialog(
        titleRes = R.string.draft_save_title,
        messageRes = R.string.draft_save_message,
        actions = listOf(
            DialogAction(R.string.draft_save, onSave),
            DialogAction(R.string.draft_dont_save, onDiscard, destructive = true),
            DialogAction(R.string.draft_cancel, onCancel),
        ),
        onDismiss = onCancel,
    )
}

/** Starting a new visit while one is pending. */
@Composable
fun PendingVisitDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ActionDialog(
        titleRes = R.string.pending_visit_title,
        messageRes = R.string.pending_visit_message,
        actions = listOf(
            DialogAction(R.string.pending_visit_cancel_continue, onConfirm, destructive = true),
            DialogAction(R.string.draft_cancel, onDismiss),
        ),
        onDismiss = onDismiss,
    )
}

/** Cancelling the pending visit from its card. */
@Composable
fun DiscardPendingDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ActionDialog(
        titleRes = R.string.discard_pending_title,
        messageRes = R.string.discard_pending_message,
        actions = listOf(
            DialogAction(R.string.discard_pending_confirm, onConfirm, destructive = true),
            DialogAction(R.string.discard_pending_keep, onDismiss),
        ),
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PendingVisitDialogPreview() {
    MedicalRepTheme { PendingVisitDialog(onConfirm = {}, onDismiss = {}) }
}

@Preview(showBackground = true)
@Composable
private fun DiscardPendingDialogPreview() {
    MedicalRepTheme { DiscardPendingDialog(onConfirm = {}, onDismiss = {}) }
}
