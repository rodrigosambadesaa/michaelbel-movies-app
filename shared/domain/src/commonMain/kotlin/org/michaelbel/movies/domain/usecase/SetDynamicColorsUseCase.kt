@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.analytics.MoviesAnalytics
import org.michaelbel.movies.analytics.event.ChangeDynamicColorsEvent
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class SetDynamicColorsUseCase(
    private val preferences: MoviesPreferences,
    private val analytics: MoviesAnalytics,
    dispatchers: SharedDispatchers
): UseCase<Boolean, Unit>(dispatchers.io) {

    override suspend fun execute(value: Boolean) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceDynamicColorsKey, value)
        analytics.logEvent(ChangeDynamicColorsEvent(value))
    }
}
