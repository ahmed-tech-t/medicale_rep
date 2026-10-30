package com.example.medicalrep.domain.models

/** A district of a city; a rep and an account belong to districts. */
data class District(val id: String, val name: String, val cityId: String)
