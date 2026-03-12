package org.michaelbel.movies.details

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.exceptions.MovieDetailsException
import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.details.intent.DetailsIntent
import org.michaelbel.movies.details.model.DetailsModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.network.connectivity.NetworkManager
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.persistence.database.ktx.isNotEmpty
import org.michaelbel.movies.ui.navigation.AuthDestination
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.GalleryDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.pending.PendingAction
import org.michaelbel.movies.ui.pending.PendingActionStore

class DetailsViewModel(
    private val destination: DetailsDestination,
    private val interactor: Interactor,
    private val uiInteractor: UiInteractor,
    private val networkManager: NetworkManager
): MoviesViewModel<DetailsModel, DetailsIntent, Event>(DetailsModel()) {

    init {
        dispatch(DetailsIntent.CollectAppTheme)
        dispatch(DetailsIntent.CollectNetworkStatus)
        dispatch(DetailsIntent.CollectFeatureFlags)
        dispatch(DetailsIntent.CollectAccount)
        dispatch(DetailsIntent.CollectFavorite)
        dispatch(DetailsIntent.CollectMovieDb)
        dispatch(DetailsIntent.LoadMovie)
    }

    override fun dispatch(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.CollectAppTheme -> {
                launch {
                    interactor.currentTheme.collectLatest { appTheme ->
                        reduce { it.copy(appTheme = appTheme) }
                    }
                }
            }
            is DetailsIntent.CollectNetworkStatus -> {
                launch {
                    networkManager.status.collectLatest { networkStatus ->
                        reduce { it.copy(networkStatus = networkStatus) }
                    }
                }
            }
            is DetailsIntent.CollectFeatureFlags -> {
                reduce {
                    it.copy(
                        isDetailsGalleryFeatureEnabled = uiInteractor.isDetailsGalleryFeatureEnabled,
                        isDetailsShareFeatureEnabled = uiInteractor.isDetailsShareFeatureEnabled
                    )
                }
            }
            is DetailsIntent.CollectAccount -> {
                launch {
                    interactor.accountPojoFlow.collectLatest { accountPojo ->
                        reduce { it.copy(isAuthorized = accountPojo.isNotEmpty) }
                    }
                }
            }
            is DetailsIntent.CollectFavorite -> {
                launch {
                    interactor.movieFlow(Movie.FAVORITE, destination.movieId).collectLatest { favoriteMovie ->
                        reduce { it.copy(isFavorite = favoriteMovie != null) }
                    }
                }
            }
            is DetailsIntent.CollectMovieDb -> {
                launch {
                    interactor.movieFlow(destination.movieList.orEmpty(), destination.movieId).collectLatest { movieDb ->
                        if (movieDb != null) {
                            reduce { it.copy(detailsState = ScreenState.Content(movieDb)) }
                        }
                    }
                }
            }
            is DetailsIntent.LoadMovie -> launch { interactor.movieDetails(destination.movieList.orEmpty(), destination.movieId) }
            is DetailsIntent.BackClick -> launch { MainNavigator.back() }
            is DetailsIntent.GalleryClick -> launch { MainNavigator.forward(GalleryDestination(destination.movieId)) }
            is DetailsIntent.FavoriteClick -> when {
                !stateFlow.value.isAuthorized -> {
                    PendingActionStore.set(PendingAction.AddFavorite(destination.movieId))
                    launch { MainNavigator.forward(AuthDestination) }
                }
                !stateFlow.value.isFavoriteJobActive -> {
                    val job = launch { interactor.updateFavorite(destination.movieId, favorite = !stateFlow.value.isFavorite) }.also { launchedJob ->
                        launchedJob.invokeOnCompletion { reduce { it.copy(favoriteJob = null) } }
                    }
                    reduce { it.copy(favoriteJob = job) }
                }
            }
            is DetailsIntent.GenerateColors -> {
                launch {
                    if (intent.containerColor != null && intent.onContainerColor != null) {
                        interactor.updateMovieColors(destination.movieId, intent.containerColor, intent.onContainerColor)
                        if (destination.movieList != null) {
                            val moviePojo = interactor.movie(destination.movieList.orEmpty(), destination.movieId)
                            reduce { it.copy(detailsState = ScreenState.Content(moviePojo)) }
                        }
                    }
                }
            }
        }
    }

    override fun catch(throwable: Throwable) {
        when (throwable) {
            is MovieDetailsException -> reduce { it.copy(detailsState = ScreenState.Failure(throwable)) }
            else -> super.catch(throwable)
        }
    }
}
