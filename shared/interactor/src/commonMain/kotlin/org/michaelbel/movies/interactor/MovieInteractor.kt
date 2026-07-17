package org.michaelbel.movies.interactor

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.persistence.database.typealiases.Query

interface MovieInteractor {

    fun moviesPagingData(movieList: MovieList): Flow<PagingData<MoviePojo>>

    fun favoriteMoviesPagingData(): Flow<PagingData<MoviePojo>>

    fun moviesPagingData(searchQuery: Query): Flow<PagingData<MoviePojo>>

    suspend fun removeMovies(pagingKey: PagingKey)

    suspend fun removeMovie(pagingKey: PagingKey, movieId: MovieId)

    suspend fun insertMovie(pagingKey: PagingKey, movie: MoviePojo)

    suspend fun moviesResult(pagingKey: PagingKey): List<MoviePojo>
}
