package org.michaelbel.movies.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences
import org.michaelbel.movies.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val preferences: MoviesPreferences
): SettingsRepository {

    override val currentTheme: Flow<AppTheme>
        get() = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceThemeKey).map { name -> AppTheme.transform(name ?: AppTheme.FollowSystem.toString()) }

    override val currentFeedView: Flow<FeedView>
        get() = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceFeedViewKey).map { name -> FeedView.transform(name ?: FeedView.FeedList.toString()) }

    override val currentMovieList: Flow<MovieList>
        get() = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceMovieListKey).map { className -> MovieList.transform(className ?: MovieList.NowPlaying().toString()) }

    override val dynamicColors: Flow<Boolean?>
        get() = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceDynamicColorsKey)

    override val themeData: Flow<ThemeData>
        get() {
            return combine(
                preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceThemeKey),
                preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceDynamicColorsKey),
                preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferencePaletteColorsKey),
                preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferencePaletteKey),
                preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceSeedColorKey)
            ) { themeName, dynamicColors, paletteColors, paletteKey, seedColor ->
                ThemeData(
                    appTheme = AppTheme.transform(themeName ?: AppTheme.FollowSystem.toString()),
                    dynamicColors = dynamicColors ?: ThemeData.Default.dynamicColors,
                    paletteColors = paletteColors.orEmpty(),
                    paletteKey = paletteKey ?: ThemeData.STYLE_TONAL_SPOT,
                    seedColor = seedColor ?: ThemeData.DEFAULT_SEED_COLOR
                )
            }
        }

    override val isBiometricEnabled: Flow<Boolean>
        get() = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceBiometricKey).map { it.orEmpty() }

    override val isScreenshotBlockEnabled: Flow<Boolean>
        get() = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceScreenshotBlockKey).map { it.orEmpty() }

    override suspend fun isBiometricEnabledAsync(): Boolean {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceBiometricKey).orEmpty()
    }

    override suspend fun setPaletteColors(value: Boolean) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferencePaletteColorsKey, value)
    }

    override suspend fun setPaletteKey(paletteKey: Int) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferencePaletteKey, paletteKey)
    }

    override suspend fun setSeedColor(seedColor: Int) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceSeedColorKey, seedColor)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceBiometricKey, enabled)
    }

    override suspend fun setScreenshotBlockEnabled(enabled: Boolean) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceScreenshotBlockKey, enabled)
    }
}
