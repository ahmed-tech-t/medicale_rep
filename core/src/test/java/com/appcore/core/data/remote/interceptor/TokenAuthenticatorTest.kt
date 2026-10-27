package com.appcore.core.data.remote.interceptor

import com.appcore.core.data.local.datastore.model.UserDS
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.session.FakeUserLocalRepo
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class TokenAuthenticatorTest {

    private class FakeRepo : AuthSessionLocalRepo {
        var access = ""
        var refresh = ""
        override suspend fun getAccessToken() = access
        override suspend fun getRefreshToken() = refresh
        override suspend fun saveSession(accessToken: String, refreshToken: String) {
            access = accessToken
            refresh = refreshToken
        }
        override suspend fun clearSession() { access = ""; refresh = "" }
    }

    private fun signedInSession(): AuthSessionManager {
        val repo = FakeRepo().apply { access = "old-access"; refresh = "old-refresh" }
        return AuthSessionManager(repo, FakeUserLocalRepo(UserDS()), emptySet()).also { runBlocking { it.initialize() } }
    }

    private fun unauthorized(): Response = Response.Builder()
        .request(
            Request.Builder()
                .url("https://example.com/api/home")
                .header("Authorization", "Bearer old-access")
                .build()
        )
        .protocol(Protocol.HTTP_1_1)
        .code(401)
        .message("Unauthorized")
        .build()

    private fun httpException(code: Int) = HttpException(
        retrofit2.Response.error<Unit>(code, "".toResponseBody())
    )

    @Test
    fun `successful refresh retries with new bearer and saves tokens`() {
        val session = signedInSession()
        val authenticator = TokenAuthenticator(session, { RefreshResult.Success("new-access", "new-refresh") })

        val retry = authenticator.authenticate(null, unauthorized())

        assertEquals("Bearer new-access", retry?.header("Authorization"))
        assertEquals("new-access", session.getAccessTokenSync())
        assertEquals("new-refresh", session.getRefreshTokenSync())
    }

    @Test
    fun `invalid session clears tokens and emits expiry`() = runTest {
        val session = signedInSession()
        val authenticator = TokenAuthenticator(session, { RefreshResult.InvalidSession })
        val expired = async { session.sessionExpired.first() }
        yield()

        assertNull(authenticator.authenticate(null, unauthorized()))
        expired.await()
        assertEquals("", session.getAccessTokenSync())
    }

    @Test
    fun `http 401 from refresh call expires session`() {
        val session = signedInSession()
        val authenticator = TokenAuthenticator(session, { throw httpException(401) })

        assertNull(authenticator.authenticate(null, unauthorized()))
        assertEquals("", session.getAccessTokenSync())
    }

    @Test
    fun `network failure keeps session`() {
        val session = signedInSession()
        val authenticator = TokenAuthenticator(session, { throw IOException("offline") })

        assertNull(authenticator.authenticate(null, unauthorized()))
        assertEquals("old-access", session.getAccessTokenSync())
    }

    @Test
    fun `token already refreshed by a parallel request is reused`() {
        val session = signedInSession()
        var refresherCalls = 0
        val authenticator = TokenAuthenticator(session, {
            refresherCalls++
            RefreshResult.Success("new-access", "new-refresh")
        })
        runBlocking { session.saveSession("parallel-access", "parallel-refresh") }

        val retry = authenticator.authenticate(null, unauthorized())

        assertEquals("Bearer parallel-access", retry?.header("Authorization"))
        assertEquals(0, refresherCalls)
    }
}
