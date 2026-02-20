package org.michaelbel.movies.interactor.impl

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.color.DynamicColors
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.common.ktx.appNotificationSettingsIntent
import org.michaelbel.movies.interactor.SettingsUiInteractor
import org.michaelbel.movies.ui.appicon.IconAlias
import org.michaelbel.movies.ui.appicon.enabledIcon
import org.michaelbel.movies.ui.appicon.setIcon
import org.michaelbel.movies.ui.ktx.currentGrammaticalGender
import org.michaelbel.movies.ui.ktx.displayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.supportSetRequestedApplicationGrammaticalGender

class SettingsUiInteractorImpl(
    private val context: Context
): SettingsUiInteractor {

    override val isNavigationIconVisible: Boolean = false

    override val isLanguageFeatureEnabled: Boolean = true

    override val isThemeFeatureEnabled: Boolean = true

    override val isFeedViewFeatureEnabled: Boolean = true

    override val isMovieListFeatureEnabled: Boolean = true

    override val isGenderFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(34) get() = Build.VERSION.SDK_INT >= 34

    override val isDynamicColorsFeatureEnabled: Boolean
        get() = DynamicColors.isDynamicColorAvailable()

    override val isPaletteColorsFeatureEnabled: Boolean = true

    override val isNotificationsFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(33) get() = Build.VERSION.SDK_INT >= 33

    override val isBiometricFeatureEnabled: Boolean = true

    override val isWidgetFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(26) get() = Build.VERSION.SDK_INT >= 26

    override val isTileFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(33) get() = Build.VERSION.SDK_INT >= 33

    override val isAppIconFeatureEnabled: Boolean = true

    override val isScreenshotFeatureEnabled: Boolean = true

    override val isGithubFeatureEnabled: Boolean = true

    override val isReviewAppFeatureEnabled: Boolean = true

    override val isUpdateAppFeatureEnabled: Boolean = true

    override val isAboutFeatureEnabled: Boolean = true

    override val settingsWindowInsets: WindowInsets
        @Composable get() = displayCutoutWindowInsets

    override val bottomBarModifier: Modifier
        get() = Modifier

    @Composable
    override fun navigateToAppNotificationSettings(): () -> Unit {
        val resultContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        val context = LocalContext.current
        return { resultContract.launch(context.appNotificationSettingsIntent) }
    }

    @Composable
    override fun rememberPostNotificationsPermissionHandler(
        areNotificationsEnabled: Boolean,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ): () -> Unit {
        val context = LocalContext.current
        val activity = LocalActivity.current
        val postNotificationsPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            when {
                granted -> onPermissionGranted()
                else -> {
                    if (Build.VERSION.SDK_INT >= 33) {
                        val shouldRequest = activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                        if (shouldRequest == false) {
                            onPermissionDenied()
                        }
                    }
                }
            }
        }
        val resultContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        return {
            when {
                areNotificationsEnabled -> resultContract.launch(context.appNotificationSettingsIntent)
                Build.VERSION.SDK_INT >= 33 -> postNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override val enabledIcon: IconAlias
        get() = context.enabledIcon

    override fun setIcon(iconAlias: IconAlias) {
        context.setIcon(iconAlias)
    }

    override val grammaticalGender: SealedString
        get() = context.currentGrammaticalGender

    override fun setGrammaticalGender(grammaticalGender: Int) {
        context.supportSetRequestedApplicationGrammaticalGender(grammaticalGender)
    }
}
