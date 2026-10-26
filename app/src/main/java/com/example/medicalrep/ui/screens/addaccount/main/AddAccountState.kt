package com.example.medicalrep.ui.screens.addaccount.main

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.GeoPoint

@Immutable
data class AddAccountState(
    val city: String = "",
    val district: String = "",
    val entityType: String = "",
    val classCategory: String = "",
    val specialty: String = "",
    val name: TextFieldState = TextFieldState(),
    /** Where the map starts: the rep's location once known, the work area until then. */
    val userLocation: GeoPoint = GeoPoint(30.5589, 31.0112),
    val pin: GeoPoint? = null,
    val signPhoto: Uri? = null,
    val capturedAtMillis: Long = 0L,
    val cityOptions: List<String> = emptyList(),
    val districtOptions: List<String> = emptyList(),
    val entityTypeOptions: List<String> = emptyList(),
    val classOptions: List<String> = listOf("A", "B", "C"),
    val specialtyOptions: List<String> = emptyList(),
) {
    val nameBlank: Boolean get() = name.text.isBlank()

    val canSave: Boolean
        get() = listOf(city, district, entityType, classCategory, specialty).all { it.isNotBlank() } &&
            !nameBlank && pin != null && signPhoto != null
}
