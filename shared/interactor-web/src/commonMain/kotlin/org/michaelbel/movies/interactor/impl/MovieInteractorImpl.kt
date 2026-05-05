package org.michaelbel.movies.interactor.impl

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.michaelbel.movies.common.dispatchers.MoviesDispatchers
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.interactor.LocaleInteractor
import org.michaelbel.movies.interactor.MovieInteractor
import org.michaelbel.movies.interactor.ktx.nameOrLocalList
import org.michaelbel.movies.network.ktx.isEmpty
import org.michaelbel.movies.network.ktx.nextPage
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.persistence.database.entity.mini.MovieDbMini
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.Limit
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.repository.MovieRepository
import org.michaelbel.movies.repository.PagingKeyRepository
import org.michaelbel.movies.repository.SearchRepository

class MovieInteractorImpl(
    private val dispatchers: MoviesDispatchers,
    private val localeInteractor: LocaleInteractor,
    private val movieRepository: MovieRepository,
    private val searchRepository: SearchRepository,
    private val pagingKeyRepository: PagingKeyRepository
): MovieInteractor {

    override fun moviesPagingData(movieList: MovieList): Flow<PagingData<MoviePojo>> {
        return pagingDataFlow(movieList.nameOrLocalList)
    }

    override fun favoriteMoviesPagingData(): Flow<PagingData<MoviePojo>> {
        return pagingDataFlow(Movie.FAVORITE)
    }

    override fun moviesPagingData(searchQuery: Query): Flow<PagingData<MoviePojo>> {
        return flow {
            when (searchQuery.isEmpty()) {
                true -> emit(PagingData.from(emptyList()))
                false -> {
                    val movies = fetchAndStoreSearchMovies(searchQuery)
                    emit(PagingData.from(movies))
                }
            }
        }
    }

    override fun movieFlow(pagingKey: PagingKey, movieId: MovieId): Flow<MoviePojo?> {
        return movieRepository.movieFlow(pagingKey, movieId)
    }

    override fun moviesFlow(pagingKey: PagingKey, limit: Limit): Flow<List<MoviePojo>> {
        return movieRepository.moviesFlow(pagingKey, limit)
    }

    override suspend fun moviesWidget(): List<MovieDbMini> {
        return withContext(dispatchers.io) { movieRepository.moviesWidget(localeInteractor.language) }
    }

    override suspend fun movie(pagingKey: PagingKey, movieId: MovieId): MoviePojo {
        return withContext(dispatchers.io) { movieRepository.movie(pagingKey, movieId) }
    }

    override suspend fun movieDetails(pagingKey: PagingKey, movieId: MovieId): MoviePojo {
        return withContext(dispatchers.io) { movieRepository.movieDetails(pagingKey, localeInteractor.language, movieId) }
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

    override suspend fun updateFavorite(movieId: MovieId, favorite: Boolean) {
        return withContext(dispatchers.io) { movieRepository.updateFavorite(movieId, favorite) }
    }

    override suspend fun fetchAndInsertSearchMovies(query: Query) {
        return withContext(dispatchers.io) {
            if (query.isEmpty()) throw PageEmptyException()

            fetchAndStoreSearchMovies(query)
        }
    }

    override suspend fun moviesResult(pagingKey: PagingKey): List<MoviePojo> {
        return withContext(dispatchers.io) {
            val movieResult = movieRepository.moviesResult(pagingKey, localeInteractor.language, 1).results
            movieResult.mapIndexed { index, movieResponse -> movieResponse.moviePojo(pagingKey, index, 1) }
        }
    }

    private fun pagingDataFlow(pagingKey: PagingKey): Flow<PagingData<MoviePojo>> {
        return flow {
            val movies = fetchAndStoreMovies(pagingKey)
            emit(PagingData.from(movies))
        }
    }

    private suspend fun fetchAndStoreMovies(pagingKey: PagingKey): List<MoviePojo> {
        val moviesResult = movieRepository.moviesResult(pagingKey, localeInteractor.language, 1)
        movieRepository.removeMovies(pagingKey)
        pagingKeyRepository.removePagingKey(pagingKey)
        pagingKeyRepository.insertPagingKey(pagingKey, moviesResult.nextPage, moviesResult.totalPages)
        movieRepository.insertMovies(pagingKey, moviesResult.page, moviesResult.results)
        return movieRepository.moviesFlow(pagingKey, Int.MAX_VALUE).first()
    }

    private suspend fun fetchAndStoreSearchMovies(query: Query): List<MoviePojo> {
        val moviesResult = searchRepository.searchMoviesResult(query, localeInteractor.language, 1)
        movieRepository.removeMovies(query)
        pagingKeyRepository.removePagingKey(query)

        if (moviesResult.isEmpty) {
            throw PageEmptyException()
        }

        pagingKeyRepository.insertPagingKey(query, moviesResult.nextPage, moviesResult.totalPages)
        movieRepository.insertMovies(query, moviesResult.page, moviesResult.results)
        return movieRepository.moviesFlow(query, Int.MAX_VALUE).first()
    }
}
