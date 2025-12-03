@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.feed.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.michaelbel.movies.feed.FeedViewModel
import org.michaelbel.movies.feed.intent.FeedIntent
import org.michaelbel.movies.feed.ktx.titleText
import org.michaelbel.movies.feed.model.FeedModel
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.page.PageContent
import org.michaelbel.movies.ui.compose.page.PageLoading
import org.michaelbel.movies.ui.ktx.collectAsStateCommon

@Composable
actual fun FeedScreen(
    viewModel: FeedViewModel
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val pagingData by viewModel.pagingDataFlow2.collectAsStateCommon()

    FeedScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        pagingItems = pagingData
    )
}

@Composable
private fun FeedScreenContent(
    state: FeedModel,
    dispatch: (FeedIntent) -> Unit,
    pagingItems: List<MoviePojo>
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FeedToolbar(
                title = state.movieList.titleText,
                account = AccountPojo.Empty,
                isTmdbApiKeyEmpty = false,
                topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                isSearchIconVisible = false,
                onSearchIconClick = { dispatch(FeedIntent.SearchClick) },
                isAuthIconVisible = false,
                onAuthIconClick = { dispatch(FeedIntent.AuthClick) },
                onAccountIconClick = { dispatch(FeedIntent.AccountClick) },
                isSettingsIconVisible = true,
                onSettingsIconClick = { dispatch(FeedIntent.SettingsClick) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            pagingItems.isEmpty() -> {
                PageLoading(
                    feedView = state.feedView,
                    paddingValues = innerPadding
                )
            }
            else -> {
                PageContent(
                    feedView = state.feedView,
                    lazyListState = rememberLazyListState(),
                    lazyGridState = rememberLazyGridState(),
                    lazyStaggeredGridState = rememberLazyStaggeredGridState(),
                    pagingItems = pagingItems,
                    onMovieClick = { pagingKey, movieId -> dispatch(FeedIntent.MovieDetailsClick(pagingKey, movieId)) },
                    contentPadding = innerPadding,
                    modifier = Modifier
                )
            }
        }
    }
}