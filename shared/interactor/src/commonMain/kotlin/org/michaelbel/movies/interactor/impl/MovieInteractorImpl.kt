@file:OptIn(ExperimentalPagingApi::class)

package org.michaelbel.movies.interactor.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.interactor.LocaleInteractor
import org.michaelbel.movies.interactor.MovieInteractor
import org.michaelbel.movies.interactor.ktx.nameOrLocalList
import org.michaelbel.movies.interactor.remote.FeedMoviesRemoteMediator
import org.michaelbel.movies.interactor.remote.SearchMoviesRemoteMediator
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.repository.MovieRepository
import org.michaelbel.movies.repository.PagingKeyRepository
import org.michaelbel.movies.repository.SearchRepository

class MovieInteractorImpl(
    private val dispatchers: SharedDispatchers,
    private val localeInteractor: LocaleInteractor,
    private val movieRepository: MovieRepository,
    private val searchRepository: SearchRepository,
    private val pagingKeyRepository: PagingKeyRepository,
    private val moviesDatabase: MoviesDatabase
): MovieInteractor {

    override fun moviesPagingData(movieList: MovieList): Flow<PagingData<MoviePojo>> {
        return Pager(
            config = PagingConfig(
                pageSize = MovieResponse.DEFAULT_PAGE_SIZE,
                enablePlaceholders = true
            ),
            remoteMediator = FeedMoviesRemoteMediator(
                localeInteractor = localeInteractor,
                movieRepository = movieRepository,
                pagingKeyRepository = pagingKeyRepository,
                moviesDatabase = moviesDatabase,
                pagingKey = MovieList.name(movieList)
            ),
            pagingSourceFactory = { movieRepository.moviesPagingSource(movieList.nameOrLocalList) }
        ).flow
    }

    override fun favoriteMoviesPagingData(): Flow<PagingData<MoviePojo>> {
        return Pager(
            config = PagingConfig(
                pageSize = MovieResponse.DEFAULT_PAGE_SIZE,
                enablePlaceholders = true
            ),
            remoteMediator = FeedMoviesRemoteMediator(
                localeInteractor = localeInteractor,
                movieRepository = movieRepository,
                pagingKeyRepository = pagingKeyRepository,
                moviesDatabase = moviesDatabase,
                pagingKey = Movie.FAVORITE
            ),
            pagingSourceFactory = { movieRepository.moviesPagingSource(Movie.FAVORITE) }
        ).flow
    }

    override fun moviesPagingData(searchQuery: Query): Flow<PagingData<MoviePojo>> {
        return Pager(
            config = PagingConfig(
                pageSize = MovieResponse.DEFAULT_PAGE_SIZE,
                enablePlaceholders = true
            ),
            remoteMediator = SearchMoviesRemoteMediator(
                localeInteractor = localeInteractor,
                pagingKeyRepository = pagingKeyRepository,
                searchRepository = searchRepository,
                movieRepository = movieRepository,
                moviesDatabase = moviesDatabase,
                query = searchQuery
            ),
            pagingSourceFactory = { movieRepository.moviesPagingSource(searchQuery) }
        ).flow
    }

    override suspend fun removeMovies(pagingKey: PagingKey) {
        return withContext(dispatchers.io) { movieRepository.removeMovies(pagingKey) }
    }

    override suspend fun removeMovie(pagingKey: PagingKey, movieId: MovieId) {
        return withContext(dispatchers.io) { movieRepository.removeMovie(pagingKey, movieId) }
    }

    override suspend fun insertMovie(pagingKey: PagingKey, movie: MoviePojo) {
        return withContext(dispatchers.io) { movieRepository.insertMovie(pagingKey, movie) }
    }

    override suspend fun moviesResult(pagingKey: PagingKey): List<MoviePojo> {
        return withContext(dispatchers.io) {
            val movieResult = movieRepository.moviesResult(pagingKey, localeInteractor.language, 1).results
            movieResult.mapIndexed { index, movieResponse -> movieResponse.moviePojo(pagingKey, index, 1) }
        }
    }
}
