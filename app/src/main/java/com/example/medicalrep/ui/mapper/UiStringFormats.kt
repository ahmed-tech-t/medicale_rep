package com.example.medicalrep.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.medicalrep.R
import com.example.medicalrep.ui.models.AttachmentPillUiModel
import com.example.medicalrep.ui.models.StatMetricUiModel
import com.example.medicalrep.ui.models.VisitUi

@Composable
fun VisitUi.resolvedPlaceName(): String =
    if (placeName.isBlank()) stringResource(R.string.em_dash) else placeName

@Composable
fun VisitUi.resolvedSpecialtyLine(): String {
    val specialtyPart = specialty?.name ?: stringResource(R.string.em_dash)
    return if (classCategory != null) {
        stringResource(R.string.specialty_with_class_format, specialtyPart, classCategory.name)
    } else {
        specialtyPart
    }
}

@Composable
fun VisitUi.resolvedLocationLine(): String {
    val parts = listOfNotNull(region?.name, city?.name)
    return if (parts.isEmpty()) {
        stringResource(R.string.em_dash)
    } else {
        parts.joinToString(stringResource(R.string.location_separator))
    }
}

/** "specialty • account" line shown under a visit's name; blank parts are skipped. */
fun VisitUi.resolvedSpecialtyAccountLine(): String =
    listOfNotNull(specialty?.name, account?.name).joinToString(" • ")

@Composable
fun VisitUi.resolvedDisplayName(): String =
    when {
        placeName.isNotBlank() -> placeName
        account != null -> account.name
        else -> stringResource(R.string.em_dash)
    }

@Composable
fun StatMetricUiModel.resolvedTitle(): String =
    if (titleBlank) stringResource(R.string.em_dash) else title

@Composable
fun AttachmentPillUiModel.resolvedLabel(): String =
    if (labelBlank) stringResource(fallbackLabelRes) else label
