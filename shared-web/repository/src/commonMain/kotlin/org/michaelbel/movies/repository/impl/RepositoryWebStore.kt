package org.michaelbel.movies.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.persistence.database.entity.mini.MovieDbMini
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.entity.pojo.ImagePojo
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.entity.pojo.PagingKeyPojo
import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo
import org.michaelbel.movies.persistence.database.typealiases.Limit
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

class RepositoryWebStore {

    private val accountState = MutableStateFlow(AccountPojo.Empty)
    private val imagesState = MutableStateFlow<Map<MovieId, List<ImagePojo>>>(emptyMap())
    private val moviesState = MutableStateFlow<Map<PagingKey, List<MoviePojo>>>(emptyMap())
    private val pagingKeysState = MutableStateFlow<Map<PagingKey, PagingKeyPojo>>(emptyMap())
    private val suggestionsState = MutableStateFlow<List<SuggestionPojo>>(emptyList())

    fun accountFlow(): Flow<AccountPojo> {
        return accountState
    }

    fun updateAccount(account: AccountPojo) {
        accountState.value = account
    }

    fun clearAccount() {
        accountState.value = AccountPojo.Empty
    }

    fun imagesFlow(movieId: MovieId): Flow<List<ImagePojo>> {
        return imagesState.map { images ->
            images[movieId].orEmpty().sortedBy(ImagePojo::position)
        }
    }

    fun updateImages(movieId: MovieId, images: List<ImagePojo>) {
        imagesState.value += (movieId to images.sortedBy(ImagePojo::position))
    }

    fun movieFlow(pagingKey: PagingKey, movieId: MovieId): Flow<MoviePojo?> {
        return moviesState.map { movies ->
            movies[pagingKey].orEmpty().firstOrNull { movie -> movie.movieId == movieId }
        }
    }

    fun moviesFlow(pagingKey: PagingKey, limit: Limit): Flow<List<MoviePojo>> {
        return moviesState.map { movies ->
            val items = movies[pagingKey].orEmpty().sortedBy(MoviePojo::position)
            when {
                limit > 0 -> items.take(limit)
                else -> items
            }
        }
    }

    fun movieById(pagingKey: PagingKey, movieId: MovieId): MoviePojo? {
        return moviesState.value[pagingKey].orEmpty().firstOrNull { movie -> movie.movieId == movieId }
    }

    fun movieById(movieId: MovieId): MoviePojo? {
        return moviesState.value.values
            .asSequence()
            .flatten()
            .firstOrNull { movie -> movie.movieId == movieId }
    }

    fun movies(pagingKey: PagingKey, limit: Limit): List<MoviePojo> {
        return moviesState.value[pagingKey]
            .orEmpty()
            .sortedBy(MoviePojo::position)
            .let { movies ->
                when {
                    limit > 0 -> movies.take(limit)
                    else -> movies
                }
            }
    }

    fun moviesMini(pagingKey: PagingKey, limit: Limit): List<MovieDbMini> {
        return movies(pagingKey, limit).map { movie ->
            MovieDbMini(
                movieList = movie.movieList,
                movieId = movie.movieId,
                title = movie.title
            )
        }
    }

    fun isEmpty(pagingKey: PagingKey): Boolean {
        return moviesState.value[pagingKey].isNullOrEmpty()
    }

    fun maxPosition(pagingKey: PagingKey): Int {
        return moviesState.value[pagingKey].orEmpty().maxOfOrNull(MoviePojo::position) ?: 0
    }

    fun removeMovies(pagingKey: PagingKey) {
        moviesState.value -= pagingKey
    }

    fun removeMovie(pagingKey: PagingKey, movieId: MovieId) {
        val updatedMovies = moviesState.value[pagingKey].orEmpty().filterNot { movie -> movie.movieId == movieId }
        moviesState.value += (pagingKey to updatedMovies)
    }

    fun upsertMovies(movies: List<MoviePojo>) {
        if (movies.isEmpty()) return

        val updatedState = moviesState.value.toMutableMap()
        movies.groupBy(MoviePojo::movieList).forEach { (pagingKey, newMovies) ->
            val currentMovies = updatedState[pagingKey].orEmpty()
            val mergedMovies = currentMovies
                .associateBy(MoviePojo::movieId)
                .toMutableMap()
                .apply {
                    newMovies.forEach { movie ->
                        this[movie.movieId] = movie
                    }
                }
                .values
                .sortedBy(MoviePojo::position)
            updatedState[pagingKey] = mergedMovies
        }

        moviesState.value = updatedState
    }

    fun pagingKey(pagingKey: PagingKey): PagingKeyPojo? {
        return pagingKeysState.value[pagingKey]
    }

    fun updatePagingKey(pagingKeyPojo: PagingKeyPojo) {
        pagingKeysState.value += (pagingKeyPojo.pagingKey to pagingKeyPojo)
    }

    fun removePagingKey(pagingKey: PagingKey) {
        pagingKeysState.value -= pagingKey
    }

    fun suggestionsFlow(): Flow<List<SuggestionPojo>> {
        return suggestionsState
    }

    fun updateSuggestions(suggestions: List<SuggestionPojo>) {
        suggestionsState.value = suggestions
    }

    fun clearSuggestions() {
        suggestionsState.value = emptyList()
    }
}
