package org.michaelbel.movies.interactor.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.michaelbel.movies.analytics.MoviesAnalytics
import org.michaelbel.movies.analytics.event.ChangeDynamicColorsEvent
import org.michaelbel.movies.analytics.event.SelectFeedViewEvent
import org.michaelbel.movies.analytics.event.SelectMovieListEvent
import org.michaelbel.movies.analytics.event.SelectThemeEvent
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.interactor.SettingsInteractor
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.repository.SettingsRepository

class SettingsInteractorImpl(
    private val dispatchers: SharedDispatchers,
    private val settingsRepository: SettingsRepository,
    private val uiInteractor: UiInteractor,
    private val analytics: MoviesAnalytics
): SettingsInteractor {

    override val currentTheme: Flow<AppTheme> = settingsRepository.currentTheme

    override val currentFeedView: Flow<FeedView> = settingsRepository.currentFeedView

    override val currentMovieList: Flow<MovieList> = settingsRepository.currentMovieList

    override val themeData: Flow<ThemeData> = combine(
        settingsRepository.themeData,
        settingsRepository.dynamicColors
    ) { themeData, dynamicColors ->
        when (dynamicColors) {
            null -> themeData.copy(dynamicColors = uiInteractor.defaultDynamicColorsEnabled)
            else -> themeData
        }
    }

    override val isBiometricEnabled: Flow<Boolean> = settingsRepository.isBiometricEnabled

    override val isScreenshotBlockEnabled: Flow<Boolean> = settingsRepository.isScreenshotBlockEnabled

    override suspend fun isBiometricEnabledAsync(): Boolean {
        return settingsRepository.isBiometricEnabledAsync()
    }

    override suspend fun selectTheme(appTheme: AppTheme) {
        withContext(dispatchers.ui) {
            settingsRepository.selectTheme(appTheme)
            analytics.logEvent(SelectThemeEvent(appTheme.toString()))
        }
    }

    override suspend fun selectFeedView(feedView: FeedView) {
        withContext(dispatchers.ui) {
            settingsRepository.selectFeedView(feedView)
            analytics.logEvent(SelectFeedViewEvent(feedView.toString()))
        }
    }

    override suspend fun selectMovieList(movieList: MovieList) {
        withContext(dispatchers.ui) {
            settingsRepository.selectMovieList(movieList)
            analytics.logEvent(SelectMovieListEvent(movieList.toString()))
        }
    }

    override suspend fun setDynamicColors(value: Boolean) {
        withContext(dispatchers.main) {
            settingsRepository.setDynamicColors(value)
            analytics.logEvent(ChangeDynamicColorsEvent(value))
        }
    }

    override suspend fun setPaletteColors(value: Boolean) {
        withContext(dispatchers.main) { settingsRepository.setPaletteColors(value) }
    }

    override suspend fun setPaletteKey(paletteKey: Int) {
        withContext(dispatchers.ui) { settingsRepository.setPaletteKey(paletteKey) }
    }

    override suspend fun setSeedColor(seedColor: Int) {
        withContext(dispatchers.ui) { settingsRepository.setSeedColor(seedColor) }
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        withContext(dispatchers.main) { settingsRepository.setBiometricEnabled(enabled) }
    }

    override suspend fun setScreenshotBlockEnabled(enabled: Boolean) {
        withContext(dispatchers.main) { settingsRepository.setScreenshotBlockEnabled(enabled) }
    }

    override suspend fun resetSettings() {
        withContext(dispatchers.main) { settingsRepository.resetSettings() }
    }
}
