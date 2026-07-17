@file:OptIn(ExperimentalPagingApi::class)

package org.michaelbel.movies.domain.usecase.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.domain.usecase.MoviesResultUseCase
import org.michaelbel.movies.network.ktx.isEmpty
import org.michaelbel.movies.network.ktx.isPaginationReached
import org.michaelbel.movies.network.ktx.nextPage
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.repository.PagingKeyRepository

class FeedMoviesRemoteMediator(
    private val language: String,
    private val pagingKeyRepository: PagingKeyRepository,
    private val moviesResultUseCase: MoviesResultUseCase,
    private val moviePersistence: MoviePersistence,
    private val moviesDatabase: MoviesDatabase,
    private val pagingKey: PagingKey
): RemoteMediator<Int, MoviePojo>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MoviePojo>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> pagingKeyRepository.prevPage(pagingKey)
                LoadType.APPEND -> pagingKeyRepository.page(pagingKey)
            } ?: return MediatorResult.Success(endOfPaginationReached = true)

            val params = MoviesResultUseCase.Params(pagingKey, language, loadKey)
            val moviesResult = moviesResultUseCase(params).getOrThrow()

            moviesDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pagingKeyRepository.removePagingKey(pagingKey)
                    moviePersistence.removeMovies(pagingKey)
                }

                if (moviesResult.isEmpty) {
                    throw PageEmptyException()
                }

                pagingKeyRepository.insertPagingKey(pagingKey, moviesResult.nextPage, moviesResult.totalPages)

                val maxPosition = moviePersistence.maxPosition(pagingKey)
                val moviesDb = moviesResult.results.mapIndexed { index, movieResponse ->
                    movieResponse.moviePojo(
                        movieList = pagingKey,
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
