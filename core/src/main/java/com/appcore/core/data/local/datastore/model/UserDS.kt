package com.appcore.core.data.local.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDS(
    val completed: Boolean = false,
    val country: String? = null,
    val currency: String? = null,
    val description: String? = null,
    val email: String = "",
    val emailVerified: Boolean = false,
    val expiresIn: Int = 0,
    val firstName: String = "",
    val id: Int? = null,
    val identificationStatus: Boolean? = null,
    val idtentificationPicture: String? = null,
    val lastName: String = "",
    val phone: String = "",
    val phoneVerified: Boolean = false,
    val picture: String = "",
    val refreshExpiresIn: Int = 0,
    val roleName: String = "",
    val status: String = "",
    val loginWithSocial: Boolean = false,
    /** Where the user was last seen; null until the first GPS update. Cleared with the user. */
    val lastLatitude: Double? = null,
    val lastLongitude: Double? = null,
    val lastLocationTimeMillis: Long = 0L,
)
