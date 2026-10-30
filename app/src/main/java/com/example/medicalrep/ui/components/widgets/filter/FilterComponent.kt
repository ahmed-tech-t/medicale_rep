package com.example.medicalrep.ui.components.widgets.filter

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.medicalrep.R
import com.appcore.core.ui.theme.spacing
import com.example.medicalrep.ui.components.widgets.BottomSheetComponent

/** The filter form in a bottom sheet; the caller owns [state] and decides what closing and applying do. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterComponent(
    state: FilterState,
    onEvent: (FilterEvents) -> Unit,
    onApplyFilter: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.filter_visits_title),
    showAreaPicker: Boolean = true,
    showDateFilter: Boolean = true,
    showClassFilter: Boolean = true,
    showSpecialtyFilter: Boolean = true,
    showAccountType: Boolean = true,
    showSyncStatus: Boolean = true,
    showVisitType: Boolean = true,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    BottomSheetComponent(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        content = {
            FilterContentComponent(
                state = state,
                onEvent = onEvent,
                onApplyFilter = onApplyFilter,
                title = title,
                showAreaPicker = showAreaPicker,
                showDateFilter = showDateFilter,
                showClassFilter = showClassFilter,
                showSpecialtyFilter = showSpecialtyFilter,
                showAccountType = showAccountType,
                showSyncStatus = showSyncStatus,
                showVisitType = showVisitType,
            )
        },
    )
}
