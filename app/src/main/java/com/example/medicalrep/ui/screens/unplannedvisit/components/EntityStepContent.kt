package com.example.medicalrep.ui.screens.unplannedvisit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.LabelWrapperComponent
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitEvents
import com.example.medicalrep.ui.screens.unplannedvisit.main.UnplannedVisitState
import com.example.medicalrep.ui.screens.unplannedvisit.utils.UnplannedVisitFakes
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500

/** Step 1 (Figma 33:5735): date/time, location, entity type, place + specialty, shift, visit type. */
@Composable
fun EntityStepContent(
    state: UnplannedVisitState,
    onEvent: (UnplannedVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visit = state.visit
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
    ) {
        StepCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall),
                ) {
                    StepCardTitle(stringResource(R.string.unplanned_datetime_label))
                    NexoraText(
                        text = stringResource(R.string.unplanned_datetime_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500,
                    )
                }
                NexoraText(
                    text = stringResource(
                        R.string.unplanned_datetime_format,
                        visit.date,
                        visit.time
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier
                        .background(Slate100, RoundedCornerShape(radii.pill))
                        .padding(
                            horizontal = spacing.spaceSmallPlus,
                            vertical = spacing.spaceSmall
                        ),
                )
            }
        }

        StepCard {
            StepCardTitle(stringResource(R.string.unplanned_section_location))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
                LabelWrapperComponent(
                    title = stringResource(R.string.unplanned_label_city),
                    modifier = Modifier.weight(1f),
                ) {
                    SearchFieldComponent(
                        options = state.cityOptions,
                        onSelect = { onEvent(UnplannedVisitEvents.SelectCity(it)) },
                        placeholder = stringResource(R.string.enter_city),
                        value = visit.city
                    )
                }

                LabelWrapperComponent(
                    title = stringResource(R.string.unplanned_label_district),
                    modifier = Modifier.weight(1f),
                ) {
                    SearchFieldComponent(
                        value = visit.region,
                        options = state.districtOptions,
                        onSelect = { onEvent(UnplannedVisitEvents.SelectDistrict(it)) },
                        placeholder = stringResource(R.string.enter_district),
                    )
                }

            }
        }

        StepCard {
            StepCardTitle(stringResource(R.string.unplanned_label_entity_type))
            SearchFieldComponent(
                modifier= Modifier.fillMaxWidth(),
                value = visit.account,
                options = state.entityTypeOptions,
                onSelect = { onEvent(UnplannedVisitEvents.SelectEntityType(it)) },
                placeholder = stringResource(R.string.select_account),
            )
        }

        StepCard {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)) {
                StepCardTitle(stringResource(R.string.unplanned_section_place_specialty))
                NexoraText(
                    text = stringResource(R.string.unplanned_place_list_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                )
            }
            LabelWrapperComponent(
                title = stringResource(R.string.unplanned_place_select_hint),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SearchFieldComponent(
                    modifier = Modifier.fillMaxWidth(),
                    value = visit.placeName,
                    options = state.placeOptions,
                    onSelect = { onEvent(UnplannedVisitEvents.SelectPlace(it)) },
                    badge = if (visit.classCategory.isBlank()) null else {
                        {
                            ClassBadge(
                                text = stringResource(
                                    R.string.unplanned_class_format,
                                    visit.classCategory
                                )
                            )
                        }},
                    placeholder = stringResource(R.string.enter_place_name),
                )
            }

            LabelWrapperComponent(
                title = stringResource(R.string.unplanned_specialty_select_hint),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SearchFieldComponent(
                    modifier = Modifier.fillMaxWidth(),
                    value = visit.specialty,
                    options = state.specialtyOptions,
                    onSelect = { onEvent(UnplannedVisitEvents.SelectSpecialty(it)) },
                    placeholder = stringResource(R.string.enter_specialty),
                )
            }
        }

        StepCard {
            SegmentedChoiceRow(
                label = stringResource(R.string.unplanned_label_shift),
                options = listOf(
                    SegmentOption(
                        stringResource(R.string.shift_am),
                        selected = visit.shift == Shift.Am
                    ),
                    SegmentOption(
                        stringResource(R.string.shift_pm),
                        selected = visit.shift == Shift.Pm
                    ),
                ),
                onSelectIndex = { index ->
                    onEvent(UnplannedVisitEvents.SelectShift(if (index == 0) Shift.Am else Shift.Pm))
                },
            )
        }

        StepCard {
            SegmentedChoiceRow(
                label = stringResource(R.string.visit_type_label),
                options = listOf(
                    SegmentOption(
                        stringResource(R.string.visit_type_single),
                        selected = !visit.isDoubleVisit
                    ),
                    SegmentOption(
                        stringResource(R.string.visit_type_double),
                        selected = visit.isDoubleVisit
                    ),
                ),
                onSelectIndex = { index -> onEvent(UnplannedVisitEvents.SelectDoubleVisit(isDouble = index == 1)) },
            )
            if (visit.isDoubleVisit) {
                LabelWrapperComponent(
                    title = stringResource(R.string.unplanned_label_supervisor),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SearchFieldComponent(
                        modifier = Modifier.fillMaxWidth(),
                        value = visit.supervisorName,
                        options = state.supervisorOptions,
                        onSelect = { onEvent(UnplannedVisitEvents.SelectSupervisor(it)) },
                        placeholder = stringResource(R.string.select_supervisor),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar", heightDp = 1000)
@Composable
private fun EntityStepContentPreview() {
    MedicalRepTheme {
        EntityStepContent(
            state = UnplannedVisitFakes.state(),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun EntityStepContentSingleEnglishPreview() {
    MedicalRepTheme {
        val state = UnplannedVisitFakes.state()
        EntityStepContent(
            state = state.copy(visit = state.visit.copy(isDoubleVisit = false, classCategory = "")),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
