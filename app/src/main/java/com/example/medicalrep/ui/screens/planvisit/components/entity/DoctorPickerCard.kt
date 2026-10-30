package com.example.medicalrep.ui.screens.planvisit.components.entity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.models.DoctorUi
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.screens.planvisit.components.StepCardTitle
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate25
import com.example.medicalrep.ui.ui.theme.Slate400
import com.example.medicalrep.ui.ui.theme.Slate500
import com.example.medicalrep.ui.ui.theme.Slate800

/** The doctors of the chosen place: pick one or more from the list, each picked doctor is a row that can be removed. */
@Composable
fun DoctorPickerCard(
    options: List<DoctorUi>,
    selected: List<DoctorUi>,
    onConfirm: (List<DoctorUi>) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showPopup by remember { mutableStateOf(false) }
    val selectedNames = remember(selected) { selected.joinToString(", ") { it.name } }

    StepCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
            StepCardTitle(stringResource(R.string.unplanned_section_doctors))
            NexoraText(
                text = stringResource(R.string.unplanned_doctors_hint),
                style = MaterialTheme.typography.labelSmall,
                color = Slate500,
            )
        }
        SearchFieldComponent(
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            value = selectedNames,
            options = options,
            onSelect = {},
            selected = selected,
            onConfirm = {
                onConfirm(it)
                showPopup = false
            },
            showPopup = showPopup,
            onClick = { showPopup = !showPopup },
            placeholder = stringResource(R.string.unplanned_doctors_select_hint),
        )
        selected.forEach { doctor ->
            key(doctor.id) {
                DoctorRow(doctor = doctor, onRemove = { onRemove(doctor.id) }, enabled = enabled)
            }
        }
    }
}

@Composable
private fun DoctorRow(doctor: DoctorUi, onRemove: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(radii.medium)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Slate25)
            .border(DividerDefaults.Thickness, Slate200, shape)
            .padding(horizontal = spacing.spaceSmallPlus, vertical = spacing.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.unplanned_doctor_remove),
            tint = Slate400,
            modifier = Modifier.clickable(enabled = enabled, onClick = onRemove),
        )
        NexoraChip(
            text = doctor.classCategory?.let { stringResource(R.string.unplanned_class_format, it.name) }
                ?: stringResource(R.string.filter_class_title),
            container = if (doctor.classCategory == null) Slate100 else InfoContainer,
            content = if (doctor.classCategory == null) Slate500 else Info,
        )
        NexoraText(
            text = doctor.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Slate800,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun DoctorPickerCardPreview() {
    val yasmin = DoctorUi("d1", "Dr. Yasmin Farouk", BasicModelUi("B", "B"))
    val tarek = DoctorUi("d2", "Dr. Tarek Ezz El-Din")
    MedicalRepTheme {
        DoctorPickerCard(
            options = listOf(yasmin, tarek),
            selected = listOf(yasmin, tarek),
            onConfirm = {},
            onRemove = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
