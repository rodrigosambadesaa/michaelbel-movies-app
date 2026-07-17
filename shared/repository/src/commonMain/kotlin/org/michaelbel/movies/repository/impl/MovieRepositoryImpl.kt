@file:OptIn(ExperimentalTime::class)

package org.michaelbel.movies.repository.impl

import androidx.paging.PagingSource
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.network.AccountNetworkService
import org.michaelbel.movies.network.MovieNetworkService
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.network.model.Result
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.Page
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.persistence.datastore.MoviesPreferences
import org.michaelbel.movies.repository.MovieRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MovieRepositoryImpl(
    private val movieNetworkService: MovieNetworkService,
    private val accountNetworkService: AccountNetworkService,
    private val moviePersistence: MoviePersistence,
    private val preferences: MoviesPreferences
): MovieRepository {

    override fun moviesPagingSource(pagingKey: PagingKey): PagingSource<Int, MoviePojo> {
        return moviePersistence.pagingSource(pagingKey)
    }

    override suspend fun moviesResult(pagingKey: PagingKey, language: String, page: Page): Result<MovieResponse> {
        return when {
            pagingKey == Movie.FAVORITE -> {
                val sessionId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey).orEmpty()
                val accountId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey).orEmpty()
                accountNetworkService.favoriteMovies(accountId, sessionId, language, page)
            }
            else -> {
                if (isTmdbApiKeyEmpty && moviePersistence.isEmpty(MoviePojo.MOVIES_LOCAL_LIST)) throw ApiKeyNotNullException()
                movieNetworkService.movies(pagingKey, language, page)
            }
        }
    }

    override suspend fun movie(pagingKey: PagingKey, movieId: MovieId): MoviePojo {
        return moviePersistence.movieById(pagingKey, movieId).orEmpty
    }

    override suspend fun removeMovies(pagingKey: PagingKey) {
        moviePersistence.removeMovies(pagingKey)
    }

    override suspend fun removeMovie(pagingKey: PagingKey, movieId: MovieId) {
        moviePersistence.removeMovie(pagingKey, movieId)
    }

    override suspend fun insertMovies(pagingKey: PagingKey, page: Page, movies: List<MovieResponse>) {
        val maxPosition = moviePersistence.maxPosition(pagingKey)
        val moviesDb = movies.mapIndexed { index, movieResponse ->
            movieResponse.moviePojo(
                movieList = pagingKey,
                page = page,
                position = if (maxPosition == 0) index else maxPosition.plus(index).plus(1)
            )
        }
        moviePersistence.upsert(moviesDb)
    }

    override suspend fun insertMovie(pagingKey: PagingKey, movie: MoviePojo) {
        val maxPosition = moviePersistence.maxPosition(pagingKey)
        moviePersistence.upsert(
            movie.copy(
                movieList = pagingKey,
                dateAdded = Clock.System.now().toEpochMilliseconds(),
                position = maxPosition.plus(1)
            )
        )
    }
}
