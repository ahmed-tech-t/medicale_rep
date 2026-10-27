package com.appcore.core.data.local.session

import com.appcore.core.data.local.datastore.model.UserDS
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The session keeps the signed-in user next to the tokens, with an in-memory copy filled at initialize():
 * 1. Nothing signed in: an empty user. 2. initialize loads the stored user.
 * 3. signIn saves tokens and user together and the copy follows. 4. A token refresh keeps the user.
 * 5. updateUser changes the stored user and the copy. 6. clearSession clears tokens, the stored user and the copy.
 */
class AuthSessionManagerUserTest {
    private val users = FakeUserLocalRepo(UserDS())
    private val tokens = InMemoryTokens()
    private val session = AuthSessionManager(tokens, users, emptySet())

    @Test
    fun `nothing signed in gives an empty user`() {
        assertEquals(UserDS(), session.user.value)
    }

    @Test
    fun `initialize loads the stored user`() = runBlocking {
        users.saveUser(UserDS(id = 7, firstName = "Ahmed"))
        session.initialize()
        assertEquals(7, session.user.value.id)
    }

    @Test
    fun `sign in saves tokens and user together`() = runBlocking {
        session.signIn("access", "refresh", UserDS(id = 7))
        assertEquals("access", session.getAccessTokenSync())
        assertEquals(7, session.user.value.id)
        assertEquals(7, users.current.value.id)
    }

    @Test
    fun `a token refresh keeps the user`() = runBlocking {
        session.signIn("a", "r", UserDS(id = 7))
        session.saveSession("a2", "r2")
        assertEquals("a2", session.getAccessTokenSync())
        assertEquals(7, session.user.value.id)
    }

    @Test
    fun `update user changes the stored user and the copy`() = runBlocking {
        session.signIn("a", "r", UserDS(id = 7))
        session.updateUser { it.copy(lastLatitude = 30.5, lastLongitude = 31.1) }
        assertEquals(30.5, session.user.value.lastLatitude)
        assertEquals(30.5, users.current.value.lastLatitude)
        assertEquals(7, session.user.value.id)
    }

    @Test
    fun `clearing the session clears tokens and user`() = runBlocking {
        session.signIn("a", "r", UserDS(id = 7))
        session.clearSession()
        assertEquals("", session.getAccessTokenSync())
        assertEquals(UserDS(), session.user.value)
        assertEquals(UserDS(), users.current.value)
    }
}

private class InMemoryTokens : AuthSessionLocalRepo {
    private var access = ""
    private var refresh = ""
    override suspend fun getAccessToken() = access
    override suspend fun getRefreshToken() = refresh
    override suspend fun saveSession(accessToken: String, refreshToken: String) {
        access = accessToken
        refresh = refreshToken
    }
    override suspend fun clearSession() {
        access = ""
        refresh = ""
    }
}
