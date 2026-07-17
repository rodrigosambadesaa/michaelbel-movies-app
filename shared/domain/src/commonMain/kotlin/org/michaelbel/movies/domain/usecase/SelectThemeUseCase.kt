@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.analytics.MoviesAnalytics
import org.michaelbel.movies.analytics.event.SelectThemeEvent
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class SelectThemeUseCase(
    private val preferences: MoviesPreferences,
    private val analytics: MoviesAnalytics,
    dispatchers: SharedDispatchers
): UseCase<AppTheme, Unit>(dispatchers.io) {

    override suspend fun execute(appTheme: AppTheme) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceThemeKey, appTheme.toString())
        analytics.logEvent(SelectThemeEvent(appTheme.toString()))
    }
}
