@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.main.tabs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.common.platform.isDesktop
import org.michaelbel.movies.fave.FaveScreen
import org.michaelbel.movies.feed.FeedScreen
import org.michaelbel.movies.main.event.MainEvent
import org.michaelbel.movies.main.tabs.event.MainTabsEvent
import org.michaelbel.movies.main.tabs.event.MainTabsEventManager
import org.michaelbel.movies.main.tabs.intent.MainTabsIntent
import org.michaelbel.movies.main.tabs.model.MainTabsModel
import org.michaelbel.movies.settings.SettingsScreen
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.fadePredictiveTransitionSpec
import org.michaelbel.movies.ui.ktx.fadeTransitionSpec
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.FaveDestination
import org.michaelbel.movies.ui.navigation.FeedDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination
import org.michaelbel.movies.ui.strings.MoviesStrings

@Composable
fun MainTabsScreen(
    feedDestination: FeedDestination,
    viewModel: MainTabsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()

    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val authFailureMessage = stringResource(MoviesStrings.feed_auth_failure)
    val authSuccessMessage = stringResource(MoviesStrings.feed_auth_success)

    val backStack: MutableList<AppRoute> = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        mutableStateListOf(feedDestination)
    }

    MainTabsScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        backStack = backStack,
        feedDestination = feedDestination,
        snackbarHostState = snackbarHostState,
        layoutDirection = layoutDirection
    )

    LaunchedEffect(feedDestination.requestToken, feedDestination.approved) {
        viewModel.dispatch(MainTabsIntent.HandleRedirect(feedDestination.requestToken, feedDestination.approved))
    }

    ObserveAsEvents(
        flow = MainTabsEventManager.eventFlow
    ) { event ->
        when (event) {
            MainEvent.OpenFeed -> backStack[backStack.lastIndex] = feedDestination
            MainEvent.OpenFave -> backStack[backStack.lastIndex] = FaveDestination
            MainEvent.OpenSettings -> backStack[backStack.lastIndex] = SettingsDestination
        }
    }

    ObserveAsEvents(
        flow = viewModel.eventFlow,
        key1 = snackbarHostState,
        key2 = authFailureMessage to authSuccessMessage
    ) { event ->
        when (event) {
            is MainTabsEvent.ShowSnackbar -> {
                scope.launch {
                    val message = when (event.message) {
                        MoviesStrings.feed_auth_failure -> authFailureMessage
                        MoviesStrings.feed_auth_success -> authSuccessMessage
                        else -> return@launch
                    }
                    snackbarHostState.run {
                        currentSnackbarData?.dismiss()
                        showSnackbar(message)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainTabsScreenContent(
    state: MainTabsModel,
    dispatch: (MainTabsIntent) -> Unit,
    backStack: MutableList<AppRoute>,
    feedDestination: FeedDestination,
    snackbarHostState: SnackbarHostState,
    layoutDirection: LayoutDirection
) {
    var isFeedSearchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (backStack[backStack.lastIndex] != feedDestination || !isFeedSearchActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = if (isDesktop) 16.dp else 0.dp, top = 8.dp)
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalFloatingToolbar(
                        expanded = true
                    ) {
                        Row(
                            modifier = Modifier.animateContentSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                    positioning = TooltipAnchorPosition.Above
                                ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(
                                            text = stringResource(MoviesStrings.main_nav_feed)
                                        )
                                    }
                                },
                                state = rememberTooltipState(),
                                enableUserInput = backStack.lastOrNull() != feedDestination && !isDesktop
                            ) {
                                ToggleButton(
                                    checked = backStack.lastOrNull() == feedDestination,
                                    onCheckedChange = { dispatch(MainTabsIntent.FeedClick) },
                                    shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    Icon(
                                        imageVector = MoviesIcons.GridView,
                                        contentDescription = stringResource(MoviesStrings.main_nav_feed),
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                    )

                                    if (backStack.lastOrNull() == feedDestination || isDesktop) {
                                        Text(
                                            text = stringResource(MoviesStrings.main_nav_feed),
                                            style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp),
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Clip,
                                            modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing)
                                        )
                                    }
                                }
                            }

                            if (state.isFaveFeatureEnabled) {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        positioning = TooltipAnchorPosition.Above
                                    ),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(
                                                text = stringResource(MoviesStrings.main_nav_fave)
                                            )
                                        }
                                    },
                                    state = rememberTooltipState(),
                                    enableUserInput = backStack.lastOrNull() != FaveDestination && !isDesktop
                                ) {
                                    ToggleButton(
                                        checked = backStack.lastOrNull() == FaveDestination,
                                        onCheckedChange = { dispatch(MainTabsIntent.FaveClick) },
                                        shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                                        modifier = Modifier.height(56.dp)
                                    ) {
                                        Icon(
                                            imageVector = MoviesIcons.Favorite,
                                            contentDescription = stringResource(MoviesStrings.main_nav_fave),
                                            modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                        )

                                        if (backStack.lastOrNull() == FaveDestination || isDesktop) {
                                            Text(
                                                text = stringResource(MoviesStrings.main_nav_fave),
                                                style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp),
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Clip,
                                                modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing)
                                            )
                                        }
                                    }
                                }
                            }

                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                    positioning = TooltipAnchorPosition.Above
                                ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(
                                            text = stringResource(MoviesStrings.main_nav_settings)
                                        )
                                    }
                                },
                                state = rememberTooltipState(),
                                enableUserInput = backStack.lastOrNull() != SettingsDestination && !isDesktop
                            ) {
                                ToggleButton(
                                    checked = backStack.lastOrNull() == SettingsDestination,
                                    onCheckedChange = { dispatch(MainTabsIntent.SettingsClick) },
                                    shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    Icon(
                                        imageVector = MoviesIcons.Settings,
                                        contentDescription = stringResource(MoviesStrings.main_nav_settings),
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                    )

                                    if (backStack.lastOrNull() == SettingsDestination || isDesktop) {
                                        Text(
                                            text = stringResource(MoviesStrings.main_nav_settings),
                                            style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp),
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Clip,
                                            modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = 0.dp,
                end = innerPadding.calculateEndPadding(layoutDirection),
                bottom = 0.dp
            ),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = fadeTransitionSpec(),
            popTransitionSpec = fadeTransitionSpec(),
            predictivePopTransitionSpec = fadePredictiveTransitionSpec(),
            entryProvider = entryProvider {
                entry<FeedDestination> { FeedScreen(onSearchActiveChange = { isFeedSearchActive = it }) }
                entry<FaveDestination> { FaveScreen() }
                entry<SettingsDestination> { SettingsScreen() }
            }
        )
    }
}
