@file:OptIn(ExperimentalPagingApi::class)

package org.michaelbel.movies.domain.usecase.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.domain.usecase.SearchMoviesResultUseCase
import org.michaelbel.movies.network.ktx.isEmpty
import org.michaelbel.movies.network.ktx.isPaginationReached
import org.michaelbel.movies.network.ktx.nextPage
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.repository.PagingKeyRepository

class SearchMoviesRemoteMediator(
    private val language: String,
    private val pagingKeyRepository: PagingKeyRepository,
    private val moviePersistence: MoviePersistence,
    private val searchMoviesResultUseCase: SearchMoviesResultUseCase,
    private val moviesDatabase: MoviesDatabase,
    private val query: Query
): RemoteMediator<Int, MoviePojo>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MoviePojo>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> pagingKeyRepository.prevPage(query)
                LoadType.APPEND -> pagingKeyRepository.page(query)
            } ?: return MediatorResult.Success(endOfPaginationReached = true)

            if (query.isEmpty()) {
                throw PageEmptyException()
            }

            val params = SearchMoviesResultUseCase.Params(query, language, loadKey)
            val moviesResult = searchMoviesResultUseCase(params).getOrThrow()

            moviesDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pagingKeyRepository.removePagingKey(query)
                    moviePersistence.removeMovies(query)
                }

                if (moviesResult.isEmpty) {
                    throw PageEmptyException()
                }

                pagingKeyRepository.insertPagingKey(query, moviesResult.nextPage, moviesResult.totalPages)

                val maxPosition = moviePersistence.maxPosition(query)
                val moviesDb = moviesResult.results.mapIndexed { index, movieResponse ->
                    movieResponse.moviePojo(
                        movieList = query,
                        page = moviesResult.page,
                        position = if (maxPosition == 0) index else maxPosition.plus(index).plus(1)
                    )
                }
                moviePersistence.upsert(moviesDb)
            }

            MediatorResult.Success(endOfPaginationReached = moviesResult.isPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
