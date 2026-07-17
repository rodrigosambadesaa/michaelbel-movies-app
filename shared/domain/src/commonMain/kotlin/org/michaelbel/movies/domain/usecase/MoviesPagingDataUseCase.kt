@file:OptIn(ExperimentalPagingApi::class)

package org.michaelbel.movies.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.domain.usecase.remote.FeedMoviesRemoteMediator
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.repository.PagingKeyRepository

class MoviesPagingDataUseCase(
    private val moviePersistence: MoviePersistence,
    private val pagingKeyRepository: PagingKeyRepository,
    private val moviesResultUseCase: MoviesResultUseCase,
    private val moviesDatabase: MoviesDatabase,
    dispatchers: SharedDispatchers
): FlowUseCase<MoviesPagingDataUseCase.Params, PagingData<MoviePojo>>(dispatchers.io) {

    override fun execute(params: Params): Flow<PagingData<MoviePojo>> {
        val pagingKey = MovieList.name(params.movieList)
        val sourceKey = if (isTmdbApiKeyEmpty) MoviePojo.MOVIES_LOCAL_LIST else pagingKey
        return Pager(
            config = PagingConfig(
                pageSize = MovieResponse.DEFAULT_PAGE_SIZE,
                enablePlaceholders = true
            ),
            remoteMediator = FeedMoviesRemoteMediator(
                language = params.language,
                pagingKeyRepository = pagingKeyRepository,
                moviesResultUseCase = moviesResultUseCase,
                moviePersistence = moviePersistence,
                moviesDatabase = moviesDatabase,
                pagingKey = pagingKey
            ),
            pagingSourceFactory = { moviePersistence.pagingSource(sourceKey) }
        ).flow
    }

    data class Params(
        val movieList: MovieList,
        val language: String
    )
}
