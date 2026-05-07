package org.michaelbel.movies.interactor.impl

import android.Manifest
import android.annotation.SuppressLint
import java.util.Locale
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.common.ktx.appNotificationSettingsIntent
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.ui.appicon.IconAlias
import org.michaelbel.movies.ui.appicon.enabledIcon
import org.michaelbel.movies.ui.appicon.setIcon
import org.michaelbel.movies.ui.currentGrammaticalGender
import org.michaelbel.movies.ui.rememberNavigateToAppOpenByDefaultSettings
import org.michaelbel.movies.ui.rememberNavigateToAppSettings
import org.michaelbel.movies.ui.rememberNavigateToDeveloperSettings
import org.michaelbel.movies.ui.supportSetRequestedApplicationGrammaticalGender

class UiInteractorImpl(
    private val context: Context
): UiInteractor {

    override val isLanguageFeatureEnabled: Boolean = true

    override val isThemeFeatureEnabled: Boolean = true

    override val isFeedViewFeatureEnabled: Boolean = true

    override val isFaveFeatureEnabled: Boolean = true

    override val isMovieListFeatureEnabled: Boolean = true

    override val isGenderFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(34) get() = Build.VERSION.SDK_INT >= 34

    override val isDynamicColorsFeatureEnabled: Boolean
        get() = DynamicColors.isDynamicColorAvailable()

    override val defaultDynamicColorsEnabled: Boolean = false

    override val isPaletteColorsFeatureEnabled: Boolean = true

    override val isNotificationsFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(33) get() = Build.VERSION.SDK_INT >= 33

    override val isDoNotDisturbFeatureEnabled: Boolean = true

    override val isBatteryOptimizationFeatureEnabled: Boolean = true

    override val isBiometricFeatureEnabled: Boolean = true

    override val isWidgetFeatureEnabled: Boolean = true

    override val isTileFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(33) get() = Build.VERSION.SDK_INT >= 33

    override val isAppIconFeatureEnabled: Boolean = true

    override val isAppOpenByDefaultFeatureEnabled: Boolean = true

    override val isScreenshotFeatureEnabled: Boolean = true

    override val isEyeDropperFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(37) get() = Build.VERSION.SDK_INT >= 37

    override val isGithubFeatureEnabled: Boolean = true

    override val isTelegramFeatureEnabled: Boolean = true

    override val isReviewAppFeatureEnabled: Boolean = true

    override val isUpdateAppFeatureEnabled: Boolean = true

    override val isAboutFeatureEnabled: Boolean = true

    override val isSettingsResetFeatureEnabled: Boolean = true

    override val isFeedAuthIconFeatureEnabled: Boolean = true

    override val isFeedVoiceInputFeatureEnabled: Boolean = true

    override val isDetailsFavoriteFeatureEnabled: Boolean = true

    override val isDetailsGalleryFeatureEnabled: Boolean = true

    override val isDetailsShareFeatureEnabled: Boolean = true

    override val isPageFailureButtonVisible: Boolean
        @ChecksSdkIntAtLeast(29) get() = Build.VERSION.SDK_INT >= 29

    @Composable
    override fun navigateToAppNotificationSettings(): () -> Unit {
        val resultContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        val context = LocalContext.current
        return { resultContract.launch(context.appNotificationSettingsIntent) }
    }

    @Composable
    override fun navigateToDoNotDisturbSettings(): () -> Unit {
        val resultContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        val intent = remember { Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS) }
        return remember(resultContract, intent) { { resultContract.launch(intent) } }
    }

    override val isIgnoringBatteryOptimizations: Boolean
        get() {
            val powerManager = ContextCompat.getSystemService(context, PowerManager::class.java)
            return powerManager?.isIgnoringBatteryOptimizations(context.packageName) == true
        }

    @SuppressLint("BatteryLife")
    @Composable
    override fun requestIgnoreBatteryOptimizations(): () -> Unit {
        val context = LocalContext.current
        val resultContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        val intent = remember(context.packageName) {
            Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                "package:${context.packageName}".toUri()
            )
        }
        return remember(resultContract, intent) { { resultContract.launch(intent) } }
    }

    @Composable
    override fun navigateToBatteryOptimizationSettings(): () -> Unit {
        val resultContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        val intent = remember { Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS) }
        return remember(resultContract, intent) { { resultContract.launch(intent) } }
    }

    @Composable
    override fun navigateToAppSettings(): () -> Unit {
        return rememberNavigateToAppSettings()
    }

    @Composable
    override fun navigateToAppOpenByDefaultSettings(): () -> Unit {
        return rememberNavigateToAppOpenByDefaultSettings()
    }

    @Composable
    override fun navigateToDeveloperSettings(): () -> Unit {
        return rememberNavigateToDeveloperSettings()
    }

    @Composable
    override fun rememberCopyToClipboardHandler(): (String) -> Unit {
        val clipboard = LocalClipboard.current
        val scope = rememberCoroutineScope()
        return remember(clipboard, scope) {
            { text ->
                scope.launch {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("", text)))
                }
            }
        }
    }

    @Composable
    override fun rememberPostNotificationsPermissionHandler(
        enabled: Boolean,
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
                enabled -> resultContract.launch(context.appNotificationSettingsIntent)
                Build.VERSION.SDK_INT >= 33 -> postNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @Composable
    override fun rememberEyeDropperHandler(): (() -> Unit) {
        val context = LocalContext.current
        val intent = remember { Intent("android.intent.action.OPEN_EYE_DROPPER") }
        val resultContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.hasExtra("android.intent.extra.COLOR") == true) {
                val color = result.data?.getIntExtra("android.intent.extra.COLOR", Color.BLACK).orEmpty()
                val hex = String.format(Locale.US, "#%06X", 0xFFFFFF and color)
                Toast.makeText(context, hex, Toast.LENGTH_SHORT).show()
            }
        }
        return remember(resultContract, intent) { { resultContract.launch(intent) } }
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
