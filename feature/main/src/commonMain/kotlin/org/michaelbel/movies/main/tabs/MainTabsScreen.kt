@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3AdaptiveApi::class)

package org.michaelbel.movies.main.tabs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
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
import org.michaelbel.movies.ui.ObserveAsEvents
import org.michaelbel.movies.ui.collectAsStateCommon
import org.michaelbel.movies.ui.fadePredictiveTransitionSpec
import org.michaelbel.movies.ui.fadeTransitionSpec
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.modifierDisplayCutoutWindowInsets
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
        snackbarHostState = snackbarHostState
    )

    LaunchedEffect(
        feedDestination.mainDestination.requestToken,
        feedDestination.mainDestination.approved
    ) {
        viewModel.dispatch(
            MainTabsIntent.HandleRedirect(
                feedDestination.mainDestination.requestToken,
                feedDestination.mainDestination.approved
            )
        )
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
                snackbarHostState.currentSnackbarData?.dismiss()
                scope.launch {
                    val message = when (event.message) {
                        MoviesStrings.feed_auth_failure -> authFailureMessage
                        MoviesStrings.feed_auth_success -> authSuccessMessage
                        else -> return@launch
                    }
                    snackbarHostState.showSnackbar(message)
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
    snackbarHostState: SnackbarHostState
) {
    val openSearch = feedDestination.mainDestination.openSearch
    var isFeedSearchActive by rememberSaveable { mutableStateOf(openSearch) }
    var openSearchOnFeedStart by rememberSaveable { mutableStateOf(openSearch) }
    val currentDestination = backStack.lastOrNull()
    val shouldShowNavigation = currentDestination != feedDestination || !isFeedSearchActive

    val navigationSuiteType = when {
        currentWindowDpSize().width >= 1200.dp -> NavigationSuiteType.WideNavigationRailExpanded // fixme
        else -> NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    }
    val isNavigationBar = navigationSuiteType == NavigationSuiteType.ShortNavigationBarCompact ||
            navigationSuiteType == NavigationSuiteType.ShortNavigationBarMedium
    val isWideNavigationRailCollapsed = navigationSuiteType == NavigationSuiteType.WideNavigationRailCollapsed
    val isWideNavigationRailExpanded = navigationSuiteType == NavigationSuiteType.WideNavigationRailExpanded
    val isNavigationRail = isWideNavigationRailCollapsed || isWideNavigationRailExpanded
    val navigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(
        initialValue = when {
            shouldShowNavigation -> NavigationSuiteScaffoldValue.Visible
            else -> NavigationSuiteScaffoldValue.Hidden
        }
    )

    val navDisplay: @Composable (Modifier) -> Unit = { modifier ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = fadeTransitionSpec(),
            popTransitionSpec = fadeTransitionSpec(),
            predictivePopTransitionSpec = fadePredictiveTransitionSpec(),
            entryProvider = entryProvider {
                entry<FeedDestination> {
                    FeedScreen(
                        initialSearchActive = openSearchOnFeedStart,
                        onSearchActiveChange = {
                            isFeedSearchActive = it
                            if (openSearchOnFeedStart) {
                                openSearchOnFeedStart = false
                            }
                        }
                    )
                }
                entry<FaveDestination> { FaveScreen() }
                entry<SettingsDestination> { SettingsScreen() }
            }
        )
    }

    when {
        isNavigationBar -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (shouldShowNavigation) {
                        MainTabsBottomBar(
                            state = state,
                            currentDestination = currentDestination,
                            feedDestination = feedDestination,
                            dispatch = dispatch
                        )
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                navDisplay(Modifier.fillMaxSize())
            }
        }
        isNavigationRail -> {
            val toggleButtonColors = ToggleButtonDefaults.toggleButtonColors()
            val navigationRailItemColors = NavigationRailItemDefaults.colors(
                selectedIconColor = toggleButtonColors.checkedContentColor,
                selectedTextColor = toggleButtonColors.checkedContentColor,
                indicatorColor = toggleButtonColors.checkedContainerColor,
                unselectedIconColor = toggleButtonColors.contentColor,
                unselectedTextColor = toggleButtonColors.contentColor,
                disabledIconColor = toggleButtonColors.disabledContentColor,
                disabledTextColor = toggleButtonColors.disabledContentColor
            )

            when {
                isWideNavigationRailCollapsed -> {
                    NavigationSuiteScaffold(
                        navigationItems = {
                            NavigationRailItem(
                                selected = currentDestination == feedDestination,
                                onClick = { dispatch(MainTabsIntent.FeedClick) },
                                icon = {
                                    Icon(
                                        imageVector = MoviesIcons.GridView,
                                        contentDescription = stringResource(MoviesStrings.main_nav_feed)
                                    )
                                },
                                modifier = modifierDisplayCutoutWindowInsets,
                                colors = navigationRailItemColors
                            )

                            if (state.isFaveFeatureEnabled) {
                                NavigationRailItem(
                                    selected = currentDestination == FaveDestination,
                                    onClick = { dispatch(MainTabsIntent.FaveClick) },
                                    icon = {
                                        Icon(
                                            imageVector = MoviesIcons.Favorite,
                                            contentDescription = stringResource(MoviesStrings.main_nav_fave)
                                        )
                                    },
                                    modifier = modifierDisplayCutoutWindowInsets,
                                    colors = navigationRailItemColors
                                )
                            }

                            NavigationRailItem(
                                selected = currentDestination == SettingsDestination,
                                onClick = { dispatch(MainTabsIntent.SettingsClick) },
                                icon = {
                                    Icon(
                                        imageVector = MoviesIcons.Settings,
                                        contentDescription = stringResource(MoviesStrings.main_nav_settings)
                                    )
                                },
                                modifier = modifierDisplayCutoutWindowInsets,
                                colors = navigationRailItemColors
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        navigationSuiteType = navigationSuiteType,
                        navigationSuiteColors = NavigationSuiteDefaults.colors(
                            wideNavigationRailColors = WideNavigationRailDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            navigationRailContainerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        state = navigationSuiteScaffoldState,
                        navigationItemVerticalArrangement = Arrangement.Center
                    ) {
                        navDisplay(Modifier.fillMaxSize())
                    }
                }
                isWideNavigationRailExpanded -> {
                    NavigationSuiteScaffoldLayout(
                        navigationSuite = {
                            Surface(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(192.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(
                                        space = 8.dp,
                                        alignment = Alignment.CenterVertically
                                    )
                                ) {
                                    Surface(
                                        onClick = { dispatch(MainTabsIntent.FeedClick) },
                                        modifier = modifierDisplayCutoutWindowInsets
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        color = when {
                                            currentDestination == feedDestination -> toggleButtonColors.checkedContainerColor
                                            else -> Color.Transparent
                                        },
                                        contentColor = when {
                                            currentDestination == feedDestination -> toggleButtonColors.checkedContentColor
                                            else -> toggleButtonColors.contentColor
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = MoviesIcons.GridView,
                                                contentDescription = stringResource(MoviesStrings.main_nav_feed),
                                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                            )

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

                                    if (state.isFaveFeatureEnabled) {
                                        Surface(
                                            onClick = { dispatch(MainTabsIntent.FaveClick) },
                                            modifier = modifierDisplayCutoutWindowInsets
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            color = when {
                                                currentDestination == FaveDestination -> toggleButtonColors.checkedContainerColor
                                                else -> Color.Transparent
                                            },
                                            contentColor = when {
                                                currentDestination == FaveDestination -> toggleButtonColors.checkedContentColor
                                                else -> toggleButtonColors.contentColor
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = MoviesIcons.Favorite,
                                                    contentDescription = stringResource(MoviesStrings.main_nav_fave),
                                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                                )

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

                                    Surface(
                                        onClick = { dispatch(MainTabsIntent.SettingsClick) },
                                        modifier = modifierDisplayCutoutWindowInsets
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        color = when {
                                            currentDestination == SettingsDestination -> toggleButtonColors.checkedContainerColor
                                            else -> Color.Transparent
                                        },
                                        contentColor = when {
                                            currentDestination == SettingsDestination -> toggleButtonColors.checkedContentColor
                                            else -> toggleButtonColors.contentColor
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = MoviesIcons.Settings,
                                                contentDescription = stringResource(MoviesStrings.main_nav_settings),
                                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                            )

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
                        },
                        navigationSuiteType = navigationSuiteType,
                        state = navigationSuiteScaffoldState
                    ) {
                        navDisplay(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }

    LaunchedEffect(shouldShowNavigation) {
        when {
            shouldShowNavigation -> navigationSuiteScaffoldState.show()
            else -> navigationSuiteScaffoldState.hide()
        }
    }
}

@Composable
private fun MainTabsBottomBar(
    state: MainTabsModel,
    currentDestination: AppRoute?,
    feedDestination: FeedDestination,
    dispatch: (MainTabsIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = if (isDesktop) 16.dp else 0.dp
            )
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
                    enableUserInput = currentDestination != feedDestination && !isDesktop
                ) {
                    ToggleButton(
                        checked = currentDestination == feedDestination,
                        onCheckedChange = { dispatch(MainTabsIntent.FeedClick) },
                        shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(
                            imageVector = MoviesIcons.GridView,
                            contentDescription = stringResource(MoviesStrings.main_nav_feed),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                        )

                        if (currentDestination == feedDestination || isDesktop) {
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
                        enableUserInput = currentDestination != FaveDestination && !isDesktop
                    ) {
                        ToggleButton(
                            checked = currentDestination == FaveDestination,
                            onCheckedChange = { dispatch(MainTabsIntent.FaveClick) },
                            shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(
                                imageVector = MoviesIcons.Favorite,
                                contentDescription = stringResource(MoviesStrings.main_nav_fave),
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )

                            if (currentDestination == FaveDestination || isDesktop) {
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
                    enableUserInput = currentDestination != SettingsDestination && !isDesktop
                ) {
                    ToggleButton(
                        checked = currentDestination == SettingsDestination,
                        onCheckedChange = { dispatch(MainTabsIntent.SettingsClick) },
                        shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(
                            imageVector = MoviesIcons.Settings,
                            contentDescription = stringResource(MoviesStrings.main_nav_settings),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                        )

                        if (currentDestination == SettingsDestination || isDesktop) {
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

@Composable
private fun MainTabsNavigationRail(
    state: MainTabsModel,
    currentDestination: AppRoute?,
    feedDestination: FeedDestination,
    dispatch: (MainTabsIntent) -> Unit
) {
    val toggleButtonColors = ToggleButtonDefaults.toggleButtonColors()
    val navigationRailItemColors = NavigationRailItemDefaults.colors(
        selectedIconColor = toggleButtonColors.checkedContentColor,
        selectedTextColor = toggleButtonColors.checkedContentColor,
        indicatorColor = toggleButtonColors.checkedContainerColor,
        unselectedIconColor = toggleButtonColors.contentColor,
        unselectedTextColor = toggleButtonColors.contentColor,
        disabledIconColor = toggleButtonColors.disabledContentColor,
        disabledTextColor = toggleButtonColors.disabledContentColor
    )

    NavigationRail(
        modifier = Modifier
            .fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Spacer(
            modifier = Modifier.weight(1F)
        )

        NavigationRailItem(
            selected = currentDestination == feedDestination,
            onClick = { dispatch(MainTabsIntent.FeedClick) },
            icon = {
                Icon(
                    imageVector = MoviesIcons.GridView,
                    contentDescription = stringResource(MoviesStrings.main_nav_feed)
                )
            },
            modifier = modifierDisplayCutoutWindowInsets,
            label = {
                Text(
                    text = stringResource(MoviesStrings.main_nav_feed)
                )
            },
            alwaysShowLabel = true,
            colors = navigationRailItemColors
        )

        if (state.isFaveFeatureEnabled) {
            NavigationRailItem(
                selected = currentDestination == FaveDestination,
                onClick = { dispatch(MainTabsIntent.FaveClick) },
                icon = {
                    Icon(
                        imageVector = MoviesIcons.Favorite,
                        contentDescription = stringResource(MoviesStrings.main_nav_fave)
                    )
                },
                modifier = modifierDisplayCutoutWindowInsets,
                label = {
                    Text(
                        text = stringResource(MoviesStrings.main_nav_fave)
                    )
                },
                alwaysShowLabel = true,
                colors = navigationRailItemColors
            )
        }

        NavigationRailItem(
            selected = currentDestination == SettingsDestination,
            onClick = { dispatch(MainTabsIntent.SettingsClick) },
            icon = {
                Icon(
                    imageVector = MoviesIcons.Settings,
                    contentDescription = stringResource(MoviesStrings.main_nav_settings)
                )
            },
            modifier = modifierDisplayCutoutWindowInsets,
            label = {
                Text(
                    text = stringResource(MoviesStrings.main_nav_settings)
                )
            },
            alwaysShowLabel = true,
            colors = navigationRailItemColors
        )

        Spacer(
            modifier = Modifier.weight(1F)
        )
    }
}
