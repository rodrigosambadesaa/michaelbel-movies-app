package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class IsBiometricEnabledUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<Unit, Boolean>(dispatchers.io) {

    override suspend fun execute(params: Unit): Boolean {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceBiometricKey).orEmpty()
    }
}
