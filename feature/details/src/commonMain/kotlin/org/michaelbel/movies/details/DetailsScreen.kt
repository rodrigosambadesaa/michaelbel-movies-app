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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.details.intent.DetailsIntent
import org.michaelbel.movies.details.ktx.movie
import org.michaelbel.movies.details.ktx.movieUrl
import org.michaelbel.movies.details.ktx.onPrimaryContainer
import org.michaelbel.movies.details.ktx.primaryContainer
import org.michaelbel.movies.details.ktx.toolbarTitle
import org.michaelbel.movies.details.ui.DetailsContent
import org.michaelbel.movies.details.ui.DetailsFailure
import org.michaelbel.movies.details.ui.DetailsLoading
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.modifierDisplayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.shareText
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.strings.MoviesStrings

@Composable
fun DetailsScreen(
    destination: DetailsDestination,
    viewModel: DetailsViewModel = koinViewModel { parametersOf(destination) },
    uiInteractor: UiInteractor = koinInject()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val shouldGenerateColors = state.appTheme !is AppTheme.Amoled
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isAmoledTheme = !shouldGenerateColors

    val animateContainerColor = animateColorAsState(
        targetValue = state.detailsState.primaryContainer(isAmoledTheme),
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = 0,
            easing = LinearEasing
        ),
        label = "animateContainerColor"
    )
    val animateOnContainerColor = animateColorAsState(
        targetValue = state.detailsState.onPrimaryContainer(isAmoledTheme),
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
                modifier = modifierDisplayCutoutWindowInsets,
                title = {
                    Text(
                        text = state.detailsState.toolbarTitle
                    )
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.isDetailsFavoriteFeatureEnabled,
                        enter = fadeIn()
                    ) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                positioning = TooltipAnchorPosition.Below
                            ),
                            tooltip = {
                                PlainTooltip {
                                    Text(
                                        text = stringResource(if (state.isAuthorized && state.isFavorite) MoviesStrings.remove_from_favorites else MoviesStrings.add_to_favorites)
                                    )
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                onClick = { viewModel.dispatch(DetailsIntent.FavoriteClick) },
                                enabled = !state.isFavoriteJobActive,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform))
                                    .pointerHoverIcon(PointerIcon.Hand),
                                shape = IconButtonDefaults.extraSmallSquareShape
                            ) {
                                Image(
                                    imageVector = if (state.isAuthorized && state.isFavorite) MoviesIcons.Favorite else MoviesIcons.FavoriteBorder,
                                    contentDescription = stringResource(MoviesContentDescription.FavoriteIcon),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                    colorFilter = ColorFilter.tint(animateOnContainerColor.value)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = state.isDetailsShareFeatureEnabled && state.detailsState.movieUrl != null,
                        enter = fadeIn()
                    ) {
                        state.detailsState.movieUrl?.let { url ->
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                    positioning = TooltipAnchorPosition.Below
                                ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(
                                            text = stringResource(MoviesStrings.share)
                                        )
                                    }
                                },
                                state = rememberTooltipState()
                            ) {
                                IconButton(
                                    onClick = shareText(url, stringResource(MoviesStrings.share_via)),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
                                    shape = IconButtonDefaults.extraSmallSquareShape
                                ) {
                                    Image(
                                        imageVector = MoviesIcons.Share,
                                        contentDescription = stringResource(MoviesContentDescription.ShareIcon),
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                        colorFilter = ColorFilter.tint(animateOnContainerColor.value)
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        onClick = { viewModel.dispatch(DetailsIntent.BackClick) }
                    ) {
                        Image(
                            imageVector = MoviesIcons.ArrowBack,
                            contentDescription = stringResource(MoviesContentDescription.BackIcon),
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
        when (val detailsState = state.detailsState) {
            is ScreenState.Loading -> {
                DetailsLoading(
                    modifier = Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .fillMaxSize(),
                    additionalBottomContentPadding = innerPadding.calculateBottomPadding()
                )
            }
            is ScreenState.Content<*> -> {
                DetailsContent(
                    modifier = Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .fillMaxSize(),
                    additionalBottomContentPadding = innerPadding.calculateBottomPadding(),
                    movie = detailsState.movie,
                    isDetailsGalleryFeatureEnabled = state.isDetailsGalleryFeatureEnabled,
                    onContainerColor = animateOnContainerColor.value,
                    onNavigateToGallery = { viewModel.dispatch(DetailsIntent.GalleryClick) },
                    placeholder = false,
                    shouldGenerateColors = shouldGenerateColors,
                    onGenerateColors = { movieId, containerColor, onContainerColor ->
                        viewModel.dispatch(
                            DetailsIntent.GenerateColors(
                                movieId = movieId,
                                containerColor = containerColor,
                                onContainerColor = onContainerColor
                            )
                        )
                    },
                    detailsPaletteEffect = { movie, placeholder, shouldGenerateColorsValue, onGenerateColors ->
                        uiInteractor.DetailsPaletteEffect(
                            movie = movie,
                            placeholder = placeholder,
                            shouldGenerateColors = shouldGenerateColorsValue,
                            onGenerateColors = onGenerateColors
                        )
                    }
                )
            }
            is ScreenState.Failure -> {
                DetailsFailure(
                    modifier = Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .fillMaxSize()
                )
            }
        }
    }
}
