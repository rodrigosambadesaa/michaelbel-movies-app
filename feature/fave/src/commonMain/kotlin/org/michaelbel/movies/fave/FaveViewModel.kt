@file:OptIn(ExperimentalCoroutinesApi::class)

package org.michaelbel.movies.fave

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.fave.intent.FaveIntent
import org.michaelbel.movies.fave.model.FaveModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.MainNavigator

class FaveViewModel(
    private val uiInteractor: UiInteractor,
    private val interactor: Interactor
): MoviesViewModel<FaveModel, FaveIntent, Event>(FaveModel()) {

    val pagingDataFlow: Flow<PagingData<MoviePojo>> = interactor.favoriteMoviesPagingData()
        .cachedIn(this)

    init {
        dispatch(FaveIntent.CollectFeedView)
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
            is FaveIntent.CollectPageFailureButtonVisible -> reduce { it.copy(isPageFailureButtonVisible = uiInteractor.isPageFailureButtonVisible) }
            is FaveIntent.MovieDetailsClick -> launch { MainNavigator.forward(DetailsDestination(intent.pagingKey, intent.movieId)) }
        }
    }
}
