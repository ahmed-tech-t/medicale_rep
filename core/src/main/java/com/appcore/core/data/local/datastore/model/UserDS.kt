package com.appcore.core.data.local.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDS(
    val description: String? = null,
    val firstName: String = "",
    val id: Int? = 1,
    val lastName: String = "",
    val picture: String = "",
    val roleName: String = "",
    /** Where the user was last seen; null until the first GPS update. Cleared with the user. */
    val lastLatitude: Double? = null,
    val lastLongitude: Double? = null,
    val lastLocationTimeMillis: Long = 0L,
    /** How many metres the last location may be off; null until the first GPS update. */
    val lastAccuracyMeters: Float? = null,
    /** The street at [lastLatitude], [lastLongitude]; null when the lookup found none or failed. */
    val lastAddress: String? = null,
    /** The start of the day (millis) the visits that were not done were last marked Missed; 0 = never. */
    val missedCheckedDay: Long = 0L,
)
