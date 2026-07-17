@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.network.MovieNetworkService
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.entity.mini.MovieDbMini
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo

class MoviesWidgetUseCase(
    private val movieNetworkService: MovieNetworkService,
    private val moviePersistence: MoviePersistence,
    dispatchers: SharedDispatchers
): UseCase<String, List<MovieDbMini>>(dispatchers.io) {

    override suspend fun execute(language: String): List<MovieDbMini> {
        return try {
            val movieResult = movieNetworkService.movies(
                list = MovieList.Upcoming().name,
                language = language,
                page = 1
            )
            val moviesDb = movieResult.results.mapIndexed { index, movieResponse ->
                movieResponse.moviePojo(
                    movieList = MoviePojo.MOVIES_WIDGET,
                    position = index.plus(1)
                )
            }
            moviePersistence.removeMovies(MoviePojo.MOVIES_WIDGET)
            moviePersistence.upsert(moviesDb)
            moviePersistence.moviesMini(MoviePojo.MOVIES_WIDGET, MovieResponse.DEFAULT_PAGE_SIZE)
        } catch (exception: Exception) {
            moviePersistence.moviesMini(MoviePojo.MOVIES_WIDGET, MovieResponse.DEFAULT_PAGE_SIZE).ifEmpty {
                throw MoviesUpcomingException(exception.message.orEmpty())
            }
        }
    }

    data class MoviesUpcomingException(
        override val message: String
    ): Exception(message)
}
