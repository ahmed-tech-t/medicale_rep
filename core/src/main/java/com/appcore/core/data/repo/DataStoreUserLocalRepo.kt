package com.appcore.core.data.repo

import androidx.datastore.core.DataStore
import com.appcore.core.domain.repo.UserLocalRepo
import kotlinx.coroutines.flow.Flow

/**
 * [UserLocalRepo] over any DataStore, e.g.
 * `DataStoreUserLocalRepo(context.jsonDataStore("userInfo.pb", JsonSerializer(json, UserDS.serializer(), UserDS())), UserDS())`.
 */
class DataStoreUserLocalRepo<T>(
    private val dataStore: DataStore<T>,
    private val default: T,
) : UserLocalRepo<T> {

    override fun getUser(): Flow<T> = dataStore.data

    override suspend fun saveUser(user: T) {
        dataStore.updateData { user }
    }

    override suspend fun updateUser(transform: (T) -> T) {
        dataStore.updateData { transform(it) }
    }

    override suspend fun clear() {
        dataStore.updateData { default }
    }
}
