package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.network.SearchNetworkService
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.network.ktx.isEmpty
import org.michaelbel.movies.network.ktx.nextPage
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.PagingKeyPersistence
import org.michaelbel.movies.persistence.database.entity.pojo.PagingKeyPojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.domain.usecase.FetchAndInsertSearchMoviesUseCase.Params

class FetchAndInsertSearchMoviesUseCase(
    private val searchNetworkService: SearchNetworkService,
    private val moviePersistence: MoviePersistence,
    private val pagingKeyPersistence: PagingKeyPersistence,
    private val moviesDatabase: MoviesDatabase,
    dispatchers: SharedDispatchers
): UseCase<Params, Unit>(dispatchers.io) {

    override suspend fun execute(params: Params) {
        if (params.query.isEmpty()) throw PageEmptyException()
        if (isTmdbApiKeyEmpty) throw ApiKeyNotNullException()

        val moviesResult = searchNetworkService.searchMovies(params.query, params.language, 1)

        moviesDatabase.withTransaction {
            pagingKeyPersistence.removePagingKey(params.query)
            moviePersistence.removeMovies(params.query)

            if (moviesResult.isEmpty) throw PageEmptyException()

            pagingKeyPersistence.upsertPagingKey(
                PagingKeyPojo(
                    pagingKey = params.query,
                    page = moviesResult.nextPage,
                    totalPages = moviesResult.totalPages
                )
            )

            val maxPosition = moviePersistence.maxPosition(params.query)
            val moviesDb = moviesResult.results.mapIndexed { index, movieResponse ->
                movieResponse.moviePojo(
                    movieList = params.query,
                    page = moviesResult.page,
                    position = if (maxPosition == 0) index else maxPosition.plus(index).plus(1)
                )
            }
            moviePersistence.upsert(moviesDb)
        }
    }

    data class Params(
        val query: Query,
        val language: String
    )
}
