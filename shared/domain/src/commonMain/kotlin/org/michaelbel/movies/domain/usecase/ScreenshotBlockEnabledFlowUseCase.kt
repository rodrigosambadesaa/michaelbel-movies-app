package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class ScreenshotBlockEnabledFlowUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): FlowUseCase<Unit, Boolean>(dispatchers.io) {

    override fun execute(params: Unit): Flow<Boolean> {
        return preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceScreenshotBlockKey).map { it.orEmpty() }
    }
}
