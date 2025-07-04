package org.michaelbel.movies.settings.intent

import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.mvi.Intent
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.interactor.entity.AppLanguage

sealed interface SettingsIntent: Intent {
    data object CollectThemeData: SettingsIntent
    data object CollectFeedView: SettingsIntent
    data object CollectMovieList: SettingsIntent
    data object CollectAppServiceData: SettingsIntent
    data object CollectNotificationsEnabled: SettingsIntent
    data object CollectBiometricFeatureEnabled: SettingsIntent
    data object CollectBiometricEnabled: SettingsIntent
    data object CollectScreenshotBlockEnabled: SettingsIntent
    data object FetchUpdateAvailable: SettingsIntent
    data object BackClick: SettingsIntent
    data object ReviewClick: SettingsIntent
    data object UpdateClick: SettingsIntent
    data class SelectLanguage(val language: AppLanguage): SettingsIntent
    data class SelectTheme(val theme: AppTheme): SettingsIntent
    data class SelectFeedView(val feedView: FeedView): SettingsIntent
    data class SelectMovieList(val movieList: MovieList): SettingsIntent
    data class SetDynamicColors(val value: Boolean): SettingsIntent
    data class SetPaletteKey(val paletteKey: Int): SettingsIntent
    data class SetSeedColor(val seedColor: Int): SettingsIntent
    data class SetBiometricEnabled(val enabled: Boolean): SettingsIntent
    data class SetScreenshotBlockEnabled(val enabled: Boolean): SettingsIntent
    data class SetUpdateAvailable(val state: Boolean): SettingsIntent
}