@file:OptIn(ExperimentalCoroutinesApi::class)

package org.michaelbel.movies.feed

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.feed.intent.FeedIntent
import org.michaelbel.movies.feed.model.FeedModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.ktx.nameOrLocalList
import org.michaelbel.movies.network.connectivity.NetworkManager
import org.michaelbel.movies.notifications.NotificationClient
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.navigation.AccountDestination
import org.michaelbel.movies.ui.navigation.AuthDestination
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.navigation.SearchDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination

class FeedViewModel(
    private val interactor: Interactor,
    private val notificationClient: NotificationClient,
    private val networkManager: NetworkManager
): MoviesViewModel<FeedModel, FeedIntent>(FeedModel()) {

    private val currentMovieList: StateFlow<MovieList> = interactor.currentMovieList
        .stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = runBlocking { interactor.currentMovieList.first() }
        )

    val pagingDataFlow: Flow<PagingData<MoviePojo>> = currentMovieList
        .flatMapLatest { movieList -> interactor.moviesPagingData(movieList) }
        .cachedIn(this)

    val pagingDataFlow2: StateFlow<List<MoviePojo>> = currentMovieList.flatMapLatest { movieList ->
        flowOf(interactor.moviesResult(movieList.nameOrLocalList))
    }.catch {
        emptyList<List<MoviePojo>>()
    }.stateIn(
        scope = this,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    private var _notificationsPermissionRequired: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val notificationsPermissionRequired: StateFlow<Boolean> get() = _notificationsPermissionRequired.asStateFlow()

    init {
        dispatch(FeedIntent.CollectAccountPojo)
        dispatch(FeedIntent.CollectFeedView)
        dispatch(FeedIntent.CollectMovieList)
        dispatch(FeedIntent.CollectNetworkStatus)
        dispatch(FeedIntent.SubscribeNotificationsPermissionRequired)
    }

    override fun dispatch(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.CollectAccountPojo -> {
                launch {
                    interactor.accountPojoFlow.collectLatest { accountPojo ->
                        reduce { it.copy(accountPojo = accountPojo) }
                    }
                }
            }
            is FeedIntent.CollectFeedView -> {
                launch {
                    interactor.currentFeedView.collectLatest { feedView ->
                        reduce { it.copy(feedView = feedView) }
                    }
                }
            }
            is FeedIntent.CollectMovieList -> {
                launch {
                    interactor.currentMovieList.collectLatest { movieList ->
                        reduce { it.copy(movieList = movieList) }
                    }
                }
            }
            is FeedIntent.CollectNetworkStatus -> {
                launch {
                    networkManager.status.collectLatest { networkStatus ->
                        reduce { it.copy(networkStatus = networkStatus) }
                    }
                }
            }
            is FeedIntent.HideNotificationDialog -> {
                launch {
                    _notificationsPermissionRequired.tryEmit(false)
                    notificationClient.updateNotificationExpireTime()
                }
            }
            is FeedIntent.SubscribeNotificationsPermissionRequired -> {
                launch {
                    _notificationsPermissionRequired.tryEmit(notificationClient.notificationsPermissionRequired(NOTIFICATIONS_PERMISSION_DELAY))
                }
            }
            is FeedIntent.SettingsClick -> launch { MainNavigator.forward(SettingsDestination) }
            is FeedIntent.SearchClick -> launch { MainNavigator.forward(SearchDestination) }
            is FeedIntent.AuthClick -> launch { MainNavigator.forward(AuthDestination) }
            is FeedIntent.AccountClick -> launch { MainNavigator.forward(AccountDestination) }
            is FeedIntent.ScrollToTop -> launch { push(intent) }
            is FeedIntent.ShowSnackbar -> launch { push(intent) }
            is FeedIntent.MovieDetailsClick -> launch { MainNavigator.forward(DetailsDestination(intent.pagingKey, intent.movieId)) }
        }
    }

    private companion object {
        private const val NOTIFICATIONS_PERMISSION_DELAY = 2_000L
    }
}
