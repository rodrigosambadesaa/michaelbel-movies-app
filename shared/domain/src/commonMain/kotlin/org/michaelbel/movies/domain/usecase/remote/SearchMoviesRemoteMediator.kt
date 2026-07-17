@file:OptIn(ExperimentalPagingApi::class)

package org.michaelbel.movies.domain.usecase.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.domain.usecase.PagingKeyPageUseCase
import org.michaelbel.movies.domain.usecase.PagingKeyPrevPageUseCase
import org.michaelbel.movies.domain.usecase.SearchMoviesResultUseCase
import org.michaelbel.movies.network.ktx.isEmpty
import org.michaelbel.movies.network.ktx.isPaginationReached
import org.michaelbel.movies.network.ktx.nextPage
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.PagingKeyPersistence
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.entity.pojo.PagingKeyPojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.Query

class SearchMoviesRemoteMediator(
    private val language: String,
    private val pagingKeyPageUseCase: PagingKeyPageUseCase,
    private val pagingKeyPrevPageUseCase: PagingKeyPrevPageUseCase,
    private val moviePersistence: MoviePersistence,
    private val pagingKeyPersistence: PagingKeyPersistence,
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
                LoadType.PREPEND -> pagingKeyPrevPageUseCase(query).getOrThrow()
                LoadType.APPEND -> pagingKeyPageUseCase(query).getOrThrow()
            } ?: return MediatorResult.Success(endOfPaginationReached = true)

            if (query.isEmpty()) {
                throw PageEmptyException()
            }

            val params = SearchMoviesResultUseCase.Params(query, language, loadKey)
            val moviesResult = searchMoviesResultUseCase(params).getOrThrow()

            moviesDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pagingKeyPersistence.removePagingKey(query)
                    moviePersistence.removeMovies(query)
                }

                if (moviesResult.isEmpty) {
                    throw PageEmptyException()
                }

                pagingKeyPersistence.upsertPagingKey(
                    PagingKeyPojo(
                        pagingKey = query,
                        page = moviesResult.nextPage,
                        totalPages = moviesResult.totalPages
                    )
                )

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
