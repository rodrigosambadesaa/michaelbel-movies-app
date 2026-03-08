package org.michaelbel.movies.interactor

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.appicon.IconAlias

interface UiInteractor {

    val isLanguageFeatureEnabled: Boolean

    val isThemeFeatureEnabled: Boolean

    val isFeedViewFeatureEnabled: Boolean

    val isFaveFeatureEnabled: Boolean

    val isMovieListFeatureEnabled: Boolean

    val isGenderFeatureEnabled: Boolean

    val isDynamicColorsFeatureEnabled: Boolean

    val defaultDynamicColorsEnabled: Boolean

    val isPaletteColorsFeatureEnabled: Boolean

    val isNotificationsFeatureEnabled: Boolean

    val isBatteryOptimizationFeatureEnabled: Boolean

    val isBiometricFeatureEnabled: Boolean

    val isWidgetFeatureEnabled: Boolean

    val isTileFeatureEnabled: Boolean

    val isAppIconFeatureEnabled: Boolean

    val isScreenshotFeatureEnabled: Boolean

    val isGithubFeatureEnabled: Boolean

    val isTelegramFeatureEnabled: Boolean

    val isReviewAppFeatureEnabled: Boolean

    val isUpdateAppFeatureEnabled: Boolean

    val isAboutFeatureEnabled: Boolean

    val isDetailsGalleryFeatureEnabled: Boolean

    val isDetailsShareFeatureEnabled: Boolean

    val isPageFailureButtonVisible: Boolean

    @get:Composable
    val settingsWindowInsets: WindowInsets

    val bottomBarModifier: Modifier

    @Composable
    fun navigateToAppNotificationSettings(): () -> Unit

    val isIgnoringBatteryOptimizations: Boolean

    @Composable
    fun requestIgnoreBatteryOptimizations(): () -> Unit

    @Composable
    fun navigateToBatteryOptimizationSettings(): () -> Unit

    @Composable
    fun navigateToAppSettings(): () -> Unit

    @Composable
    fun navigateToDeveloperSettings(): () -> Unit

    @Composable
    fun rememberCopyToClipboardHandler(): (String) -> Unit

    @Composable
    fun rememberPostNotificationsPermissionHandler(
        areNotificationsEnabled: Boolean,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ): () -> Unit

    @Composable
    fun DetailsPaletteEffect(
        movie: MoviePojo,
        placeholder: Boolean,
        shouldGenerateColors: Boolean,
        onGenerateColors: (Int, Int?, Int?) -> Unit
    )

    val enabledIcon: IconAlias

    fun setIcon(iconAlias: IconAlias)

    val grammaticalGender: SealedString

    fun setGrammaticalGender(grammaticalGender: Int)
}
