@file:OptIn(ExperimentalMaterial3Api::class)

package org.michaelbel.movies.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import org.michaelbel.movies.settings.SettingsViewModel
import org.michaelbel.movies.settings.event.SettingsEventManager
import org.michaelbel.movies.settings.intent.SettingsIntent
import org.michaelbel.movies.settings.ktx.iconSnackbarTextRes
import org.michaelbel.movies.settings.ktx.stringText
import org.michaelbel.movies.settings.model.SettingsData
import org.michaelbel.movies.settings.model.SettingsModel
import org.michaelbel.movies.settings.ui.common.SettingAppIcon
import org.michaelbel.movies.settings.ui.common.SettingItem
import org.michaelbel.movies.settings.ui.common.SettingSwitchItem
import org.michaelbel.movies.settings.ui.common.SettingsDialog
import org.michaelbel.movies.ui.appicon.IconAlias
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
    var areNotificationsEnabled by remember { mutableStateOf(state.areNotificationsEnabled) }
    val openAppNotificationSettings = viewModel.settingsUiInteractor.navigateToAppNotificationSettings()
    val navigateToUrl = navigateToUrl(MOVIES_GITHUB_URL)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val permissionMessage = stringResource(MoviesStrings.settings_post_notifications_should_request)
    val permissionAction = stringResource(MoviesStrings.settings_action_go)
    val onShowPermissionSnackbar: () -> Unit = {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = permissionMessage,
                actionLabel = permissionAction,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                openAppNotificationSettings()
            }
        }
    }

    val onShowSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    val messageRed = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Red.iconSnackbarTextRes))
    val messagePurple = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Purple.iconSnackbarTextRes))
    val messageBrown = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Brown.iconSnackbarTextRes))
    val messageAmoled = stringResource(MoviesStrings.settings_app_launcher_icon_changed_to, stringResource(IconAlias.Amoled.iconSnackbarTextRes))

    SettingsScreenContent(
        state = state,
        dispatch = viewModel::dispatch,
        settingsData = SettingsData(
            notificationsData = SettingsData.NotificationsData(
                isEnabled = areNotificationsEnabled,
                onClick = viewModel.settingsUiInteractor.rememberPostNotificationsPermissionHandler(
                    areNotificationsEnabled = areNotificationsEnabled,
                    onPermissionGranted = { areNotificationsEnabled = state.areNotificationsEnabled },
                    onPermissionDenied = onShowPermissionSnackbar
                )
            ),
            tileData = SettingsData.RequestedData(
                onRequest = requestTileService(onShowSnackbar)
            ),
            appIconData = SettingsData.ListData(
                current = viewModel.settingsUiInteractor.enabledIcon,
                onSelect = { icon ->
                    val message = when (icon) {
                        IconAlias.Red -> messageRed
                        IconAlias.Purple -> messagePurple
                        IconAlias.Brown -> messageBrown
                        IconAlias.Amoled -> messageAmoled
                    }
                    onShowSnackbar(message)
                    viewModel.settingsUiInteractor.setIcon(icon)
                }
            ),
            githubData = SettingsData.RequestedData(
                onRequest = navigateToUrl
            )
        ),
        snackbarHostState = snackbarHostState,
        isNavigationIconVisible = viewModel.settingsUiInteractor.isNavigationIconVisible
    )

    OnResume {
        areNotificationsEnabled = state.areNotificationsEnabled
    }

    ObserveAsEvents(SettingsEventManager.eventFlow) { event ->
        when (event) {
            is SettingsEventManager.PinWidget -> {}
        }
    }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsModel,
    dispatch: (SettingsIntent) -> Unit,
    settingsData: SettingsData,
    snackbarHostState: SnackbarHostState,
    isNavigationIconVisible: Boolean
) {
    val scope = rememberCoroutineScope()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )
    val lazyListState = rememberLazyListState()
    val onScrollToTop: () -> Unit = {
        scope.launch { lazyListState.animateScrollToItem(0) }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsToolbar(
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                isNavigationIconVisible = isNavigationIconVisible,
                onNavigationIconClick = { dispatch(SettingsIntent.BackClick) },
                onClick = onScrollToTop
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = innerPadding
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

                    SettingItem(
                        title = stringResource(MoviesStrings.settings_language),
                        description = AppLanguage.transform(stringResource(MoviesStrings.language_code)).stringText,
                        icon = MoviesIcons.Language,
                        onClick = { languageDialog = true }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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

                    SettingItem(
                        title = stringResource(MoviesStrings.settings_theme),
                        description = state.themeData.appTheme.stringText,
                        icon = MoviesIcons.ThemeLightDark,
                        onClick = { themeDialog = true }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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

                    SettingItem(
                        title = stringResource(MoviesStrings.settings_appearance),
                        description = state.feedView.stringText,
                        icon = MoviesIcons.GridView,
                        onClick = { appearanceDialog = true }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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

                    SettingItem(
                        title = stringResource(MoviesStrings.settings_movie_list),
                        description = state.movieList.stringText,
                        icon = MoviesIcons.LocalMovies,
                        onClick = { movieListDialog = true }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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

                    SettingItem(
                        title = SettingsGenderText,
                        description = state.grammaticalGender.stringText,
                        icon = MoviesIcons.Cat,
                        onClick = { genderDialog = true }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isDynamicColorsFeatureEnabled) {
                item {
                    SettingSwitchItem(
                        title = stringResource(MoviesStrings.settings_dynamic_colors),
                        description = stringResource(MoviesStrings.settings_dynamic_colors_description),
                        icon = MoviesIcons.Palette,
                        checked = state.themeData.dynamicColors,
                        onClick = { dispatch(SettingsIntent.SetDynamicColors(!state.themeData.dynamicColors)) }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isPaletteColorsFeatureEnabled) {
                item {
                    Text(
                        text = stringResource(MoviesStrings.settings_palette_colors),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
                item {
                    SettingsPaletteColorsBox(
                        isDynamicColorsEnabled = state.themeData.dynamicColors,
                        paletteKey = state.themeData.paletteKey,
                        seedColor = state.themeData.seedColor,
                        onChange = { localDynamicColors, localPaletteKey, localSeedColor ->
                            dispatch(SettingsIntent.SetDynamicColors(localDynamicColors))
                            dispatch(SettingsIntent.SetPaletteKey(localPaletteKey))
                            dispatch(SettingsIntent.SetSeedColor(localSeedColor))
                        }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isAppIconFeatureEnabled) {
                item {
                    Text(
                        text = stringResource(MoviesStrings.settings_app_launcher_icon),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SettingAppIcon(
                            iconAlias = IconAlias.Red,
                            isEnabled = settingsData.appIconData.current == IconAlias.Red,
                            onClick = settingsData.appIconData.onSelect
                        )

                        SettingAppIcon(
                            iconAlias = IconAlias.Purple,
                            isEnabled = settingsData.appIconData.current == IconAlias.Purple,
                            onClick = settingsData.appIconData.onSelect
                        )

                        SettingAppIcon(
                            iconAlias = IconAlias.Brown,
                            isEnabled = settingsData.appIconData.current == IconAlias.Brown,
                            onClick = settingsData.appIconData.onSelect
                        )

                        SettingAppIcon(
                            iconAlias = IconAlias.Amoled,
                            isEnabled = settingsData.appIconData.current == IconAlias.Amoled,
                            onClick = settingsData.appIconData.onSelect
                        )
                    }
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isNotificationsFeatureEnabled) {
                item {
                    SettingSwitchItem(
                        title = stringResource(MoviesStrings.settings_post_notifications),
                        description = stringResource(if (settingsData.notificationsData.isEnabled) MoviesStrings.settings_post_notifications_granted else MoviesStrings.settings_post_notifications_denied),
                        icon = MoviesIcons.Notifications,
                        checked = settingsData.notificationsData.isEnabled,
                        onClick = settingsData.notificationsData.onClick
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isWidgetFeatureEnabled) {
                item {
                    SettingItem(
                        title = stringResource(MoviesStrings.settings_app_widget),
                        description = stringResource(MoviesStrings.settings_app_widget_description, stringResource(MoviesStrings.appwidget_description)),
                        icon = MoviesIcons.Widgets,
                        onClick = rememberAndPinAppWidgetProvider()
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isTileFeatureEnabled) {
                item {
                    SettingItem(
                        title = stringResource(MoviesStrings.settings_tile),
                        description = stringResource(MoviesStrings.settings_tile_description),
                        icon = MoviesIcons.ViewAgenda,
                        onClick = settingsData.tileData.onRequest
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isBiometricFeatureEnabled && state.isBiometricAvailable) {
                item {
                    SettingSwitchItem(
                        title = stringResource(MoviesStrings.settings_lock_app),
                        description = stringResource(if (state.isBiometricEnabled) MoviesStrings.settings_biometric_added else MoviesStrings.settings_biometric_not_added),
                        icon = MoviesIcons.Fingerprint,
                        checked = state.isBiometricEnabled,
                        onClick = { dispatch(SettingsIntent.SetBiometricEnabled(!state.isBiometricEnabled)) }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isScreenshotFeatureEnabled) {
                item {
                    SettingSwitchItem(
                        title = stringResource(MoviesStrings.settings_screenshots),
                        description = stringResource(MoviesStrings.settings_screenshots_description),
                        icon = MoviesIcons.Screenshot,
                        checked = state.isScreenshotBlockEnabled,
                        onClick = { dispatch(SettingsIntent.SetScreenshotBlockEnabled(!state.isScreenshotBlockEnabled)) }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isGithubFeatureEnabled) {
                item {
                    SettingItem(
                        title = stringResource(MoviesStrings.settings_github),
                        description = stringResource(MoviesStrings.settings_github_description),
                        icon = MoviesIcons.Github,
                        onClick = settingsData.githubData.onRequest
                    )
                }
                if (state.isReviewAppFeatureEnabled && state.isReviewFeatureEnabled || state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            thickness = .1.dp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            if (state.isReviewAppFeatureEnabled && state.isReviewFeatureEnabled) {
                item {
                    SettingItem(
                        title = stringResource(MoviesStrings.settings_review),
                        description = stringResource(MoviesStrings.settings_review_description),
                        icon = MoviesIcons.GooglePlay,
                        onClick = { dispatch(SettingsIntent.ReviewClick) }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isUpdateAppFeatureEnabled && state.isUpdateFeatureEnabled && state.isUpdateAvailable) {
                item {
                    SettingItem(
                        title = stringResource(MoviesStrings.settings_update),
                        description = stringResource(MoviesStrings.settings_update_description),
                        icon = MoviesIcons.SystemUpdate,
                        onClick = { dispatch(SettingsIntent.UpdateClick) }
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        thickness = .1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (state.isAboutFeatureEnabled) {
                item {
                    SettingsVersionBox(
                        aboutData = SettingsData.AboutData(
                            versionName = state.versionName,
                            versionCode = state.versionCode,
                            flavor = state.appVersionData.flavor,
                            isDebug = isDebug
                        )
                    )
                }
            }
        }
    }
}