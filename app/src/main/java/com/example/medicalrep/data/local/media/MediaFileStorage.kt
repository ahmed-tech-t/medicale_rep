package com.example.medicalrep.data.local.media

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/** Voice notes and images are files in the app's private folder; the database stores only the path. */
class MediaFileStorage(private val directory: File) {

    @Inject
    constructor(@ApplicationContext context: Context) : this(File(context.filesDir, "media"))

    /** Saves [bytes] under [name] (replacing an older file of that name) and returns the file path. */
    fun save(name: String, bytes: ByteArray): String {
        directory.mkdirs()
        return File(directory, name).apply { writeBytes(bytes) }.absolutePath
    }

    /** The bytes saved at [path], or null when there is no such file. */
    fun read(path: String): ByteArray? = File(path).takeIf(File::isFile)?.readBytes()

    fun delete(path: String): Boolean = File(path).delete()

    fun deleteAll() {
        directory.deleteRecursively()
    }
}
