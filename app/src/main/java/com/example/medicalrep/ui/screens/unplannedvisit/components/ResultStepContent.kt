package com.example.medicalrep.ui.screens.unplannedvisit.components

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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraIconButton
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitEvents
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitState
import com.example.medicalrep.ui.screens.unplannedvisit.utils.UnplannedVisitFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.Error
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.OnInfoContainer
import com.example.medicalrep.ui.ui.theme.OnWarningContainer
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.WarningContainer

/** Step 3 (Figma 33:6234): visit summary, voice note, photo attachment, "clinic open" confirmation. */
@Composable
fun ResultStepContent(
    state: UnplannedVisitState,
    onEvent: (UnplannedVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        SummaryCard(state = state, onViewLocation = { onEvent(UnplannedVisitEvents.ViewLocationClick) })
        VoiceNoteCard(onRecord = { onEvent(UnplannedVisitEvents.VoiceNoteClick) })
        AttachPhotoCard(onAttach = { onEvent(UnplannedVisitEvents.AttachClick) })
        ClinicOpenRow(
            checked = state.clinicOpenConfirmed,
            onCheckedChange = { onEvent(UnplannedVisitEvents.ToggleClinicOpen(it)) },
        )
    }
}

@Composable
private fun SummaryCard(state: UnplannedVisitState, onViewLocation: () -> Unit) {
    val visit = state.visit
    StepCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraText(
                text = stringResource(R.string.unplanned_summary_title),
                style = MaterialTheme.typography.labelMedium,
                color = Slate500,
            )
            Chip(
                text = stringResource(R.string.unplanned_view_location),
                container = Error,
                content = Slate50,
                modifier = Modifier.clickable(role = Role.Button, onClick = onViewLocation),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radii.large))
                .background(Slate25)
                .border(DividerDefaults.Thickness, Slate200, RoundedCornerShape(radii.large))
                .padding(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            NexoraText(
                text = stringResource(R.string.unplanned_summary_name_format, visit.placeName, visit.specialty),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Ink,
            )
            NexoraText(
                text = stringResource(
                    R.string.unplanned_summary_place_format,
                    visit.city,
                    visit.address,
                    stringResource(R.string.unplanned_class_format, visit.classCategory),
                ),
                style = MaterialTheme.typography.labelMedium,
                color = Slate500,
            )
            HorizontalDivider(color = Slate100)
            SummaryRow(
                label = stringResource(R.string.unplanned_summary_shift_label),
                value = stringResource(if (visit.shift == Shift.Am) R.string.shift_am else R.string.shift_pm),
            )
            HorizontalDivider(color = Slate100)
            SummaryRow(
                label = stringResource(R.string.unplanned_summary_visit_type_label),
                value = if (visit.isDoubleVisit) {
                    stringResource(R.string.unplanned_double_with_format, visit.supervisorName)
                } else {
                    stringResource(R.string.visit_type_single)
                },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            Chip(
                text = stringResource(R.string.unplanned_summary_gifts_format, state.giftsCount),
                container = WarningContainer,
                content = OnWarningContainer,
                modifier = Modifier.weight(1f),
            )
            Chip(
                text = stringResource(R.string.unplanned_summary_products_format, state.productsCount),
                container = InfoContainer,
                content = OnInfoContainer,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(text = label, style = MaterialTheme.typography.labelMedium, color = Slate600)
        Chip(text = value, container = BrandTint, content = Brand)
    }
}

@Composable
private fun Chip(text: String, container: Color, content: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radii.small))
            .background(container)
            .padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceExtraSmall),
        contentAlignment = Alignment.Center,
    ) {
        NexoraText(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = content,
        )
    }
}

@Composable
private fun VoiceNoteCard(onRecord: () -> Unit) {
    StepCard {
        StepCardTitle(stringResource(R.string.unplanned_notes_label))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radii.medium))
                .background(Slate25)
                .border(DividerDefaults.Thickness, Slate200, RoundedCornerShape(radii.medium))
                .padding(spacing.spaceSmall),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexoraText(
                text = stringResource(R.string.unplanned_voice_prompt),
                style = MaterialTheme.typography.labelMedium,
                color = Slate600,
                modifier = Modifier.weight(1f),
            )
            NexoraButton(
                text = stringResource(R.string.unplanned_voice_note),
                onClick = onRecord,
                fillMaxWidth = false,
                compact = true,
            )
        }
    }
}

@Composable
private fun AttachPhotoCard(onAttach: () -> Unit) {
    StepCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(dim.iconButtonSize)
                    .clip(RoundedCornerShape(radii.medium))
                    .background(Slate100),
                contentAlignment = Alignment.Center,
            ) {
                NexoraIcon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = Slate600,
                    modifier = Modifier.size(dim.smallIconSize),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                StepCardTitle(stringResource(R.string.unplanned_attach_title))
                NexoraText(
                    text = stringResource(R.string.unplanned_attach_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                )
            }
            NexoraIconButton(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = stringResource(R.string.unplanned_attach_title),
                onClick = onAttach,
                tint = Slate50,
                modifier = Modifier
                    .clip(RoundedCornerShape(radii.medium))
                    .background(Brand),
            )
        }
    }
}

@Composable
private fun ClinicOpenRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = Brand),
        )
        NexoraText(
            text = stringResource(R.string.unplanned_clinic_open),
            style = MaterialTheme.typography.labelMedium,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
        NexoraIcon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Slate500,
            modifier = Modifier.size(dim.smallIconSize),
        )
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 800)
@Composable
private fun ResultStepContentPreview() {
    MedicalRepTheme {
        ResultStepContent(
            state = UnplannedVisitFakes.state(),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ResultStepContentSingleEnglishPreview() {
    MedicalRepTheme {
        val state = UnplannedVisitFakes.state()
        ResultStepContent(
            state = state.copy(visit = state.visit.copy(isDoubleVisit = false), clinicOpenConfirmed = false),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
