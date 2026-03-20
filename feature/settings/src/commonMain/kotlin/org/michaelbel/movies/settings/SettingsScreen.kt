@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.common.MOVIES_GITHUB_URL
import org.michaelbel.movies.common.MOVIES_TELEGRAM_URL
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.browser.navigateToUrl
import org.michaelbel.movies.common.gender.GrammaticalGender
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.interactor.entity.AppLanguage
import org.michaelbel.movies.settings.event.SettingsEvent
import org.michaelbel.movies.settings.intent.SettingsIntent
import org.michaelbel.movies.settings.ktx.stringText
import org.michaelbel.movies.settings.model.SettingsModel
import org.michaelbel.movies.settings.ui.SettingsAppIconsBox
import org.michaelbel.movies.settings.ui.SettingsPaletteColorsBox
import org.michaelbel.movies.settings.ui.SettingsDialog
import org.michaelbel.movies.settings.ui.SettingsResetDialog
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.appicon.IconAlias
import org.michaelbel.movies.ui.icons.Cat
import org.michaelbel.movies.ui.icons.Github
import org.michaelbel.movies.ui.icons.GooglePlay
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.icons.SettingsReset
import org.michaelbel.movies.ui.icons.Telegram
import org.michaelbel.movies.ui.icons.ThemeLightDark
import org.michaelbel.movies.ui.icons.TileSmall
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.ktx.OnResume
import org.michaelbel.movies.ui.ktx.SettingsGenderText
import org.michaelbel.movies.ui.ktx.clickableWithoutRipple
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.isDebug
import org.michaelbel.movies.ui.ktx.requestTileService
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.bottomListItemShape
import org.michaelbel.movies.ui.theme.middleExtraSmallListItemShape
import org.michaelbel.movies.ui.theme.topListItemShape
import org.michaelbel.movies.widget.ktx.rememberAndPinAppWidgetProvider

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    uiInteractor: UiInteractor = koinInject()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val openAppNotificationSettings = uiInteractor.navigateToAppNotificationSettings()
    val openAppOpenByDefaultSettings = uiInteractor.navigateToAppOpenByDefaultSettings()
    val requestIgnoreBatteryOptimizations = uiInteractor.requestIgnoreBatteryOptimizations()
    val openBatteryOptimizationSettings = uiInteractor.navigateToBatteryOptimizationSettings()
    val navigateToGithubUrl = navigateToUrl(MOVIES_GITHUB_URL)
    val navigateToTelegramUrl = navigateToUrl(MOVIES_TELEGRAM_URL)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val permissionMessage = stringResource(MoviesStrings.settings_post_notifications_should_request)
    val permissionAction = stringResource(MoviesStrings.settings_action_go)
    val onRequestPostNotificationsPermission = uiInteractor.rememberPostNotificationsPermissionHandler(
        enabled = state.areNotificationsEnabled,
        onPermissionGranted = { viewModel.dispatch(SettingsIntent.CollectNotificationsEnabled) },
        onPermissionDenied = { viewModel.dispatch(SettingsIntent.ShowPermissionSnackbar(permissionMessage, permissionAction)) }
    )
    val onRequestTileService = requestTileService { message -> viewModel.dispatch(SettingsIntent.ShowSnackbar(message)) }

    ObserveAsEvents(
        flow = viewModel.eventFlow,
        key1 = snackbarHostState,
        key2 = lazyListState
    ) { event ->
        when (event) {
            is SettingsEvent.PinWidget -> {}
            is SettingsEvent.RequestPostNotificationsPermission -> onRequestPostNotificationsPermission()
            is SettingsEvent.RequestIgnoreBatteryOptimizations -> {
                when {
                    state.isIgnoringBatteryOptimizations -> openBatteryOptimizationSettings()
                    else -> requestIgnoreBatteryOptimizations()
                }
            }
            is SettingsEvent.RequestTileService -> onRequestTileService()
            is SettingsEvent.RequestGithub -> navigateToGithubUrl()
            is SettingsEvent.RequestTelegram -> navigateToTelegramUrl()
            is SettingsEvent.ScrollToTop -> scope.launch { lazyListState.animateScrollToItem(0) }
            is SettingsEvent.ShowSnackbar -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                scope.launch { snackbarHostState.showSnackbar(message = event.message, duration = SnackbarDuration.Short) }
            }
            is SettingsEvent.ShowPermissionSnackbar -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                scope.launch {
                    val result = snackbarHostState.showSnackbar(message = event.message, actionLabel = event.actionLabel, duration = SnackbarDuration.Long)
                    if (result == SnackbarResult.ActionPerformed) {
                        openAppNotificationSettings()
                    }
                }
            }
        }
    }

    SettingsScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        snackbarHostState = snackbarHostState,
        lazyListState = lazyListState,
        onNavigateToAppOpenByDefaultSettings = openAppOpenByDefaultSettings
    )

    OnResume {
        viewModel.dispatch(SettingsIntent.CollectNotificationsEnabled)
        viewModel.dispatch(SettingsIntent.CollectIgnoringBatteryOptimizations)
    }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsModel,
    dispatch: (SettingsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    lazyListState: LazyListState,
    onNavigateToAppOpenByDefaultSettings: () -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )
    val layoutDirection = LocalLayoutDirection.current
    val iconChangedMessages: Map<IconAlias, String> = if (state.isAppIconFeatureEnabled) {
        IconAlias.VALUES.associateWith { iconAlias ->
            stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, iconAlias.title)
        }
    } else {
        emptyMap()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(
                        text = stringResource(MoviesStrings.settings_title)
                    )
                },
                actions = {
                    if (state.isSettingsResetFeatureEnabled) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                positioning = TooltipAnchorPosition.Below
                            ),
                            tooltip = {
                                PlainTooltip {
                                    Text(
                                        text = stringResource(MoviesStrings.settings_reset)
                                    )
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            var resetSettingsDialog by remember { mutableStateOf(false) }
                            if (resetSettingsDialog) {
                                SettingsResetDialog(
                                    onDismissRequest = { resetSettingsDialog = false },
                                    onResetClick = {
                                        dispatch(SettingsIntent.ResetSettings)
                                        resetSettingsDialog = false
                                    }
                                )
                            }

                            IconButton(
                                onClick = { resetSettingsDialog = true },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)),
                                shape = IconButtonDefaults.extraSmallSquareShape
                            ) {
                                Icon(
                                    imageVector = MoviesIcons.SettingsReset,
                                    contentDescription = stringResource(MoviesContentDescription.ResetSettingsIcon),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.clickableWithoutRipple { dispatch(SettingsIntent.ScrollToTop) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 64.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = innerPadding.calculateEndPadding(layoutDirection),
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            )
        ) {
            if (state.isLanguageFeatureEnabled) {
                item {
                    var languageDialog by remember { mutableStateOf(false) }
                    if (languageDialog) {
                        SettingsDialog(
                            icon = MoviesIcons.Language,
                            title = stringResource(MoviesStrings.settings_language),
                            items = AppLanguage.VALUES,
                            currentItem = AppLanguage.transform(stringResource(MoviesStrings.language_code)),
                            onItemSelect = { dispatch(SettingsIntent.SelectLanguage(it)) },
                            onDismissRequest = { languageDialog = false }
                        )
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(topListItemShape)
                            .clickable(onClick = { languageDialog = true }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_language),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = AppLanguage.transform(stringResource(MoviesStrings.language_code)).stringText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Language,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isThemeFeatureEnabled) {
                item {
                    var themeDialog by remember { mutableStateOf(false) }
                    if (themeDialog) {
                        SettingsDialog(
                            icon = MoviesIcons.ThemeLightDark,
                            title = stringResource(MoviesStrings.settings_theme),
                            items = AppTheme.VALUES,
                            currentItem = state.themeData.appTheme,
                            onItemSelect = { dispatch(SettingsIntent.SelectTheme(it)) },
                            onDismissRequest = { themeDialog = false }
                        )
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(if (state.isLanguageFeatureEnabled) middleExtraSmallListItemShape else topListItemShape)
                            .clickable(onClick = { themeDialog = true }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_theme),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = state.themeData.appTheme.stringText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.ThemeLightDark,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isGenderFeatureEnabled) {
                item {
                    var genderDialog by remember { mutableStateOf(false) }
                    if (genderDialog) {
                        SettingsDialog(
                            icon = MoviesIcons.Cat,
                            title = SettingsGenderText,
                            items = GrammaticalGender.VALUES,
                            currentItem = state.grammaticalGender,
                            onItemSelect = { dispatch(SettingsIntent.SetGrammaticalGender(GrammaticalGender.value(it))) },
                            onDismissRequest = { genderDialog = false }
                        )
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { genderDialog = true }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = SettingsGenderText,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = state.grammaticalGender.stringText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Cat,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isMovieListFeatureEnabled) {
                item {
                    var movieListDialog by remember { mutableStateOf(false) }
                    if (movieListDialog) {
                        SettingsDialog(
                            icon = MoviesIcons.LocalMovies,
                            title = stringResource(MoviesStrings.settings_movie_list),
                            items = MovieList.VALUES,
                            currentItem = state.movieList,
                            onItemSelect = { dispatch(SettingsIntent.SelectMovieList(it)) },
                            onDismissRequest = { movieListDialog = false }
                        )
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { movieListDialog = true }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_movie_list),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = state.movieList.stringText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.LocalMovies,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isFeedViewFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_appearance),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                ToggleButton(
                                    checked = state.feedView == FeedView.FeedList,
                                    onCheckedChange = { dispatch(SettingsIntent.SelectFeedView(FeedView.FeedList)) },
                                    modifier = Modifier
                                        .weight(1F)
                                        .height(40.dp),
                                    shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
                                ) {
                                    Icon(
                                        imageVector = MoviesIcons.ViewAgenda,
                                        contentDescription = null,
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                    )

                                    Spacer(
                                        modifier = Modifier.size(ToggleButtonDefaults.IconSpacing)
                                    )

                                    Text(
                                        text = stringResource(MoviesStrings.settings_appearance_list)
                                    )
                                }

                                ToggleButton(
                                    checked = state.feedView == FeedView.FeedGrid,
                                    onCheckedChange = { dispatch(SettingsIntent.SelectFeedView(FeedView.FeedGrid)) },
                                    modifier = Modifier
                                        .weight(1F)
                                        .height(40.dp),
                                    shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
                                ) {
                                    Icon(
                                        imageVector = MoviesIcons.Dashboard,
                                        contentDescription = null,
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                    )

                                    Spacer(
                                        modifier = Modifier.size(ToggleButtonDefaults.IconSpacing)
                                    )

                                    Text(
                                        text = stringResource(MoviesStrings.settings_appearance_grid)
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.GridView,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isDynamicColorsFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(
                                onClick = {
                                    val enabled = !state.themeData.dynamicColors
                                    dispatch(SettingsIntent.SetDynamicColors(enabled))
                                    if (enabled) {
                                        dispatch(SettingsIntent.SetPaletteColors(false))
                                    }
                                }
                            ),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_dynamic_colors),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_dynamic_colors_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.FormatPaint,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.themeData.dynamicColors,
                                onCheckedChange = null,
                                thumbContent = if (state.themeData.dynamicColors) {
                                    {
                                        Icon(
                                            imageVector = MoviesIcons.Check,
                                            contentDescription = MoviesContentDescription.None,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isPaletteColorsFeatureEnabled) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .background(MaterialTheme.colorScheme.inversePrimary)
                    ) {
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        val enabled = !state.themeData.paletteColors
                                        dispatch(SettingsIntent.SetPaletteColors(enabled))
                                        if (enabled) {
                                            dispatch(SettingsIntent.SetDynamicColors(false))
                                        }
                                    }
                                ),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                            headlineContent = {
                                Text(
                                    text = stringResource(MoviesStrings.settings_palette_colors),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(MoviesStrings.settings_palette_colors_description),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = MoviesIcons.Palette,
                                    contentDescription = null,
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = state.themeData.paletteColors,
                                    onCheckedChange = null,
                                    thumbContent = if (state.themeData.paletteColors) {
                                        {
                                            Icon(
                                                imageVector = MoviesIcons.Check,
                                                contentDescription = MoviesContentDescription.None,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    } else null
                                )
                            }
                        )

                        AnimatedVisibility(
                            visible = state.themeData.paletteColors,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            SettingsPaletteColorsBox(
                                paletteKey = state.themeData.paletteKey,
                                seedColor = state.themeData.seedColor,
                                onChange = { localPaletteKey, localSeedColor ->
                                    dispatch(SettingsIntent.SetPaletteKey(localPaletteKey))
                                    dispatch(SettingsIntent.SetSeedColor(localSeedColor))
                                }
                            )
                        }
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isAppIconFeatureEnabled) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .background(MaterialTheme.colorScheme.inversePrimary)
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(MoviesStrings.settings_app_launcher_icon),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(MoviesStrings.settings_app_launcher_icon_description),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = MoviesIcons.Apps,
                                    contentDescription = null,
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary)
                        )

                        SettingsAppIconsBox(
                            enabledIcon = state.enabledIcon,
                            onChange = { iconAlias ->
                                iconChangedMessages[iconAlias]?.let { message ->
                                    dispatch(SettingsIntent.ShowSnackbar(message))
                                }
                                dispatch(SettingsIntent.SetAppIcon(iconAlias))
                            }
                        )
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isAppOpenByDefaultFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = onNavigateToAppOpenByDefaultSettings),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_open_by_default),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_open_by_default_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isNotificationsFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.RequestPostNotificationsPermission) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_post_notifications),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(if (state.areNotificationsEnabled) MoviesStrings.settings_post_notifications_granted else MoviesStrings.settings_post_notifications_denied),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.areNotificationsEnabled,
                                onCheckedChange = null,
                                thumbContent = if (state.areNotificationsEnabled) {
                                    {
                                        Icon(
                                            imageVector = MoviesIcons.Check,
                                            contentDescription = MoviesContentDescription.None,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isBatteryOptimizationFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.RequestIgnoreBatteryOptimizations) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_battery_optimization),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(if (state.isIgnoringBatteryOptimizations) MoviesStrings.settings_battery_optimization_ignored else MoviesStrings.settings_battery_optimization_optimized),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.BatterySaver,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.isIgnoringBatteryOptimizations,
                                onCheckedChange = null,
                                thumbContent = if (state.isIgnoringBatteryOptimizations) {
                                    {
                                        Icon(
                                            imageVector = MoviesIcons.Check,
                                            contentDescription = MoviesContentDescription.None,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isWidgetFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = rememberAndPinAppWidgetProvider()),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_app_widget),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_app_widget_description, stringResource(MoviesStrings.appwidget_description)),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Widgets,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isTileFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.RequestTileService) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_tile),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_tile_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.TileSmall,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isBiometricFeatureEnabled && state.isBiometricAvailable) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.SetBiometricEnabled(!state.isBiometricEnabled)) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_lock_app),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(if (state.isBiometricEnabled) MoviesStrings.settings_biometric_added else MoviesStrings.settings_biometric_not_added),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.isBiometricEnabled,
                                onCheckedChange = null,
                                thumbContent = if (state.isBiometricEnabled) {
                                    {
                                        Icon(
                                            imageVector = MoviesIcons.Check,
                                            contentDescription = MoviesContentDescription.None,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isScreenshotFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.SetScreenshotBlockEnabled(!state.isScreenshotBlockEnabled)) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_screenshots),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_screenshots_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Screenshot,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.isScreenshotBlockEnabled,
                                onCheckedChange = null,
                                thumbContent = if (state.isScreenshotBlockEnabled) {
                                    {
                                        Icon(
                                            imageVector = MoviesIcons.Check,
                                            contentDescription = MoviesContentDescription.None,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isGithubFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(middleExtraSmallListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.RequestGithub) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_github),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_github_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Github,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isTelegramFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(if (state.isReviewAppFeatureEnabled && state.isReviewFeatureEnabled || state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) middleExtraSmallListItemShape else bottomListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.RequestTelegram) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_telegram),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_telegram_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Telegram,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
            }
            if (state.isReviewAppFeatureEnabled && state.isReviewFeatureEnabled || state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) {
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isReviewAppFeatureEnabled && state.isReviewFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(if (state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) middleExtraSmallListItemShape else bottomListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.ReviewClick) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_review),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_review_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.GooglePlay,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(bottomListItemShape)
                            .clickable(onClick = { dispatch(SettingsIntent.UpdateClick) }),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_update),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_update_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.SystemUpdate,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }
            if (state.isAboutFeatureEnabled) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp, end = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MoviesIcons.MovieFilter,
                            contentDescription = MoviesContentDescription.None,
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = stringResource(MoviesStrings.settings_app_version_name, state.versionName),
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                        )

                        Text(
                            text = stringResource(MoviesStrings.settings_app_version_code, state.versionCode),
                            modifier = Modifier.padding(start = 2.dp),
                            style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.primary)
                        )

                        Text(
                            text = state.appVersionData.flavor,
                            modifier = Modifier.padding(start = 2.dp),
                            style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                        )

                        if (isDebug) {
                            Text(
                                text = stringResource(MoviesStrings.settings_app_debug),
                                modifier = Modifier.padding(start = 2.dp),
                                style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    }
                }
            }
        }
    }
}
