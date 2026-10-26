package com.example.medicalrep.data.local.db

/**
 * Remembers whose data is in the database. Signing out keeps everything (the rep may still have data to upload),
 * but when a different user signs in, [wipe] clears all tables and media first so data never mixes.
 */
class DatabaseOwner(
    private val readOwnerId: () -> String?,
    private val writeOwnerId: (String) -> Unit,
    private val wipe: suspend () -> Unit,
) {

    suspend fun signedIn(userId: String) {
        val owner = readOwnerId()
        if (owner != null && owner != userId) wipe()
        writeOwnerId(userId)
    }
}
