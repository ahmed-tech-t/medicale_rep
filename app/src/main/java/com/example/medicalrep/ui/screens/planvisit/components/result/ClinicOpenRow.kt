package com.example.medicalrep.ui.screens.planvisit.components.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme

@Composable
internal fun ClinicOpenRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Brand),
        )
        NexoraText(
            text = stringResource(R.string.unplanned_clinic_open),
            style = MaterialTheme.typography.labelMedium,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ClinicOpenRowCheckedPreview() {
    MedicalRepTheme { ClinicOpenRow(checked = true, onCheckedChange = {}) }
}

@Preview(showBackground = true)
@Composable
private fun ClinicOpenRowUncheckedPreview() {
    MedicalRepTheme { ClinicOpenRow(checked = false, onCheckedChange = {}) }
}
