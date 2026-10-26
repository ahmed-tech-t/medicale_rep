package com.example.medicalrep.data.remote.dto

import kotlinx.serialization.Serializable

/** Nominatim answers 200 with only `error` set when the point has no address. */
@Serializable
data class NominatimReverseDto(
    val address: Address? = null,
    val error: String? = null,
) {
    @Serializable
    data class Address(val road: String? = null)
}
