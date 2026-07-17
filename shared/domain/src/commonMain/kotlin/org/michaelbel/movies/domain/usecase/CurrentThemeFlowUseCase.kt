package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class CurrentThemeFlowUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): FlowUseCase<Unit, AppTheme>(dispatchers.io) {

    override fun execute(params: Unit): Flow<AppTheme> {
        return preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceThemeKey)
            .map { name -> AppTheme.transform(name ?: AppTheme.FollowSystem.toString()) }
    }
}
