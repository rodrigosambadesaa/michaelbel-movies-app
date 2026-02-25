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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.log.log
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.feed.event.FeedEvent
import org.michaelbel.movies.feed.intent.FeedIntent
import org.michaelbel.movies.feed.model.FeedModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.ktx.nameOrLocalList
import org.michaelbel.movies.network.connectivity.NetworkManager
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.notifications.NotificationClient
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.navigation.AccountDestination
import org.michaelbel.movies.ui.navigation.AuthDestination
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.navigation.SettingsDestination

class FeedViewModel(
    private val interactor: Interactor,
    private val notificationClient: NotificationClient,
    private val networkManager: NetworkManager
): MoviesViewModel<FeedModel, FeedIntent, FeedEvent>(FeedModel()) {

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    private val searchQuery: StateFlow<String> get() = _searchQuery.asStateFlow()

    private val currentMovieList: StateFlow<MovieList> = interactor.currentMovieList
        .stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = runBlocking { interactor.currentMovieList.first() }
        )

    val pagingDataFlow: Flow<PagingData<MoviePojo>> = currentMovieList
        .flatMapLatest { movieList -> interactor.moviesPagingData(movieList) }
        .cachedIn(this)

    val searchPagingDataFlow: Flow<PagingData<MoviePojo>> = searchQuery
        .flatMapLatest(interactor::moviesPagingData)
        .cachedIn(this)

    val moviesFlow: StateFlow<List<MoviePojo>> = currentMovieList.flatMapLatest { movieList ->
        interactor.moviesFlow(movieList.nameOrLocalList, MovieResponse.DEFAULT_PAGE_SIZE)
    }.catch {
        emit(emptyList())
    }.stateIn(
        scope = this,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val pagingDataFlow2: StateFlow<List<MoviePojo>> = moviesFlow

    init {
        dispatch(FeedIntent.CollectAccountPojo)
        dispatch(FeedIntent.CollectFeedView)
        dispatch(FeedIntent.CollectMovieList)
        dispatch(FeedIntent.CollectNetworkStatus)
        dispatch(FeedIntent.CollectSuggestions)
        dispatch(FeedIntent.CollectSearchHistoryMovies)
        dispatch(FeedIntent.LoadSuggestions)
        dispatch(FeedIntent.SubscribeNotificationsPermissionRequired)
        launch {
            pagingDataFlow.collect {}
        }
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
            is FeedIntent.RefreshMovies -> { // TODO Fallback iOS
                if (stateFlow.value.isFeedLoading) return
                launch {
                    val movieList = currentMovieList.value
                    val pagingKey = movieList.nameOrLocalList
                    reduce { it.copy(isFeedLoading = true) }
                    try {
                        val movies = interactor.fetchAndInsertMovies(pagingKey)
                        log("Feed refresh loaded ${movies.size} movies for $pagingKey")
                        reduce { it.copy(isFeedLoading = false) }
                    } catch (throwable: Throwable) {
                        log(throwable)
                        reduce {
                            it.copy(
                                isFeedLoading = false
                            )
                        }
                    }
                }
            }
            is FeedIntent.CollectSuggestions -> {
                launch {
                    interactor.suggestions().collectLatest { suggestions ->
                        reduce { it.copy(suggestions = suggestions) }
                    }
                }
            }
            is FeedIntent.CollectSearchHistoryMovies -> {
                launch {
                    interactor.moviesFlow(MoviePojo.MOVIES_SEARCH_HISTORY, Int.MAX_VALUE).collectLatest { searchHistoryMovies ->
                        reduce { it.copy(searchHistoryMovies = searchHistoryMovies) }
                    }
                }
            }
            is FeedIntent.LoadSuggestions -> launch { interactor.updateSuggestions() }
            is FeedIntent.HideNotificationDialog -> {
                launch {
                    reduce { it.copy(isNotificationDialogVisible = false) }
                    notificationClient.updateNotificationExpireTime()
                }
            }
            is FeedIntent.SubscribeNotificationsPermissionRequired -> {
                launch {
                    val required = notificationClient.notificationsPermissionRequired(NOTIFICATIONS_PERMISSION_DELAY)
                    reduce { it.copy(isNotificationDialogVisible = required) }
                }
            }
            is FeedIntent.SettingsClick -> launch { MainNavigator.forward(SettingsDestination) }
            is FeedIntent.AuthClick -> launch { MainNavigator.forward(AuthDestination) }
            is FeedIntent.AccountClick -> launch { MainNavigator.forward(AccountDestination) }
            is FeedIntent.ClearSearchHistoryClick -> launch { interactor.removeMovies(MoviePojo.MOVIES_SEARCH_HISTORY) }
            is FeedIntent.RemoveMovieFromHistoryClick -> launch { interactor.removeMovie(MoviePojo.MOVIES_SEARCH_HISTORY, intent.movieId) }
            is FeedIntent.SaveMovieToSearchHistoryClick -> {
                launch {
                    val movie = interactor.movie(searchQuery.value, intent.movieId)
                    interactor.insertMovie(MoviePojo.MOVIES_SEARCH_HISTORY, movie)
                }
            }
            is FeedIntent.EnterSearchQuery -> { _searchQuery.value = intent.query }
            is FeedIntent.ScrollToTop -> launch { push(FeedEvent.ScrollToTop) }
            is FeedIntent.ShowSnackbar -> launch { push(FeedEvent.ShowSnackbar(intent.message, intent.isLong)) }
            is FeedIntent.MovieDetailsClick -> launch { MainNavigator.forward(DetailsDestination(intent.pagingKey, intent.movieId)) }
        }
    }

    private companion object {
        private const val NOTIFICATIONS_PERMISSION_DELAY = 2_000L
    }
}
