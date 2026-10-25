package com.example.medicalrep.ui.screens.addaccount.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.primitives.NexoraTextField
import com.example.medicalrep.ui.components.widgets.LabelWrapperComponent
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.components.widgets.SelectFieldComponent
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountEvents
import com.example.medicalrep.ui.screens.addaccount.main.AddAccountState
import com.example.medicalrep.ui.screens.planvisit.components.StepCard
import com.example.medicalrep.ui.screens.planvisit.components.StepCardTitle
import com.example.medicalrep.ui.ui.theme.Slate500

/** City and district. */
@Composable
internal fun LocationCard(state: AddAccountState, onEvent: (AddAccountEvents) -> Unit) {
    StepCard {
        StepCardTitle(stringResource(R.string.unplanned_section_location))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)) {
            LabelWrapperComponent(
                title = stringResource(R.string.unplanned_label_city),
                modifier = Modifier.weight(1f),
            ) {
                SearchFieldComponent(
                    value = state.city,
                    options = state.cityOptions,
                    onSelect = { onEvent(AddAccountEvents.SelectCity(it)) },
                    placeholder = stringResource(R.string.enter_city),
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
    StepCard {
        StepCardTitle(stringResource(R.string.unplanned_label_entity_type))
        SearchFieldComponent(
            modifier = Modifier.fillMaxWidth(),
            value = state.entityType,
            options = state.entityTypeOptions,
            onSelect = { onEvent(AddAccountEvents.SelectEntityType(it)) },
            placeholder = stringResource(R.string.select_account),
        )
    }
}

/** Class (select), name (typed) and specialty. */
@Composable
internal fun NameCard(state: AddAccountState, onEvent: (AddAccountEvents) -> Unit) {
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
            title = stringResource(R.string.add_account_name_label),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SelectFieldComponent(
                    value = state.classCategory,
                    options = state.classOptions,
                    onSelect = { onEvent(AddAccountEvents.SelectClass(it)) },
                    placeholder = stringResource(R.string.add_account_class_placeholder),
                    optionLabel = { stringResource(R.string.unplanned_class_format, it) },
                    modifier = Modifier.weight(1f),
                )
                NexoraTextField(
                    state = state.name,
                    placeholder = stringResource(R.string.enter_place_name),
                    modifier = Modifier.weight(2f),
                )
            }
        }
        LabelWrapperComponent(
            title = stringResource(R.string.unplanned_specialty_select_hint),
            modifier = Modifier.fillMaxWidth(),
        ) {
            SearchFieldComponent(
                modifier = Modifier.fillMaxWidth(),
                value = state.specialty,
                options = state.specialtyOptions,
                onSelect = { onEvent(AddAccountEvents.SelectSpecialty(it)) },
                placeholder = stringResource(R.string.enter_specialty),
            )
        }
    }
}
