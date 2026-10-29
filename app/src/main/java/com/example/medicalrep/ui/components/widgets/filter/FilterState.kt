package com.example.medicalrep.ui.components.widgets.filter

import androidx.annotation.StringRes
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.medicalrep.R
import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.domain.utils.Filter_Date_Formate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val dateFormat = SimpleDateFormat(Filter_Date_Formate, Locale.ENGLISH)

data class FilterState(
    val cityState: TextFieldState = TextFieldState(),
    val regionState: TextFieldState = TextFieldState(),
    val accountState: TextFieldState = TextFieldState(),
    val specialtyState: TextFieldState = TextFieldState(),

    val selectedCity: BasicModelUi? = null,
    val selectedRegion: BasicModelUi? = null,
    val selectedAccount: BasicModelUi? = null,
    val selectedClass: ClassFilter = ClassFilter.All,
    val selectedSpecialty: BasicModelUi? = null,
    val accountTypeFilter: AccountTypeFilter = AccountTypeFilter.All,
    val syncFilter: SyncFilter = SyncFilter.All,
    val visitTypeFilter: VisitTypeFilter = VisitTypeFilter.All,

// Allowed period boundaries (set by ViewModel)
    val minAllowedMillis: Long = 0L,
    val maxAllowedMillis: Long = System.currentTimeMillis(),

    // Currently selected dates as timestamps
    val startDateMillis: Long = System.currentTimeMillis(),
    val endDateMillis: Long = System.currentTimeMillis(),

    // Formatted strings ready for UI text display or API calls
    val selectedStartDate: String = "",
    val selectedEndDate: String = "",

    val overLay: FilterOverLay = FilterOverLay.None,

    val cites: List<BasicModelUi> = emptyList(),
    val regions: List<BasicModelUi> = emptyList(),
    val accounts: List<BasicModelUi> = emptyList(),
    val specialties: List<BasicModelUi> = emptyList(),
)

sealed interface FilterOverLay {
    data object None : FilterOverLay
    data object StartDateDialog : FilterOverLay
    data object EndDateDialog : FilterOverLay
}



sealed interface FilterEvents {
    data class OnCitySelect(val value: BasicModelUi) : FilterEvents
    data class OnRegionSelect(val value: BasicModelUi) : FilterEvents
    data class OnAccountSelect(val value: BasicModelUi) : FilterEvents
    data class OnClassSelect(val value: ClassFilter) : FilterEvents
    data class OnSpecialtySelect(val value: BasicModelUi) : FilterEvents
    data class OnAccountTypeSelect(val value: AccountTypeFilter) : FilterEvents
    data class OnSyncFilterSelect(val value: SyncFilter) : FilterEvents
    data class OnVisitTypeSelect(val value: VisitTypeFilter) : FilterEvents
    data class OnStartDateSelect(val millis: Long?) : FilterEvents
    data class OnEndDateSelect(val millis: Long?) : FilterEvents
    data class ToggleFilterOverlay(val overlay: FilterOverLay) : FilterEvents
    data object OnReset : FilterEvents
}

fun FilterState.reduce(event: FilterEvents): FilterState {
    return when (event) {
        is FilterEvents.OnAccountSelect -> copy(selectedAccount = event.value)
        is FilterEvents.OnCitySelect -> copy(selectedCity = event.value)
        is FilterEvents.OnClassSelect -> copy(selectedClass = event.value)
        is FilterEvents.OnRegionSelect -> copy(selectedRegion = event.value)
        is FilterEvents.OnSpecialtySelect -> copy(selectedSpecialty = event.value)
        is FilterEvents.OnAccountTypeSelect -> copy(accountTypeFilter = event.value)
        is FilterEvents.OnSyncFilterSelect -> copy(syncFilter = event.value)
        is FilterEvents.OnVisitTypeSelect -> copy(visitTypeFilter = event.value)
// Call your extension functions here!
        is FilterEvents.OnStartDateSelect -> onStartDateSelected(event.millis)
        is FilterEvents.OnEndDateSelect -> onEndDateSelected(event.millis)

        is FilterEvents.OnReset -> clearAll()
        is FilterEvents.ToggleFilterOverlay -> copy(overLay = event.overlay)
    }
}

fun FilterState.clearAll(): FilterState {
    cityState.clearText()
    regionState.clearText()
    accountState.clearText()
    specialtyState.clearText()
    val defaultMillis = System.currentTimeMillis()
    return this.copy(
        selectedCity = null,
        selectedRegion = null,
        selectedAccount = null,
        selectedClass = ClassFilter.All,
        selectedSpecialty = null,
        accountTypeFilter = AccountTypeFilter.All,
        syncFilter = SyncFilter.All,
        visitTypeFilter = VisitTypeFilter.All,
        selectedStartDate = "",
        selectedEndDate = "",
        // Reset the timestamps too, not just the strings
        startDateMillis = defaultMillis,
        endDateMillis = defaultMillis,

        )
}

fun FilterState.setAllowedPeriod(minMillis: Long, maxMillis: Long): FilterState {
    val clampedStart = startDateMillis.coerceIn(minMillis, maxMillis)
    val clampedEnd = endDateMillis.coerceIn(clampedStart, maxMillis)
    return copy(
        minAllowedMillis = minMillis,
        maxAllowedMillis = maxMillis,
        startDateMillis = clampedStart,
        endDateMillis = clampedEnd,
        selectedStartDate = dateFormat.format(Date(clampedStart)),
        selectedEndDate = dateFormat.format(Date(clampedEnd))
    )

}

// 1. Add this helper function
private fun Long.toStartOfDayUtc(): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = this@toStartOfDayUtc
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterState.rememberStartDatePickerState(): DatePickerState {
    return rememberDatePickerState(
        initialSelectedDateMillis = startDateMillis,
        selectableDates = remember(minAllowedMillis, maxAllowedMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // 2. Strip the hours/minutes from the boundaries so the days match exactly
                    val min = minAllowedMillis.toStartOfDayUtc()
                    val max = maxAllowedMillis.toStartOfDayUtc()
                    return utcTimeMillis in min..max
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterState.rememberEndDatePickerState(): DatePickerState {
    return rememberDatePickerState(
        initialSelectedDateMillis = endDateMillis,
        selectableDates = remember(startDateMillis, maxAllowedMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // 2. End date must be between the selected Start Date and the Max Allowed Date
                    val min = startDateMillis.toStartOfDayUtc()
                    val max = maxAllowedMillis.toStartOfDayUtc()
                    return utcTimeMillis in min..max
                }
            }
        }
    )
}


fun FilterState.onStartDateSelected(millis: Long?): FilterState {
    val picked = millis ?: return this

    val validStart = picked.coerceIn(minAllowedMillis, maxAllowedMillis)
    // If the new start date is after the current end date, push the end date forward to match
    val validEnd = endDateMillis.coerceIn(validStart, maxAllowedMillis)

    return copy(
        startDateMillis = validStart,
        endDateMillis = validEnd,
        selectedStartDate = dateFormat.format(Date(validStart)),
        selectedEndDate = dateFormat.format(Date(validEnd))
    )

}

fun FilterState.onEndDateSelected(millis: Long?): FilterState {
    val picked = millis ?: return this

    // End date must be between [startDateMillis .. maxAllowedMillis]
    val validEnd = picked.coerceIn(startDateMillis, maxAllowedMillis)

    return copy(
        endDateMillis = validEnd,
        selectedEndDate = dateFormat.format(Date(validEnd))
    )

}