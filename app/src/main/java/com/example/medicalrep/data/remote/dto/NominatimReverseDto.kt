package com.example.medicalrep.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Nominatim answers 200 with only `error` set when the point has no address. */
@Serializable
data class NominatimReverseDto(
    @SerialName("display_name") val displayName: String? = null,
    val error: String? = null,
)
