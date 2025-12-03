@file:OptIn(ExperimentalCoroutinesApi::class)

package org.michaelbel.movies.search

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.MovieInteractor
import org.michaelbel.movies.network.connectivity.NetworkManager
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.search.intent.SearchIntent
import org.michaelbel.movies.search.model.SearchModel
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.MainNavigator

class SearchViewModel2(
    private val interactor: Interactor,
    private val movieInteractor: MovieInteractor,
    private val networkManager: NetworkManager
): MoviesViewModel<SearchModel, SearchIntent>(SearchModel()) {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private val query: StateFlow<String> get() = _query.asStateFlow()

    val isSearchActive: StateFlow<Boolean> = interactor.isSearchActive

    val pagingDataFlow: Flow<PagingData<MoviePojo>> = query
        .flatMapLatest(movieInteractor::moviesPagingData)
        .cachedIn(this)

    init {
        dispatch(SearchIntent.CollectSuggestions)
        dispatch(SearchIntent.CollectSearchHistoryMovies)
        dispatch(SearchIntent.CollectFeedView)
        dispatch(SearchIntent.CollectNetworkStatus)
        dispatch(SearchIntent.LoadSuggestions)
    }

    override fun dispatch(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.CollectSuggestions -> {
                launch {
                    interactor.suggestions().collectLatest { suggestions ->
                        reduce { it.copy(suggestions = suggestions) }
                    }
                }
            }
            is SearchIntent.CollectSearchHistoryMovies -> {
                launch {
                    interactor.moviesFlow(MoviePojo.MOVIES_SEARCH_HISTORY, Int.MAX_VALUE).collectLatest { searchHistoryMovies ->
                        reduce { it.copy(searchHistoryMovies = searchHistoryMovies) }
                    }
                }
            }
            is SearchIntent.CollectFeedView -> {
                launch {
                    interactor.currentFeedView.collectLatest { feedView ->
                        reduce { it.copy(feedView = feedView) }
                    }
                }
            }
            is SearchIntent.CollectNetworkStatus -> {
                launch {
                    networkManager.status.collectLatest { networkStatus ->
                        reduce { it.copy(networkStatus = networkStatus) }
                    }
                }
            }
            is SearchIntent.LoadSuggestions -> launch { interactor.updateSuggestions() }
            is SearchIntent.BackClick -> launch { MainNavigator.back() }
            is SearchIntent.ClearSearchHistoryClick -> launch { interactor.removeMovies(MoviePojo.MOVIES_SEARCH_HISTORY) }
            is SearchIntent.MovieDetailsClick -> launch { MainNavigator.forward(DetailsDestination(intent.movieList, intent.movieId)) }
            is SearchIntent.RemoveMovieFromHistoryClick -> launch { interactor.removeMovie(MoviePojo.MOVIES_SEARCH_HISTORY, intent.movieId) }
            is SearchIntent.SaveMovieToHistoryClick -> {
                launch {
                    val movie = interactor.movie(query.value, intent.movieId)
                    interactor.insertMovie(MoviePojo.MOVIES_SEARCH_HISTORY, movie)
                }
            }
            is SearchIntent.EnterSearchQuery -> { _query.value = intent.query }
            is SearchIntent.ChangeActiveState -> interactor.setSearchActive(intent.state)
        }
    }
}