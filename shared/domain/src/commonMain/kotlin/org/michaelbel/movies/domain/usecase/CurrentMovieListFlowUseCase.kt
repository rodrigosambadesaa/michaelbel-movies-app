package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class CurrentMovieListFlowUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): FlowUseCase<Unit, MovieList>(dispatchers.io) {

    override fun execute(params: Unit): Flow<MovieList> {
        return preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceMovieListKey)
            .map { className -> MovieList.transform(className ?: MovieList.NowPlaying().toString()) }
    }
}
