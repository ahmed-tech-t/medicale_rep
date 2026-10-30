package com.example.medicalrep.ui.components.widgets.visit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.ui.theme.Error
import com.example.medicalrep.ui.ui.theme.ErrorBright
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate900
import com.example.medicalrep.ui.ui.theme.Success
import com.example.medicalrep.ui.ui.theme.Warning

/**
 * Finished visit (Figma 622:1187), collapsible: the header shows the doctor, specialty, clinic and sync state;
 * opened, it lists the supervisor or visit type, time, samples and gifts, the upload note of a failed visit,
 * and a footer with the sync hint (or the backend ID) and "View visit report".
 */
@Composable
fun CompletedVisitCard(
    model: VisitUi,
    onViewReport: () -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
) {
    var expanded by rememberSaveable(model.id) { mutableStateOf(initiallyExpanded) }
    val doctorName = model.doctors.firstOrNull()?.name
    Column(modifier = modifier.visitCardChrome(elevation = 0.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(spacing.spaceSmallPlus),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NexoraText(
                    text = doctorName ?: model.resolvedDisplayName(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Slate900,
                )
                val subtitle = listOfNotNull(
                    model.specialty?.name,
                    model.placeName.takeIf { doctorName != null && it.isNotBlank() },
                ).joinToString(" \u2022 ")
                if (subtitle.isNotBlank()) {
                    NexoraText(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate500
                    )
                }
            }
            SyncPill(status = model.syncStatus)
            NexoraIcon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(if (expanded) R.string.cd_collapse else R.string.cd_expand),
                tint = Slate500,
            )
        }
        AnimatedVisibility(visible = expanded) {
            CompletedVisitDetails(model = model, onViewReport = onViewReport)
        }
    }
}

@Composable
private fun CompletedVisitDetails(model: VisitUi, onViewReport: () -> Unit) {
    val synced = model.syncStatus == SyncStatus.Synced
    Column {
        CardDivider()
        if (model.isDoubleVisit) {
            InfoRow(label = stringResource(R.string.visit_log_supervisor)) {
                NexoraText(
                    text = model.supervisor?.name.orEmpty(),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Slate900,
                )
                VisitInfoChip(stringResource(R.string.visit_log_double_visit))
            }
        } else {
            InfoRow(label = stringResource(R.string.visit_type_label)) {
                VisitInfoChip(stringResource(R.string.visit_log_single_visit))
            }
        }
        CardDivider()
        InfoRow(label = stringResource(R.string.visit_log_visit_time)) {
            Text(
                text = buildAnnotatedString {
                    append(model.date)
                    append(" \u2022 ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Slate900)) {
                        append(
                            model.time
                        )
                    }
                },
                style = MaterialTheme.typography.labelLarge,
                color = Slate500,
            )
        }
        CardDivider()
        InfoRow(label = stringResource(R.string.visit_log_samples_and_gifts)) {
            val samples = model.products.sumOf { it.quantity }
            val gifts = model.gifts.sumOf { it.quantity }
            NexoraText(
                text = pluralStringResource(
                    R.plurals.visit_log_samples,
                    samples,
                    samples
                ) + " \u2022 " +
                        pluralStringResource(R.plurals.visit_log_gifts, gifts, gifts),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Slate900,
            )
        }
        if (model.syncStatus == SyncStatus.Failed) UploadFailedNote(modifier = Modifier.padding(bottom = spacing.spaceSmall))
        CardDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.spaceSmallPlus),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (synced) {
                NexoraChip(
                    modifier = Modifier.weight(1f,fill = false),
                    text = stringResource(R.string.visit_log_id_format, model.backendId),
                    container = Slate100,
                    content = Slate600,
                    shape = RoundedCornerShape(radii.small),
                    border = Slate200,
                )
            } else if (!synced) {
                NexoraText(
                    modifier = Modifier.weight(1f,fill = false),
                    text = stringResource(
                        if (model.syncStatus == SyncStatus.Failed) R.string.visit_log_not_uploaded else R.string.visit_log_sync_pending_hint,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate500,
                )
            }
            Row(
                modifier = Modifier.clickable(onClick = onViewReport),
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NexoraText(
                    text = stringResource(R.string.visit_log_view_report),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
                NexoraIcon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dim.smallIconSize),
                )
            }
        }
    }
}

/** A label at the start and its value at the end. */
@Composable
private fun InfoRow(label: String, value: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spaceSmallPlus, vertical = spacing.spaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexoraText(text = label, style = MaterialTheme.typography.labelMedium, color = Slate500)
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            content = value
        )
    }
}

@Composable
private fun CardDivider() = HorizontalDivider(color = Slate200)

@Composable
private fun VisitInfoChip(text: String) {
    NexoraChip(
        text = text,
        container = Slate100,
        content = Slate600,
        shape = RoundedCornerShape(radii.small),
        border = Slate200,
    )
}

/** White pill with a coloured dot: green synced, amber waiting for the internet, red failed. */
@Composable
private fun SyncPill(status: SyncStatus) {
    val dot = when (status) {
        SyncStatus.Synced -> Success
        SyncStatus.NotSynced -> Warning
        SyncStatus.Failed -> ErrorBright
    }
    Row(
        modifier = Modifier
            .border(DividerDefaults.Thickness, Slate200, RoundedCornerShape(radii.pill))
            .padding(horizontal = spacing.spaceSmall, vertical = spacing.spaceExtraSmall),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier
            .size(spacing.spaceSmall)
            .background(dot, CircleShape))
        NexoraText(
            text = stringResource(
                when (status) {
                    SyncStatus.Synced -> R.string.filter_synced_done
                    SyncStatus.NotSynced, SyncStatus.Failed -> R.string.visit_log_not_synced
                },
            ),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate900,
        )
    }
}

/** Shown when the last upload failed: the data is safe on the phone and is sent again. */
@Composable
private fun UploadFailedNote(modifier: Modifier= Modifier) {
    val shape = RoundedCornerShape(radii.large)
    NexoraText(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Error, fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.visit_log_sync_failed_title))
            }
            append(" ")
            append(stringResource(R.string.visit_log_sync_failed_body))
        },
        style = MaterialTheme.typography.bodySmall,
        color = Slate900,
        modifier = modifier
            .padding(horizontal = spacing.spaceSmallPlus)
            .fillMaxWidth()
            .background(Slate25, shape)
            .border(DividerDefaults.Thickness, Slate200, shape)
            .padding(spacing.spaceSmallPlus),
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun CompletedVisitCardCollapsedPreview() {
    MedicalRepTheme {
        CompletedVisitCard(
            model = VisitSamples.planned().toUi()
                .copy(isDoubleVisit = true, supervisor = BasicModelUi("sup-1", "احمد حسن")),
            onViewReport = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun CompletedVisitCardFailedExpandedPreview() {
    MedicalRepTheme {
        CompletedVisitCard(
            model = VisitSamples.planned().toUi().copy(
                syncStatus = SyncStatus.Failed,
                isDoubleVisit = true,
                supervisor = BasicModelUi("sup-1", "احمد حسن")
            ),
            onViewReport = {},
            initiallyExpanded = true,
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletedVisitCardSyncedExpandedPreview() {
    MedicalRepTheme {
        CompletedVisitCard(
            model = VisitSamples.unplanned().toUi().copy(
                backendId = "432423",
                syncStatus = SyncStatus.Synced,
                isDoubleVisit = true,
                supervisor = BasicModelUi("sup-1", "Ahmed Hassan"),
            ),
            onViewReport = {},
            initiallyExpanded = true,
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
