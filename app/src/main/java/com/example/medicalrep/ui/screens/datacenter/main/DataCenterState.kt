package com.example.medicalrep.ui.screens.datacenter.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.screens.datacenter.models.DataCenterSyncItemUi

@Immutable
data class DataCenterState(
    val items: List<DataCenterSyncItemUi> = emptyList(),
)
