package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.Page
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

class InsertMoviesUseCase(
    private val moviePersistence: MoviePersistence,
    dispatchers: SharedDispatchers
): UseCase<InsertMoviesUseCase.Params, Unit>(dispatchers.io) {

    override suspend fun execute(params: Params) {
        val maxPosition = moviePersistence.maxPosition(params.pagingKey)
        val moviesDb = params.movies.mapIndexed { index, movieResponse ->
            movieResponse.moviePojo(
                movieList = params.pagingKey,
                page = params.page,
                position = if (maxPosition == 0) index else maxPosition.plus(index).plus(1)
            )
        }
        moviePersistence.upsert(moviesDb)
    }

    data class Params(
        val pagingKey: PagingKey,
        val page: Page,
        val movies: List<MovieResponse>
    )
}
