package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.network.SearchNetworkService
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.network.model.Result
import org.michaelbel.movies.persistence.database.typealiases.Page
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.domain.usecase.SearchMoviesResultUseCase.Params

class SearchMoviesResultUseCase(
    private val searchNetworkService: SearchNetworkService,
    dispatchers: SharedDispatchers
): UseCase<Params, Result<MovieResponse>>(dispatchers.io) {

    override suspend fun execute(params: Params): Result<MovieResponse> {
        if (isTmdbApiKeyEmpty) throw ApiKeyNotNullException()
        return searchNetworkService.searchMovies(
            query = params.query,
            language = params.language,
            page = params.page
        )
    }

    data class Params(
        val query: Query,
        val language: String,
        val page: Page
    )
}
