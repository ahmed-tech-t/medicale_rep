package com.appcore.core.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Creates a DataStore in `files/datastore/[fileName]` that resets to the serializer's default when
 * the file is corrupted. Call it once per file (from a `@Singleton` provider).
 */
fun <T> Context.jsonDataStore(fileName: String, serializer: Serializer<T>): DataStore<T> =
    DataStoreFactory.create(
        serializer = serializer,
        corruptionHandler = ReplaceFileCorruptionHandler { serializer.defaultValue },
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { dataStoreFile(fileName) },
    )
