package com.example.medicalrep.ui.screens.addaccount.components

import androidx.annotation.StringRes
import com.example.medicalrep.domain.models.WorkingTime
import com.example.medicalrep.ui.screens.planvisit.components.entity.SegmentOption
import com.example.medicalrep.ui.screens.planvisit.components.entity.SegmentedChoiceRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.components.widgets.LabelWrapperComponent
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.components.widgets.toSearchItems
import com.example.medicalrep.ui.components.widgets.SelectFieldComponent
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountEvents
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountState
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.screens.planvisit.components.StepCardTitle

/** City and district. */
@Composable
internal fun LocationCard(state: AddAccountState, onEvent: (AddAccountEvents) -> Unit) {
    var showCityPopup by remember { mutableStateOf(false) }

    StepCard {
        StepCardTitle(stringResource(R.string.unplanned_section_location))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            LabelWrapperComponent(
                title = stringResource(R.string.unplanned_label_city),
                modifier = Modifier.weight(1f),
            ) {
                SearchFieldComponent(
                    value = state.city,
                    options = state.cityOptions.toSearchItems(),
                    onSelect = {
                        onEvent(AddAccountEvents.SelectCity(it.name))
                        showCityPopup = false
                    },
                    placeholder = stringResource(R.string.enter_city),
                    onClick = { showCityPopup = !showCityPopup },
                    showPopup = showCityPopup
                )
            }
            LabelWrapperComponent(
                title = stringResource(R.string.unplanned_label_district),
                modifier = Modifier.weight(1f),
            ) {
                SelectFieldComponent(
                    value = state.district,
                    options = state.districtOptions,
                    onSelect = { onEvent(AddAccountEvents.SelectDistrict(it)) },
                    placeholder = stringResource(R.string.enter_district),
                )
            }
        }
    }
}

@Composable
internal fun EntityTypeCard(state: AddAccountState, onEvent: (AddAccountEvents) -> Unit) {
    var showEntityTypePopup by remember { mutableStateOf(false) }

    StepCard {
        StepCardTitle(stringResource(R.string.unplanned_label_entity_type))
        SearchFieldComponent(
            modifier = Modifier.fillMaxWidth(),
            value = state.entityType,
            options = state.entityTypeOptions.toSearchItems(),
            onSelect = {
                onEvent(AddAccountEvents.SelectEntityType(it.name))
                showEntityTypePopup = false
            },
            placeholder = stringResource(R.string.select_account),
            showPopup = showEntityTypePopup,
            onClick = { showEntityTypePopup = !showEntityTypePopup }

        )
    }
}

/** The periods the account is open: only one, or both (then a visit can pick either). */
@Composable
internal fun WorkingTimeCard(state: AddAccountState, onEvent: (AddAccountEvents) -> Unit) {
    StepCard {
        SegmentedChoiceRow(
            label = stringResource(R.string.add_account_working_time),
            options = WorkingTime.entries.map { time ->
                SegmentOption(stringResource(time.labelRes()), selected = state.workingTime == time)
            },
            onSelectIndex = { index -> onEvent(AddAccountEvents.SelectWorkingTime(WorkingTime.entries[index])) },
        )
    }
}

@StringRes
private fun WorkingTime.labelRes() = when (this) {
    WorkingTime.Am -> R.string.shift_am
    WorkingTime.Pm -> R.string.shift_pm
    WorkingTime.Both -> R.string.working_time_both
}

/** The place name, typed. */
@Composable
internal fun NameCard(state: AddAccountState, onEvent: (AddAccountEvents) -> Unit) {
    StepCard {
        LabelWrapperComponent(
            title = stringResource(R.string.add_account_name_label),
            modifier = Modifier.fillMaxWidth(),
        ) {
            NexoraTextField(
                state = state.name,
                placeholder = stringResource(R.string.enter_place_name),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
