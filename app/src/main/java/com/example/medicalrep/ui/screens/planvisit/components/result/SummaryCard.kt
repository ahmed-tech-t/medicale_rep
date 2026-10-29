package com.example.medicalrep.ui.screens.planvisit.components.result
import com.example.medicalrep.ui.screens.planvisit.components.StepCard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.ui.theme.BrandBorder
import com.example.medicalrep.ui.ui.theme.BrandTint
import com.example.medicalrep.ui.ui.theme.ErrorBright
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoBorder
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.OnInfoContainer
import com.example.medicalrep.ui.ui.theme.OnWarningContainer
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate50
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate700
import com.example.medicalrep.ui.ui.theme.Slate900
import com.example.medicalrep.ui.ui.theme.WarningBorder
import com.example.medicalrep.ui.ui.theme.WarningTint

@Composable
internal fun SummaryCard(state: PlanVisitState, onViewLocation: () -> Unit) {
    val visit = state.visit
    StepCard {
        NexoraText(
            text = stringResource(R.string.unplanned_summary_title),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate400,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radii.large))
                .background(Slate50)
                .border(DividerDefaults.Thickness, Slate200, RoundedCornerShape(radii.large))
                .padding(spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) {
            NexoraText(
                text = stringResource(
                    R.string.unplanned_summary_name_format,
                    visit.placeName,
                    visit.specialty?.name.orEmpty()
                ),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Slate900,
            )
            NexoraText(
                text = stringResource(
                    R.string.unplanned_summary_place_format,
                    visit.city?.name.orEmpty(),
                    visit.address,
                    stringResource(R.string.unplanned_class_format, visit.classCategory?.name.orEmpty()),
                ),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
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
                    stringResource(R.string.unplanned_double_with_format, visit.supervisor?.name.orEmpty())
                } else {
                    stringResource(R.string.visit_type_single)
                },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            NexoraChip(
                text = stringResource(R.string.unplanned_summary_gifts_format, state.giftsCount),
                container = WarningTint,
                content = OnWarningContainer,
                border = WarningBorder,
                modifier = Modifier.weight(1f),
            )
            NexoraChip(
                text = stringResource(R.string.unplanned_summary_products_format, state.productsCount),
                container = BrandTint,
                content = OnInfoContainer,
                border = InfoBorder,
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
        NexoraText(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate700,
        )
        NexoraChip(text = value, container = BrandTint, content = Info, border = BrandBorder)
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SummaryCardArabicPreview() {
    MedicalRepTheme {
        SummaryCard(state = PlanVisitFakes.state(), onViewLocation = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryCardEnglishPreview() {
    MedicalRepTheme {
        val state = PlanVisitFakes.state()
        SummaryCard(
            state = state.copy(visit = state.visit.copy(isDoubleVisit = false)),
            onViewLocation = {},
        )
    }
}
