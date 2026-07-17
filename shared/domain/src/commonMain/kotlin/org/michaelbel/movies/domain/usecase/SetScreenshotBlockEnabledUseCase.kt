@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class SetScreenshotBlockEnabledUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<Boolean, Unit>(dispatchers.io) {

    override suspend fun execute(enabled: Boolean) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceScreenshotBlockKey, enabled)
    }
}
