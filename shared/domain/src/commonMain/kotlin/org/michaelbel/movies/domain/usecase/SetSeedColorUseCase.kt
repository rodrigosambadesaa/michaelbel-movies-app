@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class SetSeedColorUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<Int, Unit>(dispatchers.io) {

    override suspend fun execute(seedColor: Int) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceSeedColorKey, seedColor)
    }
}
