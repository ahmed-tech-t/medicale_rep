package com.appcore.core.data.repo

import androidx.datastore.core.DataStore
import com.appcore.core.data.local.datastore.model.AuthSessionDS
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class AuthSessionLocalRepoImpl @Inject constructor(
    private val dataStore: DataStore<AuthSessionDS>
) : AuthSessionLocalRepo {

    override suspend fun getAccessToken(): String = dataStore.data.first().accessToken

    override suspend fun getRefreshToken(): String = dataStore.data.first().refreshToken

    override suspend fun saveSession(accessToken: String, refreshToken: String) {
        dataStore.updateData { it.copy(accessToken = accessToken, refreshToken = refreshToken) }
    }

    override suspend fun clearSession() {
        dataStore.updateData { AuthSessionDS() }
    }
}
