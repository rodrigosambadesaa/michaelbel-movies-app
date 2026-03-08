package org.michaelbel.movies.interactor.impl

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.common.gender.GrammaticalGender
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.appicon.IconAlias

class UiInteractorImpl: UiInteractor {

    override val isLanguageFeatureEnabled: Boolean = false

    override val isThemeFeatureEnabled: Boolean = true

    override val isFeedViewFeatureEnabled: Boolean = true

    override val isFaveFeatureEnabled: Boolean = false

    override val isMovieListFeatureEnabled: Boolean = true

    override val isGenderFeatureEnabled: Boolean = false

    override val isDynamicColorsFeatureEnabled: Boolean = false

    override val defaultDynamicColorsEnabled: Boolean = false

    override val isPaletteColorsFeatureEnabled: Boolean = false

    override val isNotificationsFeatureEnabled: Boolean = false

    override val isBatteryOptimizationFeatureEnabled: Boolean = false

    override val isBiometricFeatureEnabled: Boolean = false

    override val isWidgetFeatureEnabled: Boolean = false

    override val isTileFeatureEnabled: Boolean = false

    override val isAppIconFeatureEnabled: Boolean = false

    override val isScreenshotFeatureEnabled: Boolean = false

    override val isGithubFeatureEnabled: Boolean = true

    override val isTelegramFeatureEnabled: Boolean = true

    override val isReviewAppFeatureEnabled: Boolean = false

    override val isUpdateAppFeatureEnabled: Boolean = false

    override val isAboutFeatureEnabled: Boolean = true

    override val isDetailsGalleryFeatureEnabled: Boolean = false

    override val isDetailsShareFeatureEnabled: Boolean = true

    override val isPageFailureButtonVisible: Boolean = false

    override val settingsWindowInsets: WindowInsets
        @Composable get() = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)

    override val bottomBarModifier: Modifier
        get() = Modifier.navigationBarsPadding()

    @Composable
    override fun navigateToAppNotificationSettings(): () -> Unit {
        return {}
    }

    override val isIgnoringBatteryOptimizations: Boolean = false

    @Composable
    override fun requestIgnoreBatteryOptimizations(): () -> Unit {
        return {}
    }

    @Composable
    override fun navigateToBatteryOptimizationSettings(): () -> Unit {
        return {}
    }

    @Composable
    override fun navigateToAppSettings(): () -> Unit {
        return {}
    }

    @Composable
    override fun navigateToDeveloperSettings(): () -> Unit {
        return {}
    }

    @Composable
    override fun rememberCopyToClipboardHandler(): (String) -> Unit {
        return { _: String -> }
    }

    @Composable
    override fun rememberPostNotificationsPermissionHandler(
        areNotificationsEnabled: Boolean,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ): () -> Unit {
        return {}
    }

    @Composable
    override fun DetailsPaletteEffect(
        movie: MoviePojo,
        placeholder: Boolean,
        shouldGenerateColors: Boolean,
        onGenerateColors: (Int, Int?, Int?) -> Unit
    ) {}

    override val enabledIcon: IconAlias = IconAlias.Red

    override fun setIcon(iconAlias: IconAlias) {}

    override val grammaticalGender: SealedString = GrammaticalGender.NotSpecified()

    override fun setGrammaticalGender(grammaticalGender: Int) {}
}
