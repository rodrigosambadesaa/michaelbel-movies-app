@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class ThemeDataFlowUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): FlowUseCase<Boolean, ThemeData>(dispatchers.io) {

    override fun execute(defaultDynamicColorsEnabled: Boolean): Flow<ThemeData> {
        return combine(
            preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceThemeKey),
            preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceDynamicColorsKey),
            preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferencePaletteColorsKey),
            preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferencePaletteKey),
            preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceSeedColorKey)
        ) { themeName, dynamicColors, paletteColors, paletteKey, seedColor ->
            ThemeData(
                appTheme = AppTheme.transform(themeName ?: AppTheme.FollowSystem.toString()),
                dynamicColors = dynamicColors ?: defaultDynamicColorsEnabled,
                paletteColors = paletteColors.orEmpty(),
                paletteKey = paletteKey ?: ThemeData.STYLE_TONAL_SPOT,
                seedColor = seedColor ?: ThemeData.DEFAULT_SEED_COLOR
            )
        }
    }
}
