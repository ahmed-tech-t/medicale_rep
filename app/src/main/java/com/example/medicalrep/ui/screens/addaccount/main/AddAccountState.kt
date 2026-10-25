package com.example.medicalrep.ui.screens.addaccount.main

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.map.MapCameraTarget

@Immutable
data class AddAccountState(
    val city: String = "",
    val district: String = "",
    val entityType: String = "",
    val classCategory: String = "",
    val specialty: String = "",
    val name: TextFieldState = TextFieldState(),
    val userLocation: MapCameraTarget = MapCameraTarget(0.0, 0.0),
    val pin: MapCameraTarget? = null,
    val signPhoto: Uri? = null,
    val capturedAtMillis: Long = 0L,
    val cityOptions: List<String> = emptyList(),
    val districtOptions: List<String> = emptyList(),
    val entityTypeOptions: List<String> = emptyList(),
    val classOptions: List<String> = emptyList(),
    val specialtyOptions: List<String> = emptyList(),
) {
    val nameBlank: Boolean get() = name.text.isBlank()

    val canSave: Boolean
        get() = listOf(city, district, entityType, classCategory, specialty).all { it.isNotBlank() } &&
            !nameBlank && pin != null && signPhoto != null
}
