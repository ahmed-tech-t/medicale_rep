package com.appcore.core.data.local.session

/**
 * Runs when the session is cleared (logout or expiry). Apps add one with Hilt `@IntoSet`
 * to wipe their own per-user state, e.g. favorites or the cached user profile.
 */
fun interface SessionClearListener {
    suspend fun onSessionCleared()
}
