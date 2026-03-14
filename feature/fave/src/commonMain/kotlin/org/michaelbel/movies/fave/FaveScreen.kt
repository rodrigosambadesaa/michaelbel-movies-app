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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.fave.intent.FaveIntent
import org.michaelbel.movies.fave.model.FaveModel
import org.michaelbel.movies.feed.ui.FeedEmpty
import org.michaelbel.movies.ui.compose.page.PageContent
import org.michaelbel.movies.ui.compose.page.PageFailure
import org.michaelbel.movies.ui.compose.page.PageLoading
import org.michaelbel.movies.ui.ktx.clickableWithoutRipple
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.isFailure
import org.michaelbel.movies.ui.ktx.isLoading
import org.michaelbel.movies.ui.ktx.modifierDisplayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.pageContentTopPadding
import org.michaelbel.movies.ui.ktx.refreshThrowable
import org.michaelbel.movies.ui.ktx.rememberConnectivityClickHandler

@Composable
fun FaveScreen(
    viewModel: FaveViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {},
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding() + WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + pageContentTopPadding,
            end = innerPadding.calculateEndPadding(layoutDirection),
            bottom = innerPadding.calculateBottomPadding() + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 72.dp + pageContentTopPadding
        )
        when {
            pagingItems.isLoading -> {
                PageLoading(
                    feedView = state.feedView,
                    modifier = modifierDisplayCutoutWindowInsets,
                    paddingValues = contentPadding
                )
            }
            pagingItems.isFailure -> {
                when {
                    pagingItems.refreshThrowable is PageEmptyException -> {
                        FeedEmpty(
                            modifier = Modifier
                                .padding(innerPadding)
                                .then(modifierDisplayCutoutWindowInsets)
                                .fillMaxSize()
                        )
                    }
                    else -> {
                        PageFailure(
                            modifier = Modifier
                                .padding(innerPadding)
                                .then(modifierDisplayCutoutWindowInsets)
                                .fillMaxSize()
                                .clickableWithoutRipple(pagingItems::retry),
                            isButtonVisible = state.isPageFailureButtonVisible,
                            onButtonClick = rememberConnectivityClickHandler()
                        )
                    }
                }
            }
            else -> {
                PageContent(
                    feedView = state.feedView,
                    lazyListState = rememberLazyListState(),
                    lazyGridState = rememberLazyGridState(),
                    lazyStaggeredGridState = rememberLazyStaggeredGridState(),
                    pagingItems = pagingItems,
                    onMovieClick = { pagingKey, movieId -> viewModel.dispatch(FaveIntent.MovieDetailsClick(pagingKey, movieId)) },
                    contentPadding = contentPadding,
                    modifier = modifierDisplayCutoutWindowInsets
                )
            }
        }
    }
}
