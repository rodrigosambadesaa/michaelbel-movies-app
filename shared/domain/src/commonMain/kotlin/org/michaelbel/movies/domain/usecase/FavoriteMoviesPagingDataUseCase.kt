@file:OptIn(ExperimentalPagingApi::class)
@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.domain.usecase.remote.FeedMoviesRemoteMediator
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.PagingKeyPersistence
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo

class FavoriteMoviesPagingDataUseCase(
    private val moviePersistence: MoviePersistence,
    private val pagingKeyPersistence: PagingKeyPersistence,
    private val pagingKeyPageUseCase: PagingKeyPageUseCase,
    private val pagingKeyPrevPageUseCase: PagingKeyPrevPageUseCase,
    private val moviesResultUseCase: MoviesResultUseCase,
    private val moviesDatabase: MoviesDatabase,
    dispatchers: SharedDispatchers
): FlowUseCase<String, PagingData<MoviePojo>>(dispatchers.io) {

    override fun execute(language: String): Flow<PagingData<MoviePojo>> {
        return Pager(
            config = PagingConfig(
                pageSize = MovieResponse.DEFAULT_PAGE_SIZE,
                enablePlaceholders = true
            ),
            remoteMediator = FeedMoviesRemoteMediator(
                language = language,
                pagingKeyPageUseCase = pagingKeyPageUseCase,
                pagingKeyPrevPageUseCase = pagingKeyPrevPageUseCase,
                moviesResultUseCase = moviesResultUseCase,
                moviePersistence = moviePersistence,
                pagingKeyPersistence = pagingKeyPersistence,
                moviesDatabase = moviesDatabase,
                pagingKey = Movie.FAVORITE
            ),
            pagingSourceFactory = { moviePersistence.pagingSource(Movie.FAVORITE) }
        ).flow
    }
}
