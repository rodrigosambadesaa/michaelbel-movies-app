@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalComposeUiApi::class)

package org.michaelbel.movies.feed.ui

import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.feed.intent.FeedIntent
import org.michaelbel.movies.feed.ktx.SearchTrailingAction
import org.michaelbel.movies.feed.model.FeedModel
import org.michaelbel.movies.persistence.database.ktx.isEmpty
import org.michaelbel.movies.persistence.database.ktx.letters
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.Query
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.compose.AccountAvatar
import org.michaelbel.movies.ui.compose.page.PageFailure
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.onSecondaryClick
import org.michaelbel.movies.ui.rememberSpeechRecognitionLauncher
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.bottomListItemShape
import org.michaelbel.movies.ui.theme.middleExtraSmallListItemShape
import org.michaelbel.movies.ui.theme.middleLargeIncreasedListItemShape
import org.michaelbel.movies.ui.theme.topListItemShape

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
    state: FeedModel,
    dispatch: (FeedIntent) -> Unit,
    isSearchRefreshLoading: Boolean,
    isSearchFailure: Boolean,
    isSearchEmptyFailure: Boolean,
    onSearchRetryClick: () -> Unit,
    searchLoadingContent: @Composable (modifier: Modifier) -> Unit,
    searchContent: @Composable (
        modifier: Modifier,
        lazyStaggeredGridState: LazyStaggeredGridState
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchContainerColor = MaterialTheme.colorScheme.inversePrimary
    val searchBarColors = SearchBarDefaults.colors(
        containerColor = searchContainerColor,
        dividerColor = if (isSearchResultsVisible) Color.Transparent else MaterialTheme.colorScheme.onPrimaryContainer
    )
    val searchFocusRequester = remember { FocusRequester() }
    val searchResultsLazyStaggeredGridState = rememberLazyStaggeredGridState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val textFieldState = rememberTextFieldState(initialText = query)
    val requestFocusAfterClose = remember { mutableStateOf(false) }
    val expandedHistoryMovieId = remember { mutableStateOf<MovieId?>(null) }
    val searchInputTextStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
    val searchHistoryMovies = state.searchHistoryMovies
    val suggestions = state.suggestions
    val searchTrailingAction = when {
        textFieldState.text.isNotEmpty() -> SearchTrailingAction.Clear
        state.isFeedVoiceInputFeatureEnabled && active -> SearchTrailingAction.Voice
        else -> SearchTrailingAction.None
    }

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
                textStyle = searchInputTextStyle,
                placeholder = {
                    Text(
                        text = stringResource(MoviesStrings.search_title),
                        style = searchInputTextStyle
                    )
                },
                leadingIcon = {
                    if (active || isSearchResultsVisible) {
                        IconButton(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            onClick = {
                                clearInputFocus()
                                onBackClick()
                            }
                        ) {
                            Image(
                                imageVector = MoviesIcons.ArrowBack,
                                contentDescription = stringResource(MoviesContentDescription.BackIcon),
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    } else {
                        Image(
                            imageVector = MoviesIcons.Search,
                            contentDescription = stringResource(MoviesContentDescription.SearchIcon),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = searchTrailingAction == SearchTrailingAction.Clear,
                                enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                                exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                            ) {
                                IconButton(
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                    onClick = {
                                        onCloseClick()
                                        requestFocusAfterClose.value = true
                                    }
                                ) {
                                    Image(
                                        imageVector = MoviesIcons.Close,
                                        contentDescription = stringResource(MoviesContentDescription.CloseIcon),
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                                    )
                                }
                            }

                            androidx.compose.animation.AnimatedVisibility(
                                visible = searchTrailingAction == SearchTrailingAction.Voice,
                                enter = scaleIn(transformOrigin = TransformOrigin(0F, 0F)) + fadeIn() + expandIn(expandFrom = Alignment.TopStart),
                                exit = scaleOut(transformOrigin = TransformOrigin(0F, 0F)) + fadeOut() + shrinkOut(shrinkTowards = Alignment.TopStart)
                            ) {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        positioning = TooltipAnchorPosition.Below
                                    ),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(
                                                text = stringResource(MoviesStrings.voice_search)
                                            )
                                        }
                                    },
                                    state = rememberTooltipState()
                                ) {
                                    IconButton(
                                        onClick = rememberSpeechRecognitionLauncher { text ->
                                            onInputText(text)
                                            clearInputFocus()
                                        }
                                    ) {
                                        Image(
                                            imageVector = MoviesIcons.KeyboardVoice,
                                            contentDescription = stringResource(MoviesContentDescription.VoiceIcon),
                                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                                        )
                                    }
                                }
                            }
                        }

                        if (state.isFeedAuthIconFeatureEnabled && !active && !isSearchResultsVisible) {
                            when {
                                state.accountPojo.isEmpty -> {
                                    TooltipBox(
                                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                            positioning = TooltipAnchorPosition.Below
                                        ),
                                        tooltip = {
                                            PlainTooltip {
                                                Text(
                                                    text = stringResource(MoviesStrings.login)
                                                )
                                            }
                                        },
                                        state = rememberTooltipState()
                                    ) {
                                        IconButton(
                                            onClick = { dispatch(FeedIntent.AuthClick) },
                                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                        ) {
                                            Image(
                                                imageVector = MoviesIcons.AccountCircle,
                                                contentDescription = stringResource(MoviesContentDescription.AccountIcon),
                                                modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    IconButton(
                                        onClick = { dispatch(FeedIntent.AccountClick) },
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                    ) {
                                        AccountAvatar(
                                            account = state.accountPojo,
                                            fontSize = if (state.accountPojo.letters.length == 1) 16.sp else 13.sp,
                                            modifier = Modifier.size(IconButtonDefaults.largeIconSize)
                                        )
                                    }
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
        shadowElevation = 2.dp,
        colors = searchBarColors
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
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
                                SearchEmpty()
                            } else {
                                PageFailure(
                                    onClick = onSearchRetryClick,
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
                                searchResultsLazyStaggeredGridState
                            )
                        }
                    }
                }
                searchHistoryMovies.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues()
                    ) {
                        item {
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                headlineContent = {
                                    Text(
                                        text = stringResource(MoviesStrings.search_recent),
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Start
                                        )
                                    )
                                },
                                trailingContent = {
                                    TextButton(
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                        onClick = { dispatch(FeedIntent.ClearSearchHistoryClick) },
                                        shapes = ButtonDefaults.shapes()
                                    ) {
                                        Text(
                                            text = stringResource(MoviesStrings.search_clear),
                                            style = LocalTextStyle.current.copy(
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                }
                            )
                        }
                        itemsIndexed(
                            items = searchHistoryMovies,
                            key = { index, movie -> movie.movieId }
                        ) { index, movie ->
                            val itemShape = when {
                                searchHistoryMovies.size == 1 -> middleLargeIncreasedListItemShape
                                index == 0 -> topListItemShape
                                index == searchHistoryMovies.lastIndex -> bottomListItemShape
                                else -> middleExtraSmallListItemShape
                            }

                            Column {
                                SwipeToDismiss(
                                    item = movie,
                                    onDelete = { dispatch(FeedIntent.RemoveMovieFromHistoryClick(it.movieId)) },
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    shape = itemShape
                                ) { historyMovie, _ ->
                                    Box {
                                        ListItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(itemShape)
                                                .combinedClickable(
                                                    onClick = {
                                                        expandedHistoryMovieId.value = null
                                                        onInputText(historyMovie.title)
                                                        clearInputFocus()
                                                    },
                                                    onLongClick = { expandedHistoryMovieId.value = historyMovie.movieId }
                                                )
                                                .onSecondaryClick { expandedHistoryMovieId.value = historyMovie.movieId },
                                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                            headlineContent = {
                                                Text(
                                                    text = historyMovie.title,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            leadingContent = {
                                                Icon(
                                                    imageVector = MoviesIcons.History,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                                )
                                            },
                                            trailingContent = {
                                                IconButton(
                                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                                    onClick = {
                                                        expandedHistoryMovieId.value = null
                                                        dispatch(FeedIntent.RemoveMovieFromHistoryClick(historyMovie.movieId))
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = MoviesIcons.Close,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        )

                                        DropdownMenu(
                                            expanded = expandedHistoryMovieId.value == historyMovie.movieId,
                                            onDismissRequest = { expandedHistoryMovieId.value = null },
                                            modifier = Modifier.widthIn(min = 180.dp),
                                            shape = middleLargeIncreasedListItemShape,
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            tonalElevation = 2.dp,
                                            shadowElevation = 4.dp
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = stringResource(MoviesStrings.search_remove),
                                                        color = MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = MoviesIcons.Delete,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                },
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                                onClick = {
                                                    expandedHistoryMovieId.value = null
                                                    dispatch(FeedIntent.RemoveMovieFromHistoryClick(historyMovie.movieId))
                                                }
                                            )
                                        }
                                    }
                                }

                                if (index != searchHistoryMovies.lastIndex) {
                                    Spacer(
                                        modifier = Modifier.height(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                suggestions.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues()
                    ) {
                        item {
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ListItemDefaults.colors(
                                    containerColor = Color.Transparent
                                ),
                                headlineContent = {
                                    Text(
                                        text = stringResource(MoviesStrings.search_recommendations),
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Start
                                        )
                                    )
                                }
                            )
                        }
                        itemsIndexed(
                            items = suggestions
                        ) { index, suggestion ->
                            val itemShape = when {
                                suggestions.size == 1 -> middleLargeIncreasedListItemShape
                                index == 0 -> topListItemShape
                                index == suggestions.lastIndex -> bottomListItemShape
                                else -> middleExtraSmallListItemShape
                            }
                            ListItem(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .clip(itemShape)
                                    .clickable {
                                        onInputText(suggestion.title)
                                        clearInputFocus()
                                    },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                headlineContent = {
                                    Text(
                                        text = suggestion.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }
                }
                else -> {
                    SearchEmpty()
                }
            }
        }
    }
}
