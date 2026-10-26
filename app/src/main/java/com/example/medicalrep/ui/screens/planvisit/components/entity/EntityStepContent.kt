package com.example.medicalrep.ui.screens.planvisit.components.entity

import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.screens.planvisit.components.StepCardTitle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.LabelWrapperComponent
import com.example.medicalrep.ui.components.widgets.NexoraChip
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.components.widgets.toSearchItems
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitEvents
import com.example.medicalrep.ui.screens.planvisit.main.PlanVisitState
import com.example.medicalrep.ui.screens.planvisit.utils.PlanVisitFakes
import com.example.medicalrep.ui.screens.planvisit.utils.accountAddressLine
import com.example.medicalrep.ui.ui.theme.Info
import com.example.medicalrep.ui.ui.theme.InfoContainer
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate100
import com.example.medicalrep.ui.ui.theme.Slate500

/** Step 1 (Figma 33:5735): date/time, location, entity type, place + specialty, shift, visit type. */
@Composable
fun EntityStepContent(
    state: PlanVisitState,
    onEvent: (PlanVisitEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visit = state.visit
    var showCityPopup by remember { mutableStateOf(false) }
    var showRegionPopup by remember { mutableStateOf(false) }
    var showAccountPopup by remember { mutableStateOf(false) }
    var showplaceNamePopup by remember { mutableStateOf(false) }
    var showSpecialtyPopup by remember { mutableStateOf(false) }
    var showSupervisorNamePopup by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
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
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(radii.medium)
                        )
                        .background(Slate100, RoundedCornerShape(radii.medium))
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
                        enabled = visit.type == VisitType.Unplanned,
                        options = state.cityOptions.toSearchItems(),
                        onSelect = {
                            onEvent(PlanVisitEvents.SelectCity(it.name))
                            showCityPopup = false
                        },
                        placeholder = stringResource(R.string.enter_city),
                        value = visit.city,
                        showPopup = showCityPopup,
                        onClick = { showCityPopup = !showCityPopup }
                    )
                }

                LabelWrapperComponent(
                    title = stringResource(R.string.unplanned_label_district),
                    modifier = Modifier.weight(1f),
                ) {
                    SearchFieldComponent(
                        enabled = visit.type == VisitType.Unplanned,
                        value = visit.region,
                        options = state.districtOptions.toSearchItems(),
                        onSelect = {
                            onEvent(PlanVisitEvents.SelectDistrict(it.name))
                            showRegionPopup = false
                        },
                        placeholder = stringResource(R.string.enter_district),
                        showPopup = showRegionPopup,
                        onClick = { showRegionPopup = !showRegionPopup }
                    )
                }

            }
        }

        StepCard {
            StepCardTitle(stringResource(R.string.unplanned_label_entity_type))
            SearchFieldComponent(
                enabled = visit.type == VisitType.Unplanned,
                modifier = Modifier.fillMaxWidth(),
                value = visit.account,
                options = state.entityTypeOptions.toSearchItems(),
                onSelect = {
                    onEvent(PlanVisitEvents.SelectEntityType(it.name))
                    showAccountPopup = false
                },
                placeholder = stringResource(R.string.select_account),
                showPopup = showAccountPopup,
                onClick = { showAccountPopup = !showAccountPopup }
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
                    enabled = visit.type == VisitType.Unplanned,
                    modifier = Modifier.fillMaxWidth(),
                    value = visit.placeName,
                    options = state.placeOptions.toSearchItems(),
                    onSelect = {
                        onEvent(PlanVisitEvents.SelectPlace(it.name))
                        showplaceNamePopup = false
                    },
                    showPopup = showplaceNamePopup,
                    onClick = { showplaceNamePopup = !showplaceNamePopup },
                    badge = if (visit.classCategory.isBlank()) null else {
                        {
                            NexoraChip(
                                text = stringResource(
                                    R.string.unplanned_class_format,
                                    visit.classCategory
                                ),
                                container = InfoContainer,
                                content = Info,
                            )
                        }
                    },
                    placeholder = stringResource(R.string.enter_place_name),
                )
            }
            if (visit.type == VisitType.Planned && visit.placeName.isNotBlank()) {
                AccountLocationRow(
                    addressLine = visit.accountAddressLine(),
                    onViewMap = { onEvent(PlanVisitEvents.ViewAccountLocationClick) },
                )
            }

            LabelWrapperComponent(
                title = stringResource(R.string.unplanned_specialty_select_hint),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SearchFieldComponent(
                    enabled = visit.type == VisitType.Unplanned,
                    modifier = Modifier.fillMaxWidth(),
                    value = visit.specialty,
                    options = state.specialtyOptions.toSearchItems(),
                    onSelect = {
                        onEvent(PlanVisitEvents.SelectSpecialty(it.name))
                        showSpecialtyPopup = false
                    },
                    placeholder = stringResource(R.string.enter_specialty),
                    showPopup = showSpecialtyPopup,
                    onClick = { showSpecialtyPopup = !showSpecialtyPopup }
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
                    onEvent(PlanVisitEvents.SelectShift(if (index == 0) Shift.Am else Shift.Pm))
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
                onSelectIndex = { index -> onEvent(PlanVisitEvents.SelectDoubleVisit(isDouble = index == 1)) },
            )
            if (visit.isDoubleVisit) {
                LabelWrapperComponent(
                    title = stringResource(R.string.unplanned_label_supervisor),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SearchFieldComponent(
                        modifier = Modifier.fillMaxWidth(),
                        value = visit.supervisorName,
                        options = state.supervisorOptions.toSearchItems(),
                        onSelect = {
                            onEvent(PlanVisitEvents.SelectSupervisor(it.name))
                            showSupervisorNamePopup = false
                        },
                        placeholder = stringResource(R.string.select_supervisor),
                        showPopup = showSupervisorNamePopup,
                        onClick = { showSupervisorNamePopup = !showSupervisorNamePopup }
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
            state = PlanVisitFakes.state(),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun EntityStepContentSingleEnglishPreview() {
    MedicalRepTheme {
        val state = PlanVisitFakes.state()
        EntityStepContent(
            state = state.copy(visit = state.visit.copy(isDoubleVisit = false, classCategory = "")),
            onEvent = {},
            modifier = Modifier.padding(spacing.spaceMedium),
        )
    }
}
