package com.example.medicalrep.data.local.db

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 1. First ever sign in: nothing to wipe, owner is stored.
 * 2. Same user signs in again: data is kept.
 * 3. A different user signs in: data is wiped once and the new owner is stored.
 */
class DatabaseOwnerTest {
    private var owner: String? = null
    private var wipes = 0
    private val databaseOwner = DatabaseOwner({ owner }, { owner = it }, { wipes++ })

    @Test
    fun `first sign in keeps data and stores the owner`() = runBlocking {
        databaseOwner.signedIn("u1")
        assertEquals(0, wipes)
        assertEquals("u1", owner)
    }

    @Test
    fun `same user keeps data`() = runBlocking {
        databaseOwner.signedIn("u1")
        databaseOwner.signedIn("u1")
        assertEquals(0, wipes)
    }

    @Test
    fun `different user wipes once and becomes the owner`() = runBlocking {
        databaseOwner.signedIn("u1")
        databaseOwner.signedIn("u2")
        assertEquals(1, wipes)
        assertEquals("u2", owner)
        databaseOwner.signedIn("u2")
        assertEquals(1, wipes)
    }
}
