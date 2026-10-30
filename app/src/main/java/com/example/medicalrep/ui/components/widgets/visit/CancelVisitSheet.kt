package com.example.medicalrep.ui.components.widgets.visit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.components.widgets.BottomSheetComponent
import com.example.medicalrep.ui.ui.theme.Error
import com.example.medicalrep.ui.ui.theme.ErrorContainer
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate500

/** Figma 622:1188: asks why a visit is cancelled; [onConfirm] only works while the [reason] has text. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelVisitSheet(
    reason: TextFieldState,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomSheetComponent(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onBack,
        content = { CancelVisitSheetContent(reason = reason, onConfirm = onConfirm, onBack = onBack) },
    )
}

@Composable
internal fun CancelVisitSheetContent(
    reason: TextFieldState,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            Box(
                modifier = Modifier
                    .size(dim.iconButtonSize)
                    .background(ErrorContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                NexoraIcon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(dim.smallIconSize),
                )
            }
            NexoraText(
                text = stringResource(R.string.cancel_visit_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
                modifier = Modifier.weight(1f),
            )
            NexoraIconButton(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.cd_close),
                onClick = onBack,
                tint = Slate500,
            )
        }
        NexoraText(
            text = stringResource(R.string.cancel_visit_hint),
            style = MaterialTheme.typography.bodySmall,
            color = Slate500,
        )
        HorizontalDivider(thickness = DividerDefaults.Thickness)
        NexoraTextField(
            state = reason,
            label = stringResource(R.string.cancel_visit_notes),
            placeholder = stringResource(R.string.cancel_visit_notes_hint),
            singleLine = false,
            minLines = MinNotesLines,
        )
        HorizontalDivider(thickness = DividerDefaults.Thickness)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            NexoraButton(
                text = stringResource(R.string.cancel_visit_confirm),
                onClick = onConfirm,
                enabled = reason.text.isNotBlank(),
                variant = NexoraButtonVariant.Danger,
                modifier = Modifier.weight(ConfirmWeight),
                leadingIcon = {
                    NexoraIcon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(dim.smallIconSize),
                    )
                },
            )
            NexoraButton(
                text = stringResource(R.string.cancel_visit_back),
                onClick = onBack,
                variant = NexoraButtonVariant.Muted,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private const val MinNotesLines = 5
private const val ConfirmWeight = 2f

@Preview(showBackground = true, locale = "ar")
@Composable
private fun CancelVisitSheetEmptyPreview() {
    MedicalRepTheme { CancelVisitSheetContent(reason = TextFieldState(), onConfirm = {}, onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun CancelVisitSheetFilledPreview() {
    MedicalRepTheme {
        CancelVisitSheetContent(
            reason = TextFieldState().apply { setTextAndPlaceCursorAtEnd("The doctor is travelling this week") },
            onConfirm = {},
            onBack = {},
        )
    }
}
