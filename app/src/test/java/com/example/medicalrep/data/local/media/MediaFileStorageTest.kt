package com.example.medicalrep.data.local.media

import java.io.File
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * 1. Saved bytes are read back from the returned path. 2. Saving the same name replaces the file.
 * 3. Reading a missing path gives null. 4. delete removes the file, and returns false when it is missing.
 * 5. deleteAll removes everything, and saving afterwards works again.
 */
class MediaFileStorageTest {
    @get:Rule val folder = TemporaryFolder()
    private val storage by lazy { MediaFileStorage(File(folder.root, "media")) }

    @Test
    fun `saved bytes are read back`() {
        val path = storage.save("note.m4a", byteArrayOf(1, 2, 3))
        assertArrayEquals(byteArrayOf(1, 2, 3), storage.read(path))
    }

    @Test
    fun `saving the same name replaces the file`() {
        storage.save("note.m4a", byteArrayOf(1, 2, 3))
        val path = storage.save("note.m4a", byteArrayOf(9))
        assertArrayEquals(byteArrayOf(9), storage.read(path))
    }

    @Test
    fun `missing file reads as null`() = assertNull(storage.read(File(folder.root, "none").path))

    @Test
    fun `delete removes the file`() {
        val path = storage.save("a", byteArrayOf(1))
        assertTrue(storage.delete(path))
        assertNull(storage.read(path))
        assertFalse(storage.delete(path))
    }

    @Test
    fun `deleteAll clears and storage still works`() {
        val path = storage.save("a", byteArrayOf(1))
        storage.deleteAll()
        assertNull(storage.read(path))
        assertArrayEquals(byteArrayOf(2), storage.read(storage.save("b", byteArrayOf(2))))
    }
}
