@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.details.event.DetailsEvent
import org.michaelbel.movies.details.intent.DetailsIntent
import org.michaelbel.movies.details.ktx.movie
import org.michaelbel.movies.details.ktx.movieUrl
import org.michaelbel.movies.details.ktx.toolbarTitle
import org.michaelbel.movies.details.model.DetailsModel
import org.michaelbel.movies.details.ui.DetailsContent
import org.michaelbel.movies.details.ui.DetailsFailure
import org.michaelbel.movies.details.ui.DetailsLoading
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.modifierDisplayCutoutWindowInsets
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.strings.MoviesStrings

@Composable
actual fun DetailsScreen(
    destination: DetailsDestination,
    viewModel: DetailsViewModel
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val copiedText = stringResource(MoviesStrings.details_url_copied)

    DetailsScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        snackbarHostState = snackbarHostState
    )

    ObserveAsEvents(
        flow = viewModel.eventFlow,
        key1 = snackbarHostState
    ) { event ->
        when (event) {
            is DetailsEvent.CopyClick -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = copiedText,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsScreenContent(
    state: DetailsModel,
    dispatch: (DetailsIntent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.detailsState.toolbarTitle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    val movieUrl = state.detailsState.movieUrl
                    AnimatedVisibility(
                        visible = movieUrl != null,
                        modifier = modifierDisplayCutoutWindowInsets,
                        enter = fadeIn()
                    ) {
                        if (movieUrl != null) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(movieUrl))
                                    dispatch(DetailsIntent.CopyClick)
                                },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
                                shape = IconButtonDefaults.extraSmallSquareShape
                            ) {
                                Image(
                                    imageVector = MoviesIcons.Share,
                                    contentDescription = stringResource(MoviesContentDescriptionCommon.ShareIcon),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { dispatch(DetailsIntent.BackClick) },
                        modifier = modifierDisplayCutoutWindowInsets
                    ) {
                        Image(
                            imageVector = MoviesIcons.ArrowBack,
                            contentDescription = stringResource(MoviesContentDescriptionCommon.BackIcon),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        when (state.detailsState) {
            is ScreenState.Loading -> {
                DetailsLoading(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            is ScreenState.Content<*> -> {
                DetailsContent(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    movie = state.detailsState.movie,
                    onNavigateToGallery = { dispatch(DetailsIntent.GalleryClick) }
                )
            }
            is ScreenState.Failure -> {
                DetailsFailure(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }
    }
}
