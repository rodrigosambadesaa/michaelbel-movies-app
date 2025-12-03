package org.michaelbel.movies.search.ui

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.search.SearchViewModel2
import org.michaelbel.movies.search.intent.SearchIntent
import org.michaelbel.movies.search.model.SearchModel
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
actual fun SearchScreen(
    viewModel: SearchViewModel2
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val active by viewModel.isSearchActive.collectAsStateCommon()

    SearchScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        pagingItems = pagingItems,
        active = active
    )
}

@Composable
private fun SearchScreenContent(
    state: SearchModel,
    dispatch: (SearchIntent) -> Unit,
    pagingItems: LazyPagingItems<MoviePojo>,
    active: Boolean
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }

    val onShowSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
        }
    }

    if (pagingItems.isFailure && pagingItems.refreshThrowable is ApiKeyNotNullException) {
        onShowSnackbar(stringResource(UiR.string.error_api_key_null))
    }

    if (state.networkStatus == NetworkStatus.Available && pagingItems.isFailure && pagingItems.refreshThrowable is UnknownHostException) {
        pagingItems.retry()
    }

    var query by rememberSaveable { mutableStateOf("") }

    val searchBarHorizontalPadding: Dp by animateDpAsState(
        targetValue = if (active) 0.dp else 8.dp,
        label = ""
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { innerPadding ->
        Column {
            SearchToolbar(
                query = query,
                onQueryChange = { text ->
                    query = text
                },
                onSearch = {
                    dispatch(SearchIntent.EnterSearchQuery(query))
                    dispatch(SearchIntent.ChangeActiveState(false))
                },
                active = active,
                onActiveChange = { dispatch(SearchIntent.ChangeActiveState(it)) },
                onBackClick = { dispatch(SearchIntent.BackClick) },
                onCloseClick = {
                    dispatch(SearchIntent.ChangeActiveState(query.isNotEmpty()))
                    query = ""
                    focusRequester.requestFocus()
                },
                onInputText = { text ->
                    query = text
                    dispatch(SearchIntent.EnterSearchQuery(query))
                    dispatch(SearchIntent.ChangeActiveState(query.isNotEmpty()))
                },
                suggestions = state.suggestions,
                searchHistoryMovies = state.searchHistoryMovies,
                onHistoryMovieRemoveClick = { dispatch(SearchIntent.RemoveMovieFromHistoryClick(it)) },
                onClearHistoryClick = { dispatch(SearchIntent.ClearSearchHistoryClick) },
                modifier = Modifier
                    .padding(horizontal = searchBarHorizontalPadding)
                    .windowInsetsPadding(displayCutoutWindowInsets)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            when {
                pagingItems.isLoading -> {
                    PageLoading(
                        feedView = state.feedView,
                        modifier = Modifier.windowInsetsPadding(displayCutoutWindowInsets)
                    )
                }
                pagingItems.isFailure -> {
                    if (pagingItems.refreshThrowable is PageEmptyException) {
                        SearchEmpty(
                            modifier = Modifier
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
                        onMovieClick = { movieList, movieId ->
                            dispatch(SearchIntent.SaveMovieToHistoryClick(movieId))
                            dispatch(SearchIntent.MovieDetailsClick(movieList, movieId))
                        },
                        contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                        modifier = Modifier.windowInsetsPadding(displayCutoutWindowInsets)
                    )
                }
            }
        }
    }

    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
}