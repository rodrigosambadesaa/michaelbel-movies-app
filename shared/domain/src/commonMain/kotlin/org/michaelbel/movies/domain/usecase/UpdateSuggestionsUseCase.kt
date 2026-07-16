@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.network.MovieNetworkService
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.SuggestionPersistence
import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo

class UpdateSuggestionsUseCase(
    private val movieNetworkService: MovieNetworkService,
    private val moviePersistence: MoviePersistence,
    private val suggestionPersistence: SuggestionPersistence,
    dispatchers: SharedDispatchers
): UseCase<String, Unit>(dispatchers.io) {

    override suspend fun execute(language: String) {
        suggestionPersistence.removeAll()

        val nowPlayingMovies = moviePersistence.movies(Movie.NOW_PLAYING, 5)
        when {
            nowPlayingMovies.isNotEmpty() -> {
                suggestionPersistence.insert(nowPlayingMovies.map { movie -> SuggestionPojo(movie.title) })
            }
            else -> {
                val movieResponse = movieNetworkService.movies(
                    list = Movie.NOW_PLAYING,
                    language = language,
                    page = 1
                ).results.take(5)
                suggestionPersistence.insert(movieResponse.map { movie -> SuggestionPojo(movie.title) })
            }
        }
    }
}
