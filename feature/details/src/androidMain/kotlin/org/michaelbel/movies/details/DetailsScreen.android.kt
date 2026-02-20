@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.details.intent.DetailsIntent
import org.michaelbel.movies.details.ktx.movie
import org.michaelbel.movies.details.ktx.movieUrl
import org.michaelbel.movies.details.ktx.onPrimaryContainer
import org.michaelbel.movies.details.ktx.primaryContainer
import org.michaelbel.movies.details.ktx.toolbarTitle
import org.michaelbel.movies.details.model.DetailsModel
import org.michaelbel.movies.details.ui.DetailsContent
import org.michaelbel.movies.details.ui.DetailsFailure
import org.michaelbel.movies.details.ui.DetailsLoading
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.network.connectivity.ktx.isAvailable
import org.michaelbel.movies.network.ktx.isFailure
import org.michaelbel.movies.network.ktx.throwable
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.displayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.modifierDisplayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.navigateToShareText
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.strings.MoviesStrings
import java.net.UnknownHostException

@Composable
actual fun DetailsScreen(
    destination: DetailsDestination,
    viewModel: DetailsViewModel
) {
    val state by viewModel.stateFlow.collectAsStateCommon()

    val context = LocalContext.current
    val shareTitle = stringResource(MoviesStrings.share_via)

    DetailsScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        onShareClick = { context.navigateToShareText(it, shareTitle) }
    )
}

@Composable
private fun DetailsScreenContent(
    state: DetailsModel,
    dispatch: (DetailsIntent) -> Unit,
    onShareClick: (String) -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    if (state.networkStatus.isAvailable && state.detailsState.isFailure && state.detailsState.throwable is UnknownHostException) {
        dispatch(DetailsIntent.LoadMovie)
    }

    val animateContainerColor = animateColorAsState(
        targetValue = state.detailsState.primaryContainer(state.appTheme is AppTheme.Amoled),
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = 0,
            easing = LinearEasing
        ),
        label = "animateContainerColor"
    )
    val animateOnContainerColor = animateColorAsState(
        targetValue = state.detailsState.onPrimaryContainer(state.appTheme is AppTheme.Amoled),
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = 0,
            easing = LinearEasing
        ),
        label = "animateOnContainerColor"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = state.detailsState.toolbarTitle
                    )
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.detailsState.movieUrl != null,
                        modifier = modifierDisplayCutoutWindowInsets,
                        enter = fadeIn()
                    ) {
                        if (state.detailsState.movieUrl != null) {
                            IconButton(
                                onClick = { onShareClick(state.detailsState.movieUrl.orEmpty()) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
                                shape = IconButtonDefaults.extraSmallSquareShape
                            ) {
                                Image(
                                    imageVector = MoviesIcons.Share,
                                    contentDescription = stringResource(MoviesContentDescriptionCommon.ShareIcon),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                    colorFilter = ColorFilter.tint(animateOnContainerColor.value)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { dispatch(DetailsIntent.BackClick) },
                        modifier = modifierDisplayCutoutWindowInsets,
                    ) {
                        Image(
                            imageVector = MoviesIcons.ArrowBack,
                            contentDescription = stringResource(MoviesContentDescriptionCommon.BackIcon),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            colorFilter = ColorFilter.tint(animateOnContainerColor.value)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = animateContainerColor.value.copy(alpha = .95F),
                    titleContentColor = animateOnContainerColor.value,
                    actionIconContentColor = animateOnContainerColor.value,
                    navigationIconContentColor = animateOnContainerColor.value
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        containerColor = animateContainerColor.value
    ) { innerPadding ->
        when (state.detailsState) {
            is ScreenState.Loading -> {
                DetailsLoading(
                    modifier = Modifier
                        .padding(innerPadding)
                        .windowInsetsPadding(displayCutoutWindowInsets)
                        .fillMaxSize()
                )
            }
            is ScreenState.Content<*> -> {
                DetailsContent(
                    modifier = Modifier
                        .padding(innerPadding)
                        .windowInsetsPadding(displayCutoutWindowInsets)
                        .fillMaxSize(),
                    movie = state.detailsState.movie,
                    onContainerColor = animateOnContainerColor.value,
                    isThemeAmoled = state.appTheme is AppTheme.Amoled,
                    onNavigateToGallery = { dispatch(DetailsIntent.GalleryClick) },
                    onGenerateColors = { movieId, containerColor, onContainerColor -> dispatch(DetailsIntent.GenerateColors(movieId, containerColor, onContainerColor)) }
                )
            }
            is ScreenState.Failure -> {
                DetailsFailure(
                    modifier = Modifier
                        .padding(innerPadding)
                        .windowInsetsPadding(displayCutoutWindowInsets)
                        .fillMaxSize()
                )
            }
        }
    }
}
