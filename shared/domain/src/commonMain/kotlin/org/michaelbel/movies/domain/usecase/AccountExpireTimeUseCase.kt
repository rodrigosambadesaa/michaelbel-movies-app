package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class AccountExpireTimeUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<Unit, Long>(dispatchers.io) {

    override suspend fun execute(params: Unit): Long {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountExpireTimeKey).orEmpty()
    }
}
