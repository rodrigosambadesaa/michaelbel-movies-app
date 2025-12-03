package org.michaelbel.movies.settings.model

import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.common.version.AppVersionData

data class SettingsModel(
    val themeData: ThemeData = ThemeData.Default,
    val feedView: FeedView = FeedView.FeedList,
    val movieList: MovieList = MovieList.NowPlaying(),
    val isReviewFeatureEnabled: Boolean = false,
    val isUpdateFeatureEnabled: Boolean = false,
    val areNotificationsEnabled: Boolean = false,
    val appVersionData: AppVersionData = AppVersionData.Empty,
    val isBiometricFeatureEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isScreenshotBlockEnabled: Boolean = false,
    val isUpdateAvailable: Boolean = false
): Model