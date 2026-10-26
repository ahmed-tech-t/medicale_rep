package com.example.medicalrep.domain.models

/** A sample or gift the rep carries; [remaining] is what is left of what the server says he received, minus what is not uploaded yet. */
data class Custody(
    val id: String,
    val name: String,
    val kind: ProductType = ProductType.Gift,
    val description: String = "",
    val received: Int = 0,
    val dispatched: Int = 0,
    /** Quantity given in visits that are not uploaded yet, so the balance is right while offline. */
    val pending: Int = 0,
) {
    val remaining: Int get() = received - dispatched - pending
}
