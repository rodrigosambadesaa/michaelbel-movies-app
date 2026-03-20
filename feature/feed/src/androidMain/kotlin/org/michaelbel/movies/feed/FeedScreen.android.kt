@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.feed

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.feed.event.FeedEvent
import org.michaelbel.movies.feed.event.FeedEventManager
import org.michaelbel.movies.feed.intent.FeedIntent
import org.michaelbel.movies.feed.model.FeedModel
import org.michaelbel.movies.feed.ui.FeedEmpty
import org.michaelbel.movies.feed.ui.FeedSearchBar
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.page.PageContent
import org.michaelbel.movies.ui.compose.page.PageFailure
import org.michaelbel.movies.ui.compose.page.PageLoading
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.ktx.clickableWithoutRipple
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.displayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.isFailure
import org.michaelbel.movies.ui.ktx.isLoading
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.ktx.isRefreshLoading
import org.michaelbel.movies.ui.ktx.isWideFoldableMode
import org.michaelbel.movies.ui.ktx.refreshThrowable
import org.michaelbel.movies.ui.ktx.rememberConnectivityClickHandler
import org.michaelbel.movies.ui.strings.MoviesStrings
import java.net.UnknownHostException

@Composable
actual fun FeedScreen(
    viewModel: FeedViewModel,
    onSearchActiveChange: (Boolean) -> Unit
) {
    val state by viewModel.stateFlow.collectAsStateCommon()

    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val searchPagingItems = viewModel.searchPagingDataFlow.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val feedLazyListState = rememberLazyListState()
    val feedLazyGridState = rememberLazyGridState()
    val feedLazyStaggeredGridState = rememberLazyStaggeredGridState()

    FeedScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        pagingItems = pagingItems,
        searchPagingItems = searchPagingItems,
        snackbarHostState = snackbarHostState,
        feedLazyListState = feedLazyListState,
        feedLazyGridState = feedLazyGridState,
        feedLazyStaggeredGridState = feedLazyStaggeredGridState,
        onSearchActiveChange = onSearchActiveChange
    )

    ObserveAsEvents(
        flow = viewModel.eventFlow,
        key1 = snackbarHostState
    ) { event ->
        when (event) {
            is FeedEvent.ScrollToTop -> {
                scope.launch { feedLazyListState.animateScrollToItem(0) }
                scope.launch { feedLazyGridState.animateScrollToItem(0) }
                scope.launch { feedLazyStaggeredGridState.animateScrollToItem(0) }
            }
            is FeedEvent.ShowSnackbar -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                scope.launch {
                    snackbarHostState.run {
                        showSnackbar(
                            message = event.message,
                            duration = if (event.isLong) SnackbarDuration.Long else SnackbarDuration.Short
                        )
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun FeedScreenContent(
    state: FeedModel,
    dispatch: (FeedIntent) -> Unit,
    pagingItems: LazyPagingItems<MoviePojo>,
    searchPagingItems: LazyPagingItems<MoviePojo>,
    snackbarHostState: SnackbarHostState,
    feedLazyListState: LazyListState,
    feedLazyGridState: LazyGridState,
    feedLazyStaggeredGridState: LazyStaggeredGridState,
    onSearchActiveChange: (Boolean) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var isSearchAutoFocusEnabled by rememberSaveable { mutableStateOf(true) }
    val contentBottomPadding = when {
        isSearchActive -> 16.dp
        else -> 80.dp
    }

    LaunchedEffect(isSearchActive, onSearchActiveChange) {
        onSearchActiveChange(isSearchActive)
    }

    DisposableEffect(onSearchActiveChange) {
        onDispose {
            onSearchActiveChange(false)
        }
    }

    fun clearSearchState() {
        query = ""
        searchQuery = ""
        dispatch(FeedIntent.EnterSearchQuery(searchQuery))
        isSearchActive = false
        isSearchAutoFocusEnabled = true
    }

    BackHandler(
        enabled = isSearchActive || searchQuery.isNotBlank()
    ) {
        clearSearchState()
    }

    ObserveAsEvents(
        flow = FeedEventManager.eventFlow
    ) { event ->
        when (event) {
            FeedEvent.ReselectFeed -> {
                when {
                    isSearchActive || searchQuery.isNotEmpty() -> clearSearchState()
                    else -> dispatch(FeedIntent.ScrollToTop)
                }
            }
            FeedEvent.OpenSearch -> {
                isSearchActive = true
                isSearchAutoFocusEnabled = true
            }
            else -> Unit
        }
    }

    if (pagingItems.isFailure && pagingItems.refreshThrowable is ApiKeyNotNullException) {
        dispatch(FeedIntent.ShowSnackbar(stringResource(MoviesStrings.error_api_key_null), true))
    }

    if (state.networkStatus == NetworkStatus.Available && pagingItems.isFailure && pagingItems.refreshThrowable is UnknownHostException) {
        pagingItems.retry()
    }

    val searchBarHorizontalPadding: Dp by animateDpAsState(
        targetValue = if (isSearchActive) 0.dp else 16.dp,
        label = ""
    )
    val isSearchFailure = searchPagingItems.isFailure
    val isSearchEmptyFailure = isSearchFailure && searchPagingItems.refreshThrowable is PageEmptyException

    Scaffold(
        topBar = {
            Column {
                FeedSearchBar(
                    query = query,
                    onQueryChange = { text -> query = text },
                    onSearch = { text ->
                        query = text
                        searchQuery = text
                        dispatch(FeedIntent.EnterSearchQuery(searchQuery))
                        isSearchActive = true
                        isSearchAutoFocusEnabled = true
                    },
                    active = isSearchActive,
                    isAutoFocusEnabled = isSearchAutoFocusEnabled,
                    isSearchResultsVisible = searchQuery.isNotBlank(),
                    onActiveChange = {
                        if (!it && (query.isNotBlank() || searchQuery.isNotBlank())) {
                            clearSearchState()
                        } else {
                            isSearchActive = it
                        }
                        if (it) {
                            isSearchAutoFocusEnabled = true
                        }
                    },
                    onBackClick = ::clearSearchState,
                    onCloseClick = {
                        query = ""
                        searchQuery = ""
                        dispatch(FeedIntent.EnterSearchQuery(searchQuery))
                        isSearchActive = true
                        isSearchAutoFocusEnabled = true
                    },
                    onInputText = { text ->
                        query = text
                        searchQuery = text
                        dispatch(FeedIntent.EnterSearchQuery(searchQuery))
                        isSearchActive = true
                        isSearchAutoFocusEnabled = true
                    },
                    state = state,
                    dispatch = dispatch,
                    isSearchRefreshLoading = searchPagingItems.isRefreshLoading,
                    isSearchFailure = isSearchFailure,
                    isSearchEmptyFailure = isSearchEmptyFailure,
                    onSearchRetryClick = searchPagingItems::retry,
                    searchLoadingContent = { modifier ->
                        PageLoading(
                            feedView = state.feedView,
                            modifier = modifier,
                            cardColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    searchContent = { modifier, lazyListState, lazyGridState, lazyStaggeredGridState ->
                        PageContent(
                            feedView = state.feedView,
                            lazyListState = lazyListState,
                            lazyGridState = lazyGridState,
                            lazyStaggeredGridState = lazyStaggeredGridState,
                            pagingItems = searchPagingItems,
                            onMovieClick = { _, movieId ->
                                isSearchAutoFocusEnabled = false
                                dispatch(FeedIntent.SaveMovieToSearchHistoryClick(movieId))
                                dispatch(FeedIntent.MovieDetailsClick(searchQuery, movieId))
                            },
                            modifier = modifier,
                            contentPadding = PaddingValues(bottom = contentBottomPadding),
                            cardColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = searchBarHorizontalPadding)
                        .fillMaxWidth()
                )

                if (isTmdbApiKeyEmpty) {
                    Text(
                        text = stringResource(MoviesStrings.error_api_key_null),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val isGridLayout = state.feedView is FeedView.FeedGrid || (state.feedView is FeedView.FeedList && (!isPortrait || isWideFoldableMode))
        val feedContentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding() + if (isGridLayout) 8.dp else 4.dp,
            end = innerPadding.calculateEndPadding(layoutDirection),
            bottom = innerPadding.calculateBottomPadding() + contentBottomPadding
        )
        when {
            pagingItems.isLoading -> {
                PageLoading(
                    feedView = state.feedView,
                    modifier = Modifier.windowInsetsPadding(displayCutoutWindowInsets),
                    paddingValues = feedContentPadding
                )
            }
            pagingItems.isFailure -> {
                if (pagingItems.refreshThrowable is PageEmptyException) {
                    FeedEmpty(
                        modifier = Modifier
                            .padding(innerPadding)
                            .windowInsetsPadding(displayCutoutWindowInsets)
                            .fillMaxSize()
                    )
                } else {
                    PageFailure(
                        modifier = Modifier
                            .padding(innerPadding)
                            .windowInsetsPadding(displayCutoutWindowInsets)
                            .fillMaxSize()
                            .clickableWithoutRipple(pagingItems::retry),
                        isButtonVisible = state.isPageFailureButtonVisible,
                        onButtonClick = rememberConnectivityClickHandler()
                    )
                }
            }
            else -> {
                PageContent(
                    feedView = state.feedView,
                    lazyListState = feedLazyListState,
                    lazyGridState = feedLazyGridState,
                    lazyStaggeredGridState = feedLazyStaggeredGridState,
                    pagingItems = pagingItems,
                    onMovieClick = { pagingKey, movieId ->
                        dispatch(FeedIntent.MovieDetailsClick(pagingKey, movieId))
                    },
                    contentPadding = feedContentPadding,
                    modifier = Modifier.windowInsetsPadding(displayCutoutWindowInsets)
                )
            }
        }
    }
}
