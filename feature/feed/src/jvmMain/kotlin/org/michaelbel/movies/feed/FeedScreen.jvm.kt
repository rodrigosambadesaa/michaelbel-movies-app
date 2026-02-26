@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.feed

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.feed.event.FeedEvent
import org.michaelbel.movies.feed.event.FeedEventManager
import org.michaelbel.movies.feed.intent.FeedIntent
import org.michaelbel.movies.feed.model.FeedModel
import org.michaelbel.movies.feed.ui.FeedEmpty
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.page.PageContent
import org.michaelbel.movies.ui.compose.page.PageFailure
import org.michaelbel.movies.ui.compose.page.PageLoading
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.ktx.clickableWithoutRipple
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.isFailure
import org.michaelbel.movies.ui.ktx.isLoading
import org.michaelbel.movies.ui.ktx.refreshThrowable
import java.net.UnknownHostException

@Composable
actual fun FeedScreen(
    viewModel: FeedViewModel
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val feedLazyListState = rememberLazyListState()
    val feedLazyGridState = rememberLazyGridState()
    val feedLazyStaggeredGridState = rememberLazyStaggeredGridState()

    FeedScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        pagingItems = pagingItems,
        feedLazyListState = feedLazyListState,
        feedLazyGridState = feedLazyGridState,
        feedLazyStaggeredGridState = feedLazyStaggeredGridState
    )

    ObserveAsEvents(
        flow = FeedEventManager.eventFlow,
        key1 = state.feedView
    ) { event ->
        when (event) {
            FeedEvent.ReselectFeed -> {
                scope.launch { feedLazyListState.animateScrollToItem(0) }
                scope.launch { feedLazyGridState.animateScrollToItem(0) }
                scope.launch { feedLazyStaggeredGridState.animateScrollToItem(0) }
            }
            else -> Unit
        }
    }

    if (state.networkStatus == NetworkStatus.Available && pagingItems.isFailure && pagingItems.refreshThrowable is UnknownHostException) {
        pagingItems.retry()
    }
}

@Composable
private fun FeedScreenContent(
    state: FeedModel,
    dispatch: (FeedIntent) -> Unit,
    pagingItems: LazyPagingItems<MoviePojo>,
    feedLazyListState: LazyListState,
    feedLazyGridState: LazyGridState,
    feedLazyStaggeredGridState: LazyStaggeredGridState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {},
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val bottomBarHeight = 64.dp
        val bottomBarPadding = 16.dp
        val feedContentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding() + 8.dp,
            end = innerPadding.calculateEndPadding(layoutDirection),
            bottom = innerPadding.calculateBottomPadding() + bottomBarHeight + bottomBarPadding
        )

        when {
            pagingItems.isLoading -> {
                PageLoading(
                    feedView = state.feedView,
                    paddingValues = feedContentPadding
                )
            }
            pagingItems.isFailure -> {
                if (pagingItems.refreshThrowable is PageEmptyException) {
                    FeedEmpty(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                } else {
                    PageFailure(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .clickableWithoutRipple(pagingItems::retry)
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
                    onMovieClick = { pagingKey, movieId -> dispatch(FeedIntent.MovieDetailsClick(pagingKey, movieId)) },
                    contentPadding = feedContentPadding,
                    modifier = Modifier
                )
            }
        }
    }
}
