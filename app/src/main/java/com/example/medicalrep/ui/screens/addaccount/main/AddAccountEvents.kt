package com.example.medicalrep.ui.screens.addaccount.main

import com.appcore.core.ui.utils.document_picker.PickedDocument
import com.example.medicalrep.domain.models.WorkingTime

sealed interface AddAccountEvents {
    data object Back : AddAccountEvents
    data class SelectCity(val value: String) : AddAccountEvents
    data class SelectDistrict(val value: String) : AddAccountEvents
    data class SelectEntityType(val value: String) : AddAccountEvents
    data class SelectWorkingTime(val value: WorkingTime) : AddAccountEvents
    data class PinPlaced(val latitude: Double, val longitude: Double) : AddAccountEvents
    data class SignPhotoCaptured(val document: PickedDocument) : AddAccountEvents
    data object Save : AddAccountEvents
}
