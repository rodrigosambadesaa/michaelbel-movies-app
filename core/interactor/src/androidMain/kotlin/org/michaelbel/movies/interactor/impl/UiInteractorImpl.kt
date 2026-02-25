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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.material.color.DynamicColors
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.common.ktx.appNotificationSettingsIntent
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.network.config.formatBackdropImage
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.appicon.IconAlias
import org.michaelbel.movies.ui.appicon.enabledIcon
import org.michaelbel.movies.ui.appicon.setIcon
import org.michaelbel.movies.ui.ktx.currentGrammaticalGender
import org.michaelbel.movies.ui.ktx.displayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.supportSetRequestedApplicationGrammaticalGender

class UiInteractorImpl(
    private val context: Context
): UiInteractor {

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
        get() = true

    override val isTileFeatureEnabled: Boolean
        @ChecksSdkIntAtLeast(33) get() = Build.VERSION.SDK_INT >= 33

    override val isAppIconFeatureEnabled: Boolean = true

    override val isScreenshotFeatureEnabled: Boolean = true

    override val isGithubFeatureEnabled: Boolean = true

    override val isTelegramFeatureEnabled: Boolean = true

    override val isReviewAppFeatureEnabled: Boolean = true

    override val isUpdateAppFeatureEnabled: Boolean = true

    override val isAboutFeatureEnabled: Boolean = true

    override val isDetailsGalleryFeatureEnabled: Boolean = true

    override val isDetailsShareFeatureEnabled: Boolean = true

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

    @Composable
    override fun detailsPaletteEffect(
        movie: MoviePojo,
        placeholder: Boolean,
        shouldGenerateColors: Boolean,
        onGenerateColors: (Int, Int?, Int?) -> Unit
    ) {
        if (!shouldGenerateColors || placeholder) return

        val context = LocalContext.current
        LaunchedEffect(movie.backdropPath.formatBackdropImage) {
            val imageRequest = ImageLoader(context).execute(
                ImageRequest.Builder(context)
                    .data(movie.backdropPath.formatBackdropImage)
                    .allowHardware(false)
                    .build()
            )
            if (imageRequest is SuccessResult) {
                val bitmap = imageRequest.drawable.toBitmap()
                Palette.from(bitmap).generate { palette ->
                    if (palette != null) {
                        onGenerateColors(
                            movie.movieId,
                            palette.vibrantSwatch?.rgb,
                            palette.vibrantSwatch?.bodyTextColor
                        )
                    }
                }
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
