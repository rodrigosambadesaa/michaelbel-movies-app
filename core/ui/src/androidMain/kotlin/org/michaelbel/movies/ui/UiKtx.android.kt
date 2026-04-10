package org.michaelbel.movies.ui

import android.app.Application
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.michaelbel.movies.ui.entity.MovieCardStyle
import org.michaelbel.movies.ui.icons.MoviesAndroidIcons
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.tile.MoviesTileService
import kotlin.coroutines.CoroutineContext

actual val isDebug: Boolean
    get() = runCatching {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val application = activityThreadClass
            .getDeclaredMethod("currentApplication")
            .invoke(null) as? Application
        application?.applicationInfo?.flags?.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }.getOrDefault(false)

actual fun statusBarStyle(detectDarkMode: Boolean): Any {
    return SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()) { detectDarkMode }
}

@Composable
actual fun navigationBarStyle(detectDarkMode: Boolean): Any {
    val configuration = LocalConfiguration.current
    val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return when (currentNightMode) {
        Configuration.UI_MODE_NIGHT_NO -> SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
        Configuration.UI_MODE_NIGHT_YES -> SystemBarStyle.dark(Color.Transparent.toArgb())
        else -> SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()) { detectDarkMode }
    }
}

actual val displayCutoutWindowInsets: WindowInsets
    @Composable get() = if (isNavigationBar) WindowInsets(0, 0, 0, 0) else WindowInsets.displayCutout

actual val dialogUsePlatformDefaultWidth: Boolean
    @Composable get() {
        val configuration = LocalConfiguration.current
        return configuration.smallestScreenWidthDp >= 600
    }

actual val bottomSheetUsePlatformDefaultWidth: Boolean
    get() = false

@Composable
actual fun pageStaggeredGridCells(style: MovieCardStyle): StaggeredGridCells {
    return StaggeredGridCells.Fixed(2)
}

actual val modifierDisplayCutoutWindowInsets: Modifier
    @Composable get() = Modifier.windowInsetsPadding(displayCutoutWindowInsets)

actual val SettingsGenderText: String
    @Composable get() = stringResource(R.string.settings_gender)

@Composable
actual fun rememberSpeechRecognitionLauncher(onInputText: (String) -> Unit): () -> Unit {
    val speechRecognizeContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        val data = activityResult.data
        val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
            results[0]
        }
        if (!spokenText.isNullOrEmpty()) {
            onInputText(spokenText)
        }
    }

    return {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        speechRecognizeContract.launch(intent)
    }
}

@Composable
actual fun shareText(text: String, title: String): () -> Unit {
    val context = LocalContext.current
    return {
        context.navigateToShareText(text, title)
    }
}

@Composable
actual fun navigateToImageUri(): (uri: String) -> Unit {
    val context = LocalContext.current
    return { uri ->
        context.navigateToImageUri(uri.toUri())
    }
}

@Composable
actual fun requestTileService(onSuccess: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val tileTitleLabel = org.jetbrains.compose.resources.stringResource(MoviesStrings.tile_title)
    val tileMessage =
        org.jetbrains.compose.resources.stringResource(MoviesStrings.settings_tile_error_already_added)
    return {
        if (Build.VERSION.SDK_INT >= 33) {
            val statusBarManager = ContextCompat.getSystemService(context, StatusBarManager::class.java)
            statusBarManager?.requestAddTileService(
                ComponentName(context, MoviesTileService::class.java),
                tileTitleLabel,
                Icon.createWithResource(context, MoviesAndroidIcons.MovieFilter24),
                context.mainExecutor
            ) { result ->
                when (result) {
                    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> {
                        onSuccess(tileMessage)
                    }
                }
            }
        }
    }
}

@Composable
actual fun rememberConnectivityClickHandler(): () -> Unit {
    if (Build.VERSION.SDK_INT >= 29) {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        return {
            val intent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            launcher.launch(intent)
        }
    } else {
        return {}
    }
}

@Composable
actual fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any?,
    key2: Any?,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, flow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}

@Composable
actual fun <T> StateFlow<T>.collectAsStateCommon(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State,
    context: CoroutineContext
): State<T> = collectAsStateWithLifecycle()

actual fun Modifier.onSecondaryClick(
    onClick: () -> Unit
): Modifier = this
