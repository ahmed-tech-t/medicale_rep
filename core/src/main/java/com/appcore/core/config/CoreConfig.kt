package com.appcore.core.config

/**
 * Everything backend/app specific that core needs. Each app `@Provides` one instance.
 * Header names set to null are not sent.
 */
data class CoreConfig(
    val baseUrl: String,
    val isDebug: Boolean,
    /** Android Keystore alias of the AES key that encrypts the auth session file. */
    val cryptoKeyAlias: String,
    val defaultLanguage: String = "en",
    val staticHeaders: Map<String, String> = emptyMap(),
    val languageHeader: String? = "language",
    val countryHeader: String? = "country",
    val deviceIdHeader: String? = "device-id",
)
