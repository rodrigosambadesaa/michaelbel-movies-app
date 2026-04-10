@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.fave

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.fave.intent.FaveIntent
import org.michaelbel.movies.ui.collectAsStateCommon
import org.michaelbel.movies.ui.compose.page.FeedEmpty
import org.michaelbel.movies.ui.compose.page.PageContent
import org.michaelbel.movies.ui.compose.page.PageFailure
import org.michaelbel.movies.ui.compose.page.PageLoading
import org.michaelbel.movies.ui.isFailure
import org.michaelbel.movies.ui.isLoading
import org.michaelbel.movies.ui.navigationBarPadding
import org.michaelbel.movies.ui.refreshThrowable
import org.michaelbel.movies.ui.rememberConnectivityClickHandler

@Composable
fun FaveScreen(
    viewModel: FaveViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { innerPadding ->
        when {
            pagingItems.isLoading -> {
                PageLoading(
                    feedView = state.feedView,
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding().plus(navigationBarPadding)
                    )
                )
            }
            pagingItems.isFailure -> {
                when {
                    pagingItems.refreshThrowable is PageEmptyException -> {
                        FeedEmpty(
                            contentPadding = PaddingValues(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding()
                            )
                        )
                    }
                    else -> {
                        PageFailure(
                            onClick = pagingItems::retry,
                            contentPadding = PaddingValues(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding()
                            ),
                            isButtonVisible = state.isPageFailureButtonVisible,
                            onButtonClick = rememberConnectivityClickHandler()
                        )
                    }
                }
            }
            else -> {
                PageContent(
                    feedView = state.feedView,
                    pagingItems = pagingItems,
                    onMovieClick = { pagingKey, movieId ->
                        viewModel.dispatch(FaveIntent.MovieDetailsClick(pagingKey, movieId))
                    },
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding().plus(navigationBarPadding)
                    )
                )
            }
        }
    }
}
