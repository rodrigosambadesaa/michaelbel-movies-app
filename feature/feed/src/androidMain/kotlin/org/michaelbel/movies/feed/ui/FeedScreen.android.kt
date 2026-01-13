@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.feed.ui

import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.feed.FeedViewModel
import org.michaelbel.movies.feed.intent.FeedIntent
import org.michaelbel.movies.feed.ktx.titleText
import org.michaelbel.movies.feed.model.FeedModel
import org.michaelbel.movies.network.config.isTmdbApiKeyEmpty
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.NotificationBottomSheet
import org.michaelbel.movies.ui.compose.page.PageContent
import org.michaelbel.movies.ui.compose.page.PageFailure
import org.michaelbel.movies.ui.compose.page.PageLoading
import org.michaelbel.movies.ui.ktx.clickableWithoutRipple
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.displayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.isFailure
import org.michaelbel.movies.ui.ktx.isLoading
import org.michaelbel.movies.ui.ktx.refreshThrowable
import org.michaelbel.movies.ui.ktx.rememberConnectivityClickHandler
import java.net.UnknownHostException
import org.michaelbel.movies.ui.R as UiR

@Composable
actual fun FeedScreen(
    viewModel: FeedViewModel
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val notificationsPermissionRequired by viewModel.notificationsPermissionRequired.collectAsStateCommon()

    FeedScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        pagingItems = pagingItems,
        notificationsPermissionRequired = notificationsPermissionRequired
    )
}

@Composable
private fun FeedScreenContent(
    state: FeedModel,
    dispatch: (FeedIntent) -> Unit,
    pagingItems: LazyPagingItems<MoviePojo>,
    notificationsPermissionRequired: Boolean
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val snackbarHostState = remember { SnackbarHostState() }

    val onScrollToTop: () -> Unit = {
        scope.launch { lazyListState.animateScrollToItem(0) }
        scope.launch { lazyGridState.animateScrollToItem(0) }
        scope.launch { lazyStaggeredGridState.animateScrollToItem(0) }
    }

    val onShowSnackbar: (String, SnackbarDuration) -> Unit = { message, snackbarDuration ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                duration = snackbarDuration
            )
        }
    }

    if (pagingItems.isFailure && pagingItems.refreshThrowable is ApiKeyNotNullException) {
        onShowSnackbar(stringResource(UiR.string.error_api_key_null), SnackbarDuration.Long)
    }

    if (state.networkStatus == NetworkStatus.Available && pagingItems.isFailure && pagingItems.refreshThrowable is UnknownHostException) {
        pagingItems.retry()
    }

    var modalDialog by remember { mutableStateOf(false) }
    modalDialog = notificationsPermissionRequired
    if (modalDialog) {
        NotificationBottomSheet(
            onDismissRequest = {
                modalDialog = false
                dispatch(FeedIntent.HideNotificationDialog)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            FeedToolbar(
                title = state.movieList.titleText,
                modifier = Modifier.clickableWithoutRipple(onScrollToTop),
                account = state.accountPojo,
                isTmdbApiKeyEmpty = isTmdbApiKeyEmpty,
                isSearchIconVisible = false,
                onSearchIconClick = { dispatch(FeedIntent.SearchClick) },
                isAuthIconVisible = true,
                onAuthIconClick = { dispatch(FeedIntent.AuthClick) },
                onAccountIconClick = { dispatch(FeedIntent.AccountClick) },
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                isSettingsIconVisible = false,
                onSettingsIconClick = { dispatch(FeedIntent.SettingsClick) }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        when {
            pagingItems.isLoading -> {
                PageLoading(
                    feedView = state.feedView,
                    modifier = Modifier.windowInsetsPadding(displayCutoutWindowInsets),
                    paddingValues = innerPadding
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
                        isButtonVisible = Build.VERSION.SDK_INT >= 29,
                        onButtonClick = rememberConnectivityClickHandler()
                    )
                }
            }
            else -> {
                PageContent(
                    feedView = state.feedView,
                    lazyListState = lazyListState,
                    lazyGridState = lazyGridState,
                    lazyStaggeredGridState = lazyStaggeredGridState,
                    pagingItems = pagingItems,
                    onMovieClick = { pagingKey, movieId -> dispatch(FeedIntent.MovieDetailsClick(pagingKey, movieId)) },
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = innerPadding.calculateBottomPadding() + 80.dp
                    ),
                    modifier = Modifier.windowInsetsPadding(displayCutoutWindowInsets)
                )
            }
        }
    }
}
