package com.appcore.core.data.local.datastore.model

import kotlinx.serialization.Serializable

/** Fresh installs start from `CoreConfig` defaults (see `CoreStorageModule`), not these. */
@Serializable
data class AppSettingsDS(
    val language: String = "en",
    val country: String = "",
    val showOnBoarding: Boolean = true,
)
