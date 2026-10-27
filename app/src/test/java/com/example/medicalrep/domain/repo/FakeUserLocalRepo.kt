package com.example.medicalrep.domain.repo

import com.appcore.core.domain.repo.UserLocalRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory user cache for tests; [clear] goes back to [default]. */
class FakeUserLocalRepo<T>(private val default: T) : UserLocalRepo<T> {
    val current = MutableStateFlow(default)

    override fun getUser(): Flow<T> = current

    override suspend fun saveUser(user: T) {
        current.value = user
    }

    override suspend fun updateUser(transform: (T) -> T) {
        current.value = transform(current.value)
    }

    override suspend fun clear() {
        current.value = default
    }
}
