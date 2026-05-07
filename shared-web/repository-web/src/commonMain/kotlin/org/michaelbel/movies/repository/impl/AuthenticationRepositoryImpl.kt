package org.michaelbel.movies.repository.impl

import org.michaelbel.movies.common.exceptions.CreateRequestTokenException
import org.michaelbel.movies.common.exceptions.CreateSessionException
import org.michaelbel.movies.common.exceptions.CreateSessionWithLoginException
import org.michaelbel.movies.common.exceptions.DeleteSessionException
import org.michaelbel.movies.network.AuthenticationNetworkService
import org.michaelbel.movies.network.model.RequestToken
import org.michaelbel.movies.network.model.Session
import org.michaelbel.movies.network.model.SessionRequest
import org.michaelbel.movies.network.model.Token
import org.michaelbel.movies.network.model.Username
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences
import org.michaelbel.movies.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val authenticationNetworkService: AuthenticationNetworkService,
    private val repositoryWebStore: RepositoryWebStore,
    private val preferences: MoviesPreferences
): AuthenticationRepository {

    override suspend fun createRequestToken(loginViaTmdb: Boolean): Token {
        return try {
            val token = authenticationNetworkService.createRequestToken()
            if (!token.success) throw CreateRequestTokenException(loginViaTmdb)
            token
        } catch (_: Exception) {
            throw CreateRequestTokenException(loginViaTmdb)
        }
    }

    override suspend fun createSessionWithLogin(username: String, password: String, requestToken: String): Token {
        return try {
            val token = authenticationNetworkService.createSessionWithLogin(
                username = Username(
                    username = username,
                    password = password,
                    requestToken = requestToken
                )
            )
            if (!token.success) throw CreateSessionWithLoginException()
            token
        } catch (_: Exception) {
            throw CreateSessionWithLoginException()
        }
    }

    override suspend fun createSession(token: String): Session {
        return try {
            val session = authenticationNetworkService.createSession(RequestToken(token))
            if (!session.success) throw CreateSessionException()
            preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey, session.sessionId)
            session
        } catch (_: Exception) {
            throw CreateSessionException()
        }
    }

    override suspend fun deleteSession() {
        try {
            val sessionId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey).orEmpty()
            val deletedSession = authenticationNetworkService.deleteSession(SessionRequest(sessionId))
            if (!deletedSession.success) throw DeleteSessionException()

            repositoryWebStore.clearAccount()
            preferences.run {
                removeValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey)
                removeValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey)
            }
        } catch (_: Exception) {
            throw DeleteSessionException()
        }
    }
}
