package org.michaelbel.movies.search.intent

import org.michaelbel.movies.common.mvi.Intent
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

sealed interface SearchIntent: Intent {
    data object CollectSuggestions: SearchIntent
    data object CollectSearchHistoryMovies: SearchIntent
    data object CollectFeedView: SearchIntent
    data object CollectNetworkStatus: SearchIntent
    data object LoadSuggestions: SearchIntent
    data object BackClick: SearchIntent
    data object ClearSearchHistoryClick: SearchIntent
    data class MovieDetailsClick(val movieList: PagingKey, val movieId: MovieId): SearchIntent
    data class RemoveMovieFromHistoryClick(val movieId: MovieId): SearchIntent
    data class SaveMovieToHistoryClick(val movieId: MovieId): SearchIntent
    data class ShowSnackbar(val message: String, val isLong: Boolean): SearchIntent
    data class EnterSearchQuery(val query: String): SearchIntent
    data class ChangeActiveState(val state: Boolean): SearchIntent
}
