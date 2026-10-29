package com.example.medicalrep.ui.components.widgets.visit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.components.primitives.NexoraIcon
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.mapper.resolvedDisplayName
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.preview.VisitSamples
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate600
import com.example.medicalrep.ui.ui.theme.Slate900
import com.example.medicalrep.ui.ui.theme.SuccessBright
import com.example.medicalrep.ui.ui.theme.SuccessSoft

/**
 * Finished visit: name + subtitle + chips (double visit, date/time, items) and a sync status at the end.
 * Not-yet-synced visits show a sync icon; synced ones show the backend id and a check.
 */

@Composable
fun CompletedVisitCard(
    model: VisitUi,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .visitCardChrome(elevation = 0.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(spacing.spaceSmallPlus),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            Column {
                NexoraText(
                    text = model.resolvedDisplayName(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Slate900,
                )
                if (model.specialty != null) {
                    NexoraText(
                        text = model.specialty.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate500,
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
                verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
            ) {
                if (model.isDoubleVisit) {
                    VisitInfoChip(stringResource(R.string.unplanned_double_with_format, model.supervisor?.name.orEmpty()))
                }

                VisitInfoChip(
                    stringResource(
                        R.string.visit_log_items_format,
                        model.products.sumOf { it.quantity },
                        model.gifts.sumOf { it.quantity },
                    ),
                )
                VisitInfoChip("${model.time} ${model.date}".trim())
            }
        }
        SyncState(model = model)
    }
}

@Composable
private fun VisitInfoChip(text: String) {
    NexoraChip(
        text = text,
        container = Slate100,
        content = Slate600,
        shape = RoundedCornerShape(radii.pill),
    )
}

@Composable
private fun SyncState(model: VisitUi) {
    val synced = model.syncStatus == SyncStatus.Synced
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (synced && model.backendId.isNotBlank()) {
            NexoraText(
                text = stringResource(R.string.visit_log_id_format, model.backendId),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Info,
            )
        }
        Box(
            modifier = Modifier
                .size(dim.largeIconSize)
                .background(if (synced) SuccessSoft else InfoContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (synced) {
                NexoraIcon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = SuccessBright,
                    modifier = Modifier.size(dim.smallIconSize),
                )
            } else {
                NexoraIcon(
                    painter = painterResource(R.drawable.ic_sync),
                    contentDescription = stringResource(R.string.cd_sync),
                    tint = Info,
                    modifier = Modifier.size(dim.smallIconSize),
                )
            }
        }
        Spacer(Modifier.width(1.dp))
    }
}


@Preview(showBackground = true, locale = "ar")
@Composable
private fun CompletedVisitCardNotSyncedPreview() {
    MedicalRepTheme {
        CompletedVisitCard(
            model = VisitSamples.planned().toUi().copy(isDoubleVisit = true, supervisor = BasicModelUi("sup-1", "احمد حسن")),
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun CompletedVisitCardSyncedPreview() {
    MedicalRepTheme {
        CompletedVisitCard(
            model = VisitSamples.unplanned().toUi().copy(
                backendId = "432423",
                syncStatus = SyncStatus.Synced,
                isDoubleVisit = true,
                supervisor = BasicModelUi("sup-1", "احمد حسن"),
            ),
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
