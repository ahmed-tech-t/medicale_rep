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
    val specialtyPart = if (specialty.isBlank()) stringResource(R.string.em_dash) else specialty
    return if (classCategory.isNotBlank()) {
        stringResource(R.string.specialty_with_class_format, specialtyPart, classCategory)
    } else {
        specialtyPart
    }
}

@Composable
fun VisitUi.resolvedLocationLine(): String {
    val parts = listOf(region, city).filter { it.isNotBlank() }
    return if (parts.isEmpty()) {
        stringResource(R.string.em_dash)
    } else {
        parts.joinToString(stringResource(R.string.location_separator))
    }
}

/** "specialty • account" line shown under a visit's name; blank parts are skipped. */
fun VisitUi.resolvedSpecialtyAccountLine(): String =
    listOf(specialty, account).filter { it.isNotBlank() }.joinToString(" • ")

@Composable
fun VisitUi.resolvedDisplayName(): String =
    when {
        placeName.isNotBlank() -> placeName
        account.isNotBlank() -> account
        else -> stringResource(R.string.em_dash)
    }

@Composable
fun StatMetricUiModel.resolvedTitle(): String =
    if (titleBlank) stringResource(R.string.em_dash) else title

@Composable
fun AttachmentPillUiModel.resolvedLabel(): String =
    if (labelBlank) stringResource(fallbackLabelRes) else label
