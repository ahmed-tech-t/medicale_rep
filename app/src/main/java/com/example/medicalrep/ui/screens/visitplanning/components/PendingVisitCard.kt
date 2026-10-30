package com.example.medicalrep.ui.screens.visitplanning.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.ui.components.primitives.NexoraCard
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.StatusBadge
import com.example.medicalrep.ui.models.StatusTone
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.mapper.resolvedAccountSpecialtyLine
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Error
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50

/** Figma 591:2917 request card: status, doctor and account, day, why it was refused, when it was sent and what comes next. */
@Composable
fun PendingVisitCard(
    visit: VisitUi,
    onPickAnotherTime: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NexoraCard(
        modifier = modifier,
        containerColor = Slate50,
        borderColor = Slate200,
        contentPadding = PaddingValues(horizontal = spacing.spaceMedium, vertical = 14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    NexoraText(
                        text = visit.resolvedDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                    )
                    NexoraText(
                        text = visit.resolvedAccountSpecialtyLine(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = Slate400,
                    )
                }
                RequestStatusPill(visit.approval)
            }
            DayChip(visit.dayLabel)
            if (visit.approval == ApprovalStatus.Rejected) RejectionReasonBox(visit.rejectionReason)
            HorizontalDivider(color = Slate100)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (visit.approval == ApprovalStatus.Rejected) PickAnotherTimeButton(onClick = onPickAnotherTime)
                else NexoraText(
                    text = stringResource(
                        if (visit.approval == ApprovalStatus.Approved) R.string.request_note_approved else R.string.request_note_pending,
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                    color = Ink,
                )
                NexoraText(
                    text = visit.requestedAtLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                    color = Slate400,
                )
            }
        }
    }
}

/** The answer of the admin with its dot: waiting, approved or rejected. */
@Composable
private fun RequestStatusPill(approval: ApprovalStatus, modifier: Modifier = Modifier) {
    StatusBadge(
        text = stringResource(
            when (approval) {
                ApprovalStatus.Pending -> R.string.request_status_pending
                ApprovalStatus.Approved -> R.string.request_status_approved
                ApprovalStatus.Rejected -> R.string.request_status_rejected
            },
        ),
        tone = when (approval) {
            ApprovalStatus.Pending -> StatusTone.Warning
            ApprovalStatus.Approved -> StatusTone.Success
            ApprovalStatus.Rejected -> StatusTone.Danger
        },
        modifier = modifier,
        showDot = true,
        bordered = true,
    )
}

@Composable
private fun DayChip(label: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(radii.small)
    NexoraText(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = Ink,
        modifier = modifier
            .background(Slate25, shape)
            .border(DividerDefaults.Thickness, Slate200, shape)
            .padding(horizontal = 10.dp, vertical = 1.dp),
    )
}

@Composable
private fun RejectionReasonBox(reason: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(radii.medium)
    NexoraText(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Error, fontWeight = FontWeight.Bold)) { append(stringResource(R.string.request_rejection_reason)) }
            append(" ")
            append(reason)
        },
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
        color = Ink,
        modifier = modifier
            .fillMaxWidth()
            .background(Slate25, shape)
            .border(DividerDefaults.Thickness, Slate200, shape)
            .padding(spacing.spaceSmallPlus),
    )
}

@Composable
private fun PickAnotherTimeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(radii.small)
    NexoraText(
        text = stringResource(R.string.request_pick_another_time),
        style = MaterialTheme.typography.bodyMedium,
        color = Brand,
        modifier = modifier
            .clickable(onClick = onClick)
            .border(BorderStroke(DividerDefaults.Thickness, Brand), shape)
            .padding(horizontal = spacing.spaceSmallPlus, vertical = 4.dp),
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PendingVisitCardPreview() {
    MedicalRepTheme {
        Column(
            modifier = Modifier.padding(spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
        ) {
            ApprovalStatus.entries.forEach { approval ->
                PendingVisitCard(
                    visit = VisitPlanningFakes.pendingVisits().first().copy(
                        approval = approval,
                        rejectionReason = "تم تجاوز الحد الأقصى للزيارات اليومية المسموح بها في هذا الخط.",
                    ),
                    onPickAnotherTime = {},
                )
            }
        }
    }
}
