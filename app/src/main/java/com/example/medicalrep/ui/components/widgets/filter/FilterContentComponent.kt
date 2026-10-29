package com.example.medicalrep.ui.components.widgets.filter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.components.widgets.DateField
import com.example.medicalrep.ui.components.widgets.LabelWrapperComponent
import com.example.medicalrep.ui.components.widgets.SearchFieldComponent
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate300


@Composable
fun FilterContentComponent(
    state: FilterState,
    onEvent: (FilterEvents) -> Unit,
    showAreaPicker: Boolean,
    showDateFilter: Boolean,
    showClassFilter: Boolean,
    showSpecialtyFilter: Boolean,
    showAccountType: Boolean,
    showSyncStatus: Boolean,
    showVisitType: Boolean,

    onApplyFilter: () -> Unit,
    modifier: Modifier = Modifier,
    onReset: () -> Unit = { onEvent(FilterEvents.OnReset) },
) {
    var showCityPopup by remember { mutableStateOf(false) }
    var showRegionPopup by remember { mutableStateOf(false) }
    var showSpecialtyPopup by remember { mutableStateOf(false) }

    val startPickerState = state.rememberStartDatePickerState()
    val endPickerState = state.rememberEndDatePickerState()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = radii.sheet,
            topEnd = radii.sheet,
            bottomStart = radii.extraLarge,
            bottomEnd = radii.extraLarge
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.spaceExtraSmall),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceMediumPlus, vertical = spacing.spaceSmallPlus),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
        ) {
            // Drag Handle Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.spaceExtraSmall),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = dim.thumbnailSize, height = spacing.spaceExtraSmall)
                        .clip(RoundedCornerShape(radii.extraSmall))
                        .background(Slate300.copy(alpha = 0.8f))
                )
            }

            // Title: تصفية الزيارات المطلوبة
            NexoraText(
                text = stringResource(R.string.filter_visits_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = Ink,
                modifier = Modifier.fillMaxWidth(),
            )

            // Section 1: التصنيف (الكل، A، B، C)
            if (showClassFilter) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                ) {
                    NexoraText(
                        text = stringResource(R.string.filter_class_title),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Ink,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val allChipPadding = PaddingValues(
                            horizontal = spacing.spaceMedium,
                            vertical = spacing.spaceSmall
                        )
                        ClassFilter.entries.forEach { filter ->
                            FilterShip(
                                isSelected = state.selectedClass == filter,
                                text = filter.label,
                                modifier = if (filter == ClassFilter.All) Modifier else Modifier.size(
                                    spacing.spaceLargePlus
                                ),
                                shape = if (filter == ClassFilter.All) RoundedCornerShape(radii.pill) else CircleShape,
                                contentPadding = if (filter == ClassFilter.All) allChipPadding else PaddingValues(),
                                onClick = { onEvent(FilterEvents.OnClassSelect(filter)) })
                        }
                    }
                }
            }
            // Section: نوع الحساب
            if (showAccountType) {
                FilterChipsSection(title = stringResource(R.string.visit_details_account_type)) {
                    AccountTypeFilter.entries.forEach { filter ->
                        FilterShip(
                            isSelected = state.accountTypeFilter == filter,
                            text = stringResource(filter.valueRes),
                            onClick = { onEvent(FilterEvents.OnAccountTypeSelect(filter)) },
                        )
                    }
                }
            }

            // Section 2: المنطقة (المدينة + الحي)
            if (showAreaPicker) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                ) {
                    NexoraText(
                        text = stringResource(R.string.filter_region_title),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Ink,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
                    ) {
                        LabelWrapperComponent(
                            title = stringResource(R.string.unplanned_label_city),
                            modifier = Modifier.weight(1f),
                        ) {
                            SearchFieldComponent(
                                value = state.selectedCity?.name.orEmpty(),
                                options = state.cites,
                                onSelect = { item ->
                                    state.cites.find { it.name == item.name }?.let {
                                        onEvent(FilterEvents.OnCitySelect(it))
                                    }
                                    showCityPopup = false
                                },
                                showPopup = showCityPopup,
                                placeholder = stringResource(R.string.enter_city),
                                onClick = {
                                    showCityPopup = !showCityPopup
                                    showRegionPopup = false
                                    showSpecialtyPopup = false
                                },
                            )
                        }

                        LabelWrapperComponent(
                            title = stringResource(R.string.unplanned_label_district),
                            modifier = Modifier.weight(1f),
                        ) {
                            SearchFieldComponent(
                                value = state.selectedRegion?.name.orEmpty(),
                                options = state.regions,
                                onSelect = { item ->
                                    state.regions.find { it.name == item.name }?.let {
                                        onEvent(FilterEvents.OnRegionSelect(it))
                                    }
                                    showRegionPopup = false
                                },
                                showPopup = showRegionPopup,
                                placeholder = stringResource(R.string.enter_district),
                                onClick = {
                                    showRegionPopup = !showRegionPopup
                                    showCityPopup = false
                                    showSpecialtyPopup = false
                                },
                            )
                        }
                    }
                }
            }

            // Section 3: التخصص
            if (showSpecialtyFilter) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                ) {
                    NexoraText(
                        text = stringResource(R.string.unplanned_label_specialty),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Ink,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    SearchFieldComponent(
                        value = state.selectedSpecialty?.name.orEmpty(),
                        options = state.specialties,
                        onSelect = { item ->
                            state.specialties.find { it.name == item.name }?.let {
                                onEvent(FilterEvents.OnSpecialtySelect(it))
                            }
                            showSpecialtyPopup = false
                        },
                        showPopup = showSpecialtyPopup,
                        placeholder = stringResource(R.string.unplanned_specialty_select_hint),
                        onClick = {
                            showSpecialtyPopup = !showSpecialtyPopup
                            showCityPopup = false
                            showRegionPopup = false
                        },
                    )
                }

            }

            // Section: حالة المزامنة
            if (showSyncStatus) {
                FilterChipsSection(title = stringResource(R.string.filter_sync_status_title)) {
                    SyncFilter.entries.forEach { filter ->
                        FilterShip(
                            isSelected = state.syncFilter == filter,
                            text = stringResource(filter.valueRes),
                            onClick = { onEvent(FilterEvents.OnSyncFilterSelect(filter)) },
                        )
                    }
                }
            }

            // Section: نوع الزيارة
            if (showVisitType) {
                FilterChipsSection(title = stringResource(R.string.visit_type_label)) {
                    VisitTypeFilter.entries.forEach { filter ->
                        FilterShip(
                            isSelected = state.visitTypeFilter == filter,
                            text = stringResource(filter.valueRes),
                            onClick = { onEvent(FilterEvents.OnVisitTypeSelect(filter)) },
                        )
                    }
                }
            }

            // Section 4: الفترة الزمنية (التاريخ من + التاريخ إلى)
            if (showDateFilter) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                ) {
                    NexoraText(
                        text = stringResource(R.string.filter_period),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Ink,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
                    ) {
                        LabelWrapperComponent(
                            title = stringResource(R.string.filter_date_from),
                            modifier = Modifier.weight(1f),
                        ) {
                            DateField(
                                state = startPickerState,
                                showPicker = state.overLay == FilterOverLay.StartDateDialog,
                                placeholder = state.selectedStartDate,
                                onShowPicker = {
                                    onEvent(FilterEvents.ToggleFilterOverlay(FilterOverLay.StartDateDialog))
                                },
                                onDateChange = { millis ->
                                    onEvent(FilterEvents.OnStartDateSelect(millis))
                                },
                                onDismiss = {
                                    onEvent(FilterEvents.ToggleFilterOverlay(FilterOverLay.None))
                                }
                            )
                        }

                        LabelWrapperComponent(
                            title = stringResource(R.string.filter_date_to),
                            modifier = Modifier.weight(1f),
                        ) {
                            DateField(
                                state = endPickerState,
                                showPicker = state.overLay == FilterOverLay.EndDateDialog,
                                placeholder = state.selectedEndDate,
                                onShowPicker = {
                                    onEvent(FilterEvents.ToggleFilterOverlay(FilterOverLay.EndDateDialog))
                                },
                                onDateChange = { millis ->
                                    onEvent(FilterEvents.OnEndDateSelect(millis))
                                },
                                onDismiss = {
                                    onEvent(FilterEvents.ToggleFilterOverlay(FilterOverLay.None))
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))

                // Section 5: أزرار التحكم (إعادة ضبط + تطبيق الفلتر)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmallPlus),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NexoraButton(
                        text = stringResource(R.string.filter_reset),
                        onClick = onReset,
                        modifier = Modifier.weight(1f),
                        variant = NexoraButtonVariant.Secondary,
                        outLineColor = Slate300,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                        ),
                        border = BorderStroke(DividerDefaults.Thickness, Slate200),
                    )

                    NexoraButton(
                        text = stringResource(R.string.filter_apply),
                        onClick = onApplyFilter,
                        modifier = Modifier.weight(2f),
                        variant = NexoraButtonVariant.Primary,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                    )
                }
            }
        }
    }
}

/** A bold title above a wrapping row of chips. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterChipsSection(title: String, chips: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
    ) {
        NexoraText(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = Ink,
            modifier = Modifier.fillMaxWidth(),
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
        ) { chips() }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun FilterContentComponentPreview() {
    MedicalRepTheme {
        var state by remember {
            mutableStateOf(
                FilterState(
                    selectedClass = ClassFilter.A,
                    selectedCity = BasicModelUi("1", "Cairo"),
                    selectedRegion = null,
                    selectedSpecialty = null,
                    selectedStartDate = "29/10/2026",
                    selectedEndDate = "04/11/2026",
                    cites = listOf(
                        BasicModelUi("1", "Cairo"),
                        BasicModelUi("2", "Giza"),
                        BasicModelUi("3", "Alexandria")
                    ),
                    regions = listOf(BasicModelUi("1", "Maadi"), BasicModelUi("2", "Nasr City")),
                    specialties = listOf(
                        BasicModelUi("1", "باطنة"),
                        BasicModelUi("2", "أطفال"),
                        BasicModelUi("3", "عظام")
                    ),
                )
            )
        }

        FilterContentComponent(
            state = state,
            onEvent = { event -> state = state.reduce(event) },
            onApplyFilter = {},
            onReset = { state = state.clearAll() },
            modifier = Modifier.padding(spacing.spaceMedium),
            showAreaPicker = true,
            showDateFilter = true,
            showClassFilter = true,
            showSpecialtyFilter = true,
            showAccountType = true,
            showSyncStatus = true,
            showVisitType = true,
        )
    }
}
