package com.example.medicalrep.data.local.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/** Voice notes and images are files in the app's private folder; the database stores only the path. */
class MediaFileStorage(
    private val directory: File,
    private val contentResolver: ContentResolver? = null,
) {

    @Inject
    constructor(@ApplicationContext context: Context) : this(File(context.filesDir, "media"), context.contentResolver)

    /** Saves [bytes] under [name] (replacing an older file of that name) and returns the file path. */
    fun save(name: String, bytes: ByteArray): String {
        directory.mkdirs()
        return File(directory, name).apply { writeBytes(bytes) }.absolutePath
    }

    /**
     * Copies what the Uri string [source] points at into a file called [name] and returns that file as a `file:` Uri string,
     * so the copy no longer depends on the picker or camera. A [source] that already is one of our files is returned as it is.
     */
    fun copy(name: String, source: String): String {
        val uri = Uri.parse(source)
        if (uri.scheme == "file" && uri.path?.let(::File)?.parentFile == directory) return source
        directory.mkdirs()
        val target = File(directory, name)
        val input = requireNotNull(contentResolver?.openInputStream(uri)) { "Cannot read $source" }
        input.use { from -> target.outputStream().use { to -> from.copyTo(to) } }
        return Uri.fromFile(target).toString()
    }

    /** The bytes saved at [path], or null when there is no such file. */
    fun read(path: String): ByteArray? = File(path).takeIf(File::isFile)?.readBytes()

    fun delete(path: String): Boolean = File(path).delete()

    /** Deletes the file a `file:` Uri string points at; any other Uri is not ours and is left alone. */
    fun deleteUri(source: String): Boolean {
        val uri = Uri.parse(source)
        val file = uri.path?.let(::File)?.takeIf { uri.scheme == "file" && it.parentFile == directory } ?: return false
        return file.delete()
    }

    fun deleteAll() {
        directory.deleteRecursively()
    }
}
