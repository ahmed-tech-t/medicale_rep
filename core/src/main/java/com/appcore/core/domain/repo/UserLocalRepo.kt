package com.appcore.core.domain.repo

import kotlinx.coroutines.flow.Flow

/** Locally cached profile of the signed-in user; [T] is the app's own `@Serializable` user class. */
interface UserLocalRepo<T> {
    fun getUser(): Flow<T>
    suspend fun saveUser(user: T)
    suspend fun updateUser(transform: (T) -> T)
    suspend fun clear()
}
