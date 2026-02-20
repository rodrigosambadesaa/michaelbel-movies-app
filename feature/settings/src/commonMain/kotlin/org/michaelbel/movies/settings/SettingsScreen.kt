@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.common.MOVIES_GITHUB_URL
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.browser.navigateToUrl
import org.michaelbel.movies.common.gender.GrammaticalGender
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.interactor.entity.AppLanguage
import org.michaelbel.movies.settings.event.SettingsEvent
import org.michaelbel.movies.settings.intent.SettingsIntent
import org.michaelbel.movies.settings.ktx.iconSnackbarTextRes
import org.michaelbel.movies.settings.ktx.stringText
import org.michaelbel.movies.settings.model.SettingsModel
import org.michaelbel.movies.settings.ui.SettingsPaletteColorsBox
import org.michaelbel.movies.settings.ui.SettingsToolbar
import org.michaelbel.movies.settings.ui.SettingsVersionBox
import org.michaelbel.movies.settings.ui.common.SettingAppIcon
import org.michaelbel.movies.settings.ui.common.SettingsDialog
import org.michaelbel.movies.ui.appicon.IconAlias
import org.michaelbel.movies.ui.compose.SwitchCheckIcon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.ktx.SettingsGenderText
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.isDebug
import org.michaelbel.movies.ui.ktx.requestTileService
import org.michaelbel.movies.ui.lifecycle.OnResume
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.widget.ktx.rememberAndPinAppWidgetProvider

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val openAppNotificationSettings = viewModel.settingsUiInteractor.navigateToAppNotificationSettings()
    val navigateToUrl = navigateToUrl(MOVIES_GITHUB_URL)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val permissionMessage = stringResource(MoviesStrings.settings_post_notifications_should_request)
    val permissionAction = stringResource(MoviesStrings.settings_action_go)
    val onRequestPostNotificationsPermission = viewModel.settingsUiInteractor.rememberPostNotificationsPermissionHandler(
        areNotificationsEnabled = state.areNotificationsEnabled,
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
            is SettingsEvent.RequestTileService -> onRequestTileService()
            is SettingsEvent.RequestGithub -> navigateToUrl()
            is SettingsEvent.ScrollToTop -> scope.launch { lazyListState.animateScrollToItem(0) }
            is SettingsEvent.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            is SettingsEvent.ShowPermissionSnackbar -> {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Long
                    )
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
        isNavigationIconVisible = viewModel.settingsUiInteractor.isNavigationIconVisible,
    )

    OnResume {
        viewModel.dispatch(SettingsIntent.CollectNotificationsEnabled)
    }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsModel,
    dispatch: (SettingsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    lazyListState: LazyListState,
    isNavigationIconVisible: Boolean
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )
    val layoutDirection = LocalLayoutDirection.current
    val messageRed = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Red.iconSnackbarTextRes))
    val messagePurple = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Purple.iconSnackbarTextRes))
    val messageBrown = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Brown.iconSnackbarTextRes))
    val messageAmoled = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Amoled.iconSnackbarTextRes))

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsToolbar(
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                isNavigationIconVisible = isNavigationIconVisible,
                onNavigationIconClick = { dispatch(SettingsIntent.BackClick) },
                onClick = { dispatch(SettingsIntent.ScrollToTop) }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = innerPadding.calculateTopPadding(),
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
                            .clickable(onClick = { languageDialog = true }),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
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
                            .clickable(onClick = { themeDialog = true }),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isFeedViewFeatureEnabled) {
                item {
                    var appearanceDialog by remember { mutableStateOf(false) }

                    if (appearanceDialog) {
                        SettingsDialog(
                            icon = MoviesIcons.GridView,
                            title = stringResource(MoviesStrings.settings_appearance),
                            items = FeedView.VALUES,
                            currentItem = state.feedView,
                            onItemSelect = { dispatch(SettingsIntent.SelectFeedView(it)) },
                            onDismissRequest = { appearanceDialog = false }
                        )
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { appearanceDialog = true }),
                        headlineContent = {
                            Text(
                                text = stringResource(MoviesStrings.settings_appearance),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = state.feedView.stringText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.GridView,
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
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
                            .clickable(onClick = { movieListDialog = true }),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isGenderFeatureEnabled) {
                item {
                    var genderDialog by remember { mutableStateOf(false) }
                    if (genderDialog) {
                        SettingsDialog(
                            icon = MoviesIcons.Cat,
                            title = stringResource(MoviesStrings.settings_gender),
                            items = GrammaticalGender.VALUES,
                            currentItem = state.grammaticalGender,
                            onItemSelect = { dispatch(SettingsIntent.SetGrammaticalGender(GrammaticalGender.value(it))) },
                            onDismissRequest = { genderDialog = false }
                        )
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { genderDialog = true }),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isDynamicColorsFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    val enabled = !state.themeData.dynamicColors
                                    dispatch(SettingsIntent.SetDynamicColors(enabled))
                                    if (enabled) {
                                        dispatch(SettingsIntent.SetPaletteColors(false))
                                    }
                                }
                            ),
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
                                imageVector = MoviesIcons.Wallpaper,
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.themeData.dynamicColors,
                                onCheckedChange = null,
                                thumbContent = if (state.themeData.dynamicColors) {
                                    { SwitchCheckIcon() }
                                } else {
                                    null
                                }
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isPaletteColorsFeatureEnabled) {
                item {
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
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.themeData.paletteColors,
                                onCheckedChange = null,
                                thumbContent = if (state.themeData.paletteColors) {
                                    { SwitchCheckIcon() }
                                } else {
                                    null
                                }
                            )
                        }
                    )
                }
                item {
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
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isAppIconFeatureEnabled) {
                item {
                    Text(
                        text = stringResource(MoviesStrings.settings_app_launcher_icon),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SettingAppIcon(
                            iconAlias = IconAlias.Red,
                            isEnabled = state.enabledIcon == IconAlias.Red,
                            onClick = {
                                dispatch(SettingsIntent.ShowSnackbar(messageRed))
                                dispatch(SettingsIntent.SetAppIcon(IconAlias.Red))
                            }
                        )

                        SettingAppIcon(
                            iconAlias = IconAlias.Purple,
                            isEnabled = state.enabledIcon == IconAlias.Purple,
                            onClick = {
                                dispatch(SettingsIntent.ShowSnackbar(messagePurple))
                                dispatch(SettingsIntent.SetAppIcon(IconAlias.Purple))
                            }
                        )

                        SettingAppIcon(
                            iconAlias = IconAlias.Brown,
                            isEnabled = state.enabledIcon == IconAlias.Brown,
                            onClick = {
                                dispatch(SettingsIntent.ShowSnackbar(messageBrown))
                                dispatch(SettingsIntent.SetAppIcon(IconAlias.Brown))
                            }
                        )

                        SettingAppIcon(
                            iconAlias = IconAlias.Amoled,
                            isEnabled = state.enabledIcon == IconAlias.Amoled,
                            onClick = {
                                dispatch(SettingsIntent.ShowSnackbar(messageAmoled))
                                dispatch(SettingsIntent.SetAppIcon(IconAlias.Amoled))
                            }
                        )
                    }
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isNotificationsFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dispatch(SettingsIntent.RequestPostNotificationsPermission) }),
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
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.areNotificationsEnabled,
                                onCheckedChange = null,
                                thumbContent = if (state.areNotificationsEnabled) {
                                    { SwitchCheckIcon() }
                                } else {
                                    null
                                }
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isWidgetFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = rememberAndPinAppWidgetProvider()),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isTileFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dispatch(SettingsIntent.RequestTileService) }),
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
                                imageVector = MoviesIcons.ViewAgenda,
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isBiometricFeatureEnabled && state.isBiometricAvailable) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dispatch(SettingsIntent.SetBiometricEnabled(!state.isBiometricEnabled)) }),
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
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.isBiometricEnabled,
                                onCheckedChange = null,
                                thumbContent = if (state.isBiometricEnabled) {
                                    { SwitchCheckIcon() }
                                } else {
                                    null
                                }
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isScreenshotFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dispatch(SettingsIntent.SetScreenshotBlockEnabled(!state.isScreenshotBlockEnabled)) }),
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
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.isScreenshotBlockEnabled,
                                onCheckedChange = null,
                                thumbContent = if (state.isScreenshotBlockEnabled) {
                                    { SwitchCheckIcon() }
                                } else {
                                    null
                                }
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isGithubFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dispatch(SettingsIntent.RequestGithub) }),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                if (state.isReviewAppFeatureEnabled && state.isReviewFeatureEnabled || state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            if (state.isReviewAppFeatureEnabled && state.isReviewFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dispatch(SettingsIntent.ReviewClick) }),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { dispatch(SettingsIntent.UpdateClick) }),
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
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            if (state.isAboutFeatureEnabled) {
                item {
                    SettingsVersionBox(
                        versionName = state.versionName,
                        versionCode = state.versionCode,
                        flavor = state.appVersionData.flavor,
                        isDebug = isDebug
                    )
                }
            }
        }
    }
}
