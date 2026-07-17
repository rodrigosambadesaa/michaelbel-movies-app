package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.network.AccountNetworkService
import org.michaelbel.movies.network.MovieNetworkService
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.network.model.Result
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.database.typealiases.Page
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class MoviesResultUseCase(
    private val movieNetworkService: MovieNetworkService,
    private val accountNetworkService: AccountNetworkService,
    private val moviePersistence: MoviePersistence,
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<MoviesResultUseCase.Params, Result<MovieResponse>>(dispatchers.io) {

    override suspend fun execute(params: Params): Result<MovieResponse> {
        return when {
            params.pagingKey == Movie.FAVORITE -> {
                val sessionId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey).orEmpty()
                val accountId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey).orEmpty()
                accountNetworkService.favoriteMovies(accountId, sessionId, params.language, params.page)
            }
            else -> {
                if (isTmdbApiKeyEmpty && moviePersistence.isEmpty(MoviePojo.MOVIES_LOCAL_LIST)) throw ApiKeyNotNullException()
                movieNetworkService.movies(params.pagingKey, params.language, params.page)
            }
        }
    }

    data class Params(
        val pagingKey: PagingKey,
        val language: String,
        val page: Page
    )
}
