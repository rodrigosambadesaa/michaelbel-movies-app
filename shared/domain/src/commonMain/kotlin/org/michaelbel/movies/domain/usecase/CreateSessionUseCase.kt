@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.exceptions.CreateSessionException
import org.michaelbel.movies.network.AuthenticationNetworkService
import org.michaelbel.movies.network.model.RequestToken
import org.michaelbel.movies.network.model.Session
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class CreateSessionUseCase(
    private val authenticationNetworkService: AuthenticationNetworkService,
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<String, Session>(dispatchers.io) {

    override suspend fun execute(token: String): Session {
        return try {
            val session = authenticationNetworkService.createSession(RequestToken(token))
            if (!session.success) throw CreateSessionException()
            preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey, session.sessionId)
            session
        } catch (exception: Exception) {
            throw CreateSessionException(exception.message.orEmpty())
        }
    }

    data class CreateSessionException(
        override val message: String
    ): Exception(message)
}
