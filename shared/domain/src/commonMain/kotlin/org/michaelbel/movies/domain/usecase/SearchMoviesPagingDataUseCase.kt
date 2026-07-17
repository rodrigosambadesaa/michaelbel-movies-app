@file:OptIn(ExperimentalPagingApi::class)

package org.michaelbel.movies.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.domain.usecase.remote.SearchMoviesRemoteMediator
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.repository.PagingKeyRepository
import org.michaelbel.movies.domain.usecase.SearchMoviesPagingDataUseCase.Params

class SearchMoviesPagingDataUseCase(
    private val moviePersistence: MoviePersistence,
    private val pagingKeyRepository: PagingKeyRepository,
    private val searchMoviesResultUseCase: SearchMoviesResultUseCase,
    private val moviesDatabase: MoviesDatabase,
    dispatchers: SharedDispatchers
): FlowUseCase<Params, PagingData<MoviePojo>>(dispatchers.io) {

    override fun execute(params: Params): Flow<PagingData<MoviePojo>> {
        return Pager(
            config = PagingConfig(
                pageSize = MovieResponse.DEFAULT_PAGE_SIZE,
                enablePlaceholders = true
            ),
            remoteMediator = SearchMoviesRemoteMediator(
                language = params.language,
                pagingKeyRepository = pagingKeyRepository,
                moviePersistence = moviePersistence,
                searchMoviesResultUseCase = searchMoviesResultUseCase,
                moviesDatabase = moviesDatabase,
                query = params.query
            ),
            pagingSourceFactory = { moviePersistence.pagingSource(params.query) }
        ).flow
    }

    data class Params(
        val query: Query,
        val language: String
    )
}
