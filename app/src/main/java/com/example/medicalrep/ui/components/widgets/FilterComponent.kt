package com.example.medicalrep.ui.components.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
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
import androidx.compose.ui.unit.sp
import com.appcore.core.ui.theme.dim
import com.appcore.core.ui.theme.radii
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.R
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.ui.components.primitives.NexoraButton
import com.example.medicalrep.ui.components.primitives.NexoraButtonVariant
import com.example.medicalrep.ui.components.primitives.NexoraText
import com.example.medicalrep.ui.ui.theme.Brand
import com.example.medicalrep.ui.ui.theme.Ink
import com.example.medicalrep.ui.ui.theme.MedicalRepTheme
import com.example.medicalrep.ui.ui.theme.Slate200
import com.example.medicalrep.ui.ui.theme.Slate300
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FilterState(
    val cityState: TextFieldState = TextFieldState(),
    val regionState: TextFieldState = TextFieldState(),
    val accountState: TextFieldState = TextFieldState(),
    val specialtyState: TextFieldState = TextFieldState(),

    val selectedCity: BasicModel? = null,
    val selectedRegion: BasicModel? = null,
    val selectedAccount: BasicModel? = null,
    val selectedClass: BasicModel? = null,
    val selectedSpecialty: BasicModel? = null,
    val selectedStartDate: String? = null,
    val selectedEndDate: String? = null,

    val cites: List<BasicModel> = emptyList(),
    val regions: List<BasicModel> = emptyList(),
    val accounts: List<BasicModel> = emptyList(),
    val classes: List<BasicModel> = listOf(
        BasicModel("classA", "A"),
        BasicModel("classB", "B"),
        BasicModel("classC", "C")
    ),
    val specialties: List<BasicModel> = emptyList(),
)

sealed interface ClassType {
    data object ClassA : ClassType
    data object ClassB : ClassType
    data object ClassC : ClassType

    companion object {
        val list = lazy {
            listOf(
                ClassA, ClassB, ClassC
            )
        }
    }
}

sealed interface FilterEvents {
    data class OnCitySelect(val value: BasicModel) : FilterEvents
    data class OnRegionSelect(val value: BasicModel) : FilterEvents
    data class OnAccountSelect(val value: BasicModel) : FilterEvents
    data class OnClassSelect(val value: BasicModel?) : FilterEvents
    data class OnSpecialtySelect(val value: BasicModel) : FilterEvents

    data class OnStartDateSelect(val value: String) : FilterEvents
    data class OnEndDateSelect(val value: String) : FilterEvents
    data object OnReset : FilterEvents
}

fun FilterState.reduce(event: FilterEvents): FilterState {
    return when (event) {
        is FilterEvents.OnAccountSelect -> copy(selectedAccount = event.value)
        is FilterEvents.OnCitySelect -> copy(selectedCity = event.value)
        is FilterEvents.OnClassSelect -> copy(selectedClass = event.value)
        is FilterEvents.OnEndDateSelect -> copy(selectedEndDate = event.value)
        is FilterEvents.OnRegionSelect -> copy(selectedRegion = event.value)
        is FilterEvents.OnSpecialtySelect -> copy(selectedSpecialty = event.value)
        is FilterEvents.OnStartDateSelect -> copy(selectedStartDate = event.value)
        is FilterEvents.OnReset -> clearAll()
    }
}

fun FilterState.clearAll(): FilterState {
    cityState.clearText()
    regionState.clearText()
    accountState.clearText()
    specialtyState.clearText()

    return this.copy(
        selectedCity = null,
        selectedRegion = null,
        selectedAccount = null,
        selectedClass = null,
        selectedSpecialty = null,
        selectedStartDate = null,
        selectedEndDate = null
    )
}

private val BasicModel.asSearchItem: SearchableItem
    get() = TextItem(name)

@Composable
fun FilterContentComponent(
    state: FilterState,
    onEvent: (FilterEvents) -> Unit,
    onApplyFilter: () -> Unit,
    modifier: Modifier = Modifier,
    onReset: () -> Unit = { onEvent(FilterEvents.OnReset) },
) {
    var showCityPopup by remember { mutableStateOf(false) }
    var showRegionPopup by remember { mutableStateOf(false) }
    var showSpecialtyPopup by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var startDateMillis by remember(state.selectedStartDate) {
        val parsed =
            state.selectedStartDate?.let { runCatching { dateFormat.parse(it)?.time }.getOrNull() }
        mutableStateOf(parsed ?: System.currentTimeMillis())
    }
    var endDateMillis by remember(state.selectedEndDate) {
        val parsed =
            state.selectedEndDate?.let { runCatching { dateFormat.parse(it)?.time }.getOrNull() }
        mutableStateOf(parsed ?: System.currentTimeMillis())
    }

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
                    // "الكل" Pill
                    val isAllSelected = state.selectedClass == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(radii.pill))
                            .border(
                                width = DividerDefaults.Thickness,
                                color = if (isAllSelected) Brand else Slate300,
                                shape = RoundedCornerShape(radii.pill)
                            )
                            .clickable { onEvent(FilterEvents.OnClassSelect(null)) }
                            .padding(
                                horizontal = spacing.spaceMedium,
                                vertical = spacing.spaceSmall
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        NexoraText(
                            text = stringResource(R.string.filter_all),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isAllSelected) Brand else Ink,
                        )
                    }

                    // A, B, C circular chips
                    state.classes.forEach { classModel ->
                        val isSelected =
                            state.selectedClass?.id == classModel.id || state.selectedClass?.name == classModel.name
                        Box(
                            modifier = Modifier
                                .size(dim.thumbnailSize)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(
                                        alpha = .15f
                                    ) else Color.Unspecified
                                )
                                .border(
                                    width = if (isSelected) DividerDefaults.Thickness * 2 else DividerDefaults.Thickness,
                                    color = if (isSelected) Brand else Slate300,
                                    shape = CircleShape
                                )
                                .clickable { onEvent(FilterEvents.OnClassSelect(classModel)) },
                            contentAlignment = Alignment.Center,
                        ) {
                            NexoraText(
                                text = classModel.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Brand else Ink,
                            )
                        }
                    }
                }
            }

            // Section 2: المنطقة (المدينة + الحي)
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
                            options = state.cites.map { it.asSearchItem },
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
                            options = state.regions.map { it.asSearchItem },
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

            // Section 3: التخصص
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
                    options = state.specialties.map { it.asSearchItem },
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

            // Section 4: الفترة الزمنية (التاريخ من + التاريخ إلى)
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
                            dateMillis = startDateMillis,
                            onDateChange = { millis ->
                                startDateMillis = millis
                                onEvent(FilterEvents.OnStartDateSelect(dateFormat.format(Date(millis))))
                            },
                        )
                    }

                    LabelWrapperComponent(
                        title = stringResource(R.string.filter_date_to),
                        modifier = Modifier.weight(1f),
                    ) {
                        DateField(
                            dateMillis = endDateMillis,
                            onDateChange = { millis ->
                                endDateMillis = millis
                                onEvent(FilterEvents.OnEndDateSelect(dateFormat.format(Date(millis))))
                            },
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

@Preview(showBackground = true, locale = "ar")
@Composable
fun FilterContentComponentPreview() {
    MedicalRepTheme {
        var state by remember {
            mutableStateOf(
                FilterState(
                    selectedClass = BasicModel("classA", "A"),
                    selectedCity = BasicModel("1", "Cairo"),
                    selectedRegion = null,
                    selectedSpecialty = null,
                    selectedStartDate = "29/10/2026",
                    selectedEndDate = "04/11/2026",
                    cites = listOf(
                        BasicModel("1", "Cairo"),
                        BasicModel("2", "Giza"),
                        BasicModel("3", "Alexandria")
                    ),
                    regions = listOf(BasicModel("1", "Maadi"), BasicModel("2", "Nasr City")),
                    specialties = listOf(
                        BasicModel("1", "باطنة"),
                        BasicModel("2", "أطفال"),
                        BasicModel("3", "عظام")
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
        )
    }
}
