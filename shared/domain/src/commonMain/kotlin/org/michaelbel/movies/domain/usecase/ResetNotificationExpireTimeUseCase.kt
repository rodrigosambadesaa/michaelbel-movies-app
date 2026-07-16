package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class ResetNotificationExpireTimeUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<Unit, Unit>(dispatchers.io) {

    override suspend fun execute(params: Unit) {
        preferences.removeValue(MoviesPreferences.PreferenceKey.PreferenceNotificationExpireTimeKey)
    }
}
