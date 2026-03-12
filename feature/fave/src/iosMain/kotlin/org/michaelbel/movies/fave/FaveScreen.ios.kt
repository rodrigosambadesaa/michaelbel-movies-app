@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.fave

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.fave.intent.FaveIntent
import org.michaelbel.movies.fave.model.FaveModel
import org.michaelbel.movies.feed.ui.FeedEmpty
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.page.PageContent
import org.michaelbel.movies.ui.compose.page.PageLoading
import org.michaelbel.movies.ui.ktx.collectAsStateCommon

@Composable
actual fun FaveScreen(
    viewModel: FaveViewModel
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val pagingData by viewModel.moviesFlow.collectAsStateCommon()

    FaveScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        pagingItems = pagingData
    )
}

@Composable
private fun FaveScreenContent(
    state: FaveModel,
    dispatch: (FaveIntent) -> Unit,
    pagingItems: List<MoviePojo>
) {
    val shouldRefreshFavorites = pagingItems.isEmpty() && !state.isFeedLoading
    LaunchedEffect(shouldRefreshFavorites, state.networkStatus) {
        if (shouldRefreshFavorites && state.networkStatus == NetworkStatus.Available) {
            dispatch(FaveIntent.RefreshFavorites)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {},
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val bottomBarHeight = 64.dp
        val bottomBarPadding = 16.dp
        val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navigationBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding() + statusBarTopPadding,
            end = innerPadding.calculateEndPadding(layoutDirection),
            bottom = innerPadding.calculateBottomPadding() + navigationBarBottomPadding + bottomBarHeight + bottomBarPadding
        )

        when {
            state.isFeedLoading -> {
                PageLoading(
                    feedView = state.feedView,
                    paddingValues = contentPadding
                )
            }
            pagingItems.isEmpty() -> {
                FeedEmpty(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            else -> {
                PageContent(
                    feedView = state.feedView,
                    lazyListState = rememberLazyListState(),
                    lazyGridState = rememberLazyGridState(),
                    lazyStaggeredGridState = rememberLazyStaggeredGridState(),
                    pagingItems = pagingItems,
                    onMovieClick = { pagingKey, movieId ->
                        dispatch(FaveIntent.MovieDetailsClick(pagingKey, movieId))
                    },
                    contentPadding = contentPadding,
                    modifier = Modifier
                )
            }
        }
    }
}
