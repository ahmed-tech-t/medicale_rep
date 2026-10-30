package com.example.medicalrep.domain.models

/** A sample or gift the rep carries: [received] is what came in, [remaining] what is left after the visits. */
data class Custody(
    val id: String,
    val name: String,
    val kind: ProductType = ProductType.Gift,
    val description: String = "",
    val received: Int = 0,
    val remaining: Int = received,
)
