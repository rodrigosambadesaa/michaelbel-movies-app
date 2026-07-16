package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.network.AuthenticationNetworkService
import org.michaelbel.movies.network.model.SessionRequest
import org.michaelbel.movies.persistence.database.AccountPersistence
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class DeleteSessionUseCase(
    private val authenticationNetworkService: AuthenticationNetworkService,
    private val accountPersistence: AccountPersistence,
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<Unit, Unit>(dispatchers.io) {

    override suspend fun execute(params: Unit) {
        try {
            val sessionId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey).orEmpty()
            val deletedSession = authenticationNetworkService.deleteSession(SessionRequest(sessionId))
            if (!deletedSession.success) throw DeleteSessionException("")
            val accountId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey).orEmpty()
            accountPersistence.removeById(accountId)
            preferences.run {
                removeValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey)
                removeValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey)
            }
        } catch (exception: Exception) {
            throw DeleteSessionException(exception.message.orEmpty())
        }
    }

    data class DeleteSessionException(
        override val message: String
    ): Exception(message)
}
