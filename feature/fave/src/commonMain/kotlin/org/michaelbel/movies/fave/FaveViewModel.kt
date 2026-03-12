@file:OptIn(ExperimentalCoroutinesApi::class)

package org.michaelbel.movies.fave

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.fave.intent.FaveIntent
import org.michaelbel.movies.fave.model.FaveModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.network.connectivity.NetworkManager
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.MainNavigator

class FaveViewModel(
    private val uiInteractor: UiInteractor,
    private val interactor: Interactor,
    private val networkManager: NetworkManager
): MoviesViewModel<FaveModel, FaveIntent, Event>(FaveModel()) {

    val pagingDataFlow: Flow<PagingData<MoviePojo>> = interactor.favoriteMoviesPagingData()
        .cachedIn(this)

    val moviesFlow: StateFlow<List<MoviePojo>> = interactor.moviesFlow(Movie.FAVORITE, MovieResponse.DEFAULT_PAGE_SIZE)
        .catch { emit(emptyList()) }
        .stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    init {
        dispatch(FaveIntent.CollectFeedView)
        dispatch(FaveIntent.CollectNetworkStatus)
        dispatch(FaveIntent.CollectPageFailureButtonVisible)
        launch {
            pagingDataFlow.collect {}
        }
    }

    override fun dispatch(intent: FaveIntent) {
        when (intent) {
            is FaveIntent.CollectFeedView -> {
                launch {
                    interactor.currentFeedView.collectLatest { feedView ->
                        reduce { it.copy(feedView = feedView) }
                    }
                }
            }
            is FaveIntent.CollectNetworkStatus -> {
                launch {
                    networkManager.status.collectLatest { networkStatus ->
                        reduce { it.copy(networkStatus = networkStatus) }
                    }
                }
            }
            is FaveIntent.CollectPageFailureButtonVisible -> reduce { it.copy(isPageFailureButtonVisible = uiInteractor.isPageFailureButtonVisible) }
            is FaveIntent.RefreshFavorites -> {
                if (stateFlow.value.isFeedLoading) return
                launch {
                    reduce { it.copy(isFeedLoading = true) }
                    try {
                        interactor.fetchAndInsertMovies(Movie.FAVORITE)
                    } finally {
                        reduce { it.copy(isFeedLoading = false) }
                    }
                }
            }
            is FaveIntent.MovieDetailsClick -> launch { MainNavigator.forward(DetailsDestination(intent.pagingKey, intent.movieId)) }
        }
    }
}
