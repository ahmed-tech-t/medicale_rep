package com.appcore.core.data.local.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.appcore.core.data.local.crypto.CryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/** JSON DataStore serializer that encrypts the file with [CryptoManager]. */
class EncryptedJsonSerializer<T>(
    private val crypto: CryptoManager,
    private val json: Json,
    private val serializer: KSerializer<T>,
    override val defaultValue: T,
) : Serializer<T> {

    override suspend fun readFrom(input: InputStream): T {
        return try {
            val cipherBytes = input.readBytes()
            if (cipherBytes.isEmpty()) return defaultValue

            val plainBytes = crypto.decrypt(cipherBytes)
            json.decodeFromString(serializer, plainBytes.decodeToString())
        } catch (e: Exception) {
            throw CorruptionException("Cannot read encrypted data", e)
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        withContext(Dispatchers.IO) {
            val encodedValue = json.encodeToString(serializer, t)
            output.write(crypto.encrypt(encodedValue.encodeToByteArray()))
        }
    }
}
