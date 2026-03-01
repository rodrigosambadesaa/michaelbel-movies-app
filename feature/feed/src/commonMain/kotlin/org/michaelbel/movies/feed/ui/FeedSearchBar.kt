@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.feed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo
import org.michaelbel.movies.persistence.database.ktx.isEmpty
import org.michaelbel.movies.persistence.database.ktx.letters
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.compose.AccountAvatar
import org.michaelbel.movies.ui.compose.page.PageFailure
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.rememberSpeechRecognitionLauncher
import org.michaelbel.movies.ui.strings.MoviesStrings

@Composable
fun FeedSearchBar(
    query: String,
    onQueryChange: (Query) -> Unit,
    onSearch: (Query) -> Unit,
    active: Boolean,
    isAutoFocusEnabled: Boolean,
    isSearchResultsVisible: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onInputText: (Query) -> Unit,
    account: AccountPojo,
    onAuthIconClick: () -> Unit,
    onAccountIconClick: () -> Unit,
    isSearchRefreshLoading: Boolean,
    isSearchFailure: Boolean,
    isSearchEmptyFailure: Boolean,
    onSearchRetryClick: () -> Unit,
    searchLoadingContent: @Composable (modifier: Modifier) -> Unit,
    searchContent: @Composable (
        modifier: Modifier,
        lazyListState: LazyListState,
        lazyGridState: LazyGridState,
        lazyStaggeredGridState: LazyStaggeredGridState
    ) -> Unit,
    suggestions: List<SuggestionPojo>,
    searchHistoryMovies: List<MoviePojo>,
    onHistoryMovieRemoveClick: (MovieId) -> Unit,
    onClearHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchContainerColor = MaterialTheme.colorScheme.inversePrimary
    val searchBarColors = SearchBarDefaults.colors(
        containerColor = searchContainerColor,
        dividerColor = if (isSearchResultsVisible) Color.Transparent else MaterialTheme.colorScheme.onPrimaryContainer
    )
    val searchFocusRequester = remember { FocusRequester() }
    val searchResultsLazyListState = rememberLazyListState()
    val searchResultsLazyGridState = rememberLazyGridState()
    val searchResultsLazyStaggeredGridState = rememberLazyStaggeredGridState()
    val focusManager = LocalFocusManager.current
    val layoutDirection = LocalLayoutDirection.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val textFieldState = rememberTextFieldState(initialText = query)
    val requestFocusAfterClose = remember { mutableStateOf(false) }
    val safeDrawingHorizontalPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues()
    val safeDrawingStartPadding = safeDrawingHorizontalPadding.calculateStartPadding(layoutDirection)
    val safeDrawingEndPadding = safeDrawingHorizontalPadding.calculateEndPadding(layoutDirection)

    LaunchedEffect(query) {
        if (textFieldState.text.toString() != query) {
            textFieldState.setTextAndPlaceCursorAtEnd(query)
        }
    }

    LaunchedEffect(textFieldState, onQueryChange) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { onQueryChange(it) }
    }

    LaunchedEffect(active, isAutoFocusEnabled) {
        if (active && isAutoFocusEnabled) {
            searchFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(active, requestFocusAfterClose.value) {
        if (active && requestFocusAfterClose.value) {
            searchFocusRequester.requestFocus()
            requestFocusAfterClose.value = false
        }
    }

    fun clearInputFocus() {
        keyboardController?.hide()
        focusManager.clearFocus(force = true)
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                state = textFieldState,
                modifier = Modifier.focusRequester(searchFocusRequester),
                onSearch = { searchQuery ->
                    onSearch(searchQuery)
                    clearInputFocus()
                },
                expanded = active,
                onExpandedChange = onActiveChange,
                placeholder = {
                    Text(
                        text = stringResource(MoviesStrings.search_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                    )
                },
                leadingIcon = {
                    if (active || isSearchResultsVisible) {
                        IconButton(
                            onClick = {
                                clearInputFocus()
                                onBackClick()
                            }
                        ) {
                            Image(
                                imageVector = MoviesIcons.ArrowBack,
                                contentDescription = stringResource(MoviesContentDescriptionCommon.BackIcon),
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    } else {
                        Image(
                            imageVector = MoviesIcons.Search,
                            contentDescription = stringResource(MoviesContentDescriptionCommon.SearchIcon),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onCloseClick()
                                    requestFocusAfterClose.value = true
                                }
                            ) {
                                Image(
                                    imageVector = MoviesIcons.Close,
                                    contentDescription = stringResource(MoviesContentDescriptionCommon.CloseIcon),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                                )
                            }
                        } else if (active) {
                            IconButton(
                                onClick = rememberSpeechRecognitionLauncher { text ->
                                    onInputText(text)
                                    clearInputFocus()
                                }
                            ) {
                                Image(
                                    imageVector = MoviesIcons.KeyboardVoice,
                                    contentDescription = stringResource(MoviesContentDescriptionCommon.VoiceIcon),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                                )
                            }
                        }

                        if (!active && !isSearchResultsVisible) {
                            IconButton(
                                onClick = if (account.isEmpty) onAuthIconClick else onAccountIconClick
                            ) {
                                if (account.isEmpty) {
                                    Image(
                                        imageVector = MoviesIcons.AccountCircle,
                                        contentDescription = stringResource(MoviesContentDescriptionCommon.AccountIcon),
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                                    )
                                } else {
                                    AccountAvatar(
                                        account = account,
                                        fontSize = if (account.letters.length == 1) 16.sp else 13.sp,
                                        modifier = Modifier.size(IconButtonDefaults.largeIconSize)
                                    )
                                }
                            }
                        }
                    }
                },
                colors = searchBarColors.inputFieldColors
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        modifier = modifier,
        colors = searchBarColors
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = safeDrawingStartPadding, end = safeDrawingEndPadding)
        ) {
            when {
                isSearchResultsVisible -> {
                    when {
                        isSearchRefreshLoading -> {
                            searchLoadingContent(
                                Modifier
                                    .fillMaxSize()
                                    .background(searchContainerColor)
                            )
                        }
                        isSearchFailure -> {
                            if (isSearchEmptyFailure) {
                                SearchEmpty(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(searchContainerColor)
                                )
                            } else {
                                PageFailure(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(searchContainerColor)
                                        .clickable { onSearchRetryClick() },
                                    isButtonVisible = false,
                                    onButtonClick = {}
                                )
                            }
                        }
                        else -> {
                            searchContent(
                                Modifier
                                    .fillMaxSize()
                                    .background(searchContainerColor),
                                searchResultsLazyListState,
                                searchResultsLazyGridState,
                                searchResultsLazyStaggeredGridState
                            )
                        }
                    }
                }
                searchHistoryMovies.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        contentPadding = PaddingValues(bottom = 136.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = stringResource(MoviesStrings.search_recent),
                                    modifier = Modifier
                                        .weight(1F)
                                        .padding(top = 12.dp, end = 8.dp, bottom = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Start)
                                )

                                TextButton(
                                    onClick = onClearHistoryClick,
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .align(Alignment.Top),
                                    shapes = ButtonDefaults.shapes()
                                ) {
                                    Text(
                                        text = stringResource(MoviesStrings.search_clear),
                                        style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                    )
                                }
                            }
                        }
                        items(
                            items = searchHistoryMovies,
                            key = { it.movieId }
                        ) { movie ->
                            SwipeToDismiss(
                                item = movie,
                                onDelete = { onHistoryMovieRemoveClick(it.movieId) }
                            ) { historyMovie, _ ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.inversePrimary)
                                        .clickable {
                                            onInputText(historyMovie.title)
                                            clearInputFocus()
                                        }
                                        .padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = MoviesIcons.History,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(IconButtonDefaults.smallIconSize),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    Text(
                                        text = historyMovie.title,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .weight(1F)
                                    )

                                    IconButton(
                                        onClick = { onHistoryMovieRemoveClick(historyMovie.movieId) },
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Icon(
                                            imageVector = MoviesIcons.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                suggestions.isNotEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(
                                items = suggestions
                            ) { suggestion ->
                                Text(
                                    text = suggestion.title,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clickable {
                                            onInputText(suggestion.title)
                                            clearInputFocus()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                )
                            }
                        }
                    }
                }

                else -> {
                    SearchEmpty(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                    )
                }
            }
        }
    }
}
