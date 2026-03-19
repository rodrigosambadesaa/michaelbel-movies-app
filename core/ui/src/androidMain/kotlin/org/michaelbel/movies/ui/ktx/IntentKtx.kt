package org.michaelbel.movies.ui.ktx

import android.Manifest
import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.ui.icons.MoviesAndroidIcons
import org.michaelbel.movies.ui.navigation.INTENT_ACTION_SETTINGS
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.tile.MoviesTileService

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
    val tileTitleLabel = stringResource(MoviesStrings.tile_title)
    val tileMessage = stringResource(MoviesStrings.settings_tile_error_already_added)
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

fun Context.navigateToShareText(text: String, title: String) {
    Intent().apply {
        type = "text/plain"
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
    }.also { intent: Intent ->
        startActivity(Intent.createChooser(intent, title))
    }
}

fun Context.navigateToImageUri(uri: Uri) {
    Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/jpg")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }.also { intent ->
        startActivity(intent)
    }
}

val Context.appNotificationSettingsIntent: Intent
    get() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

fun Activity.resolveNotificationPreferencesIntent() {
    val categories = intent.categories
    if (categories != null && categories.isNotEmpty()) {
        val isCategoryNotificationPreferences = categories.first() == "android.intent.category.NOTIFICATION_PREFERENCES"
        if (isCategoryNotificationPreferences) {
            startActivity(Intent(Intent.ACTION_VIEW, INTENT_ACTION_SETTINGS.toUri()))
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
fun rememberNavigateToAppSettings(): () -> Unit {
    val context = LocalContext.current
    val appSettingsContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        "package:${context.packageName}".toUri()
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return remember { { appSettingsContract.launch(intent) } }
}

@Composable
fun rememberNavigateToAppOpenByDefaultSettings(): () -> Unit {
    val context = LocalContext.current
    val appSettingsContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val fallback = rememberNavigateToAppSettings()
    val intent = Intent(
        Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
        "package:${context.packageName}".toUri()
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return remember(appSettingsContract, intent, fallback) {
        { runCatching { appSettingsContract.launch(intent) }.getOrElse { fallback() } }
    }
}

@Composable
fun rememberNavigateToDeveloperSettings(): () -> Unit {
    val appSettingsContract = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    return remember { { appSettingsContract.launch(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)) } }
}

@Composable
fun rememberRequestNotificationPermission(
    onGranted: () -> Unit = {}
): () -> Unit {
    if (Build.VERSION.SDK_INT >= 33) {
        val context = LocalContext.current
        val navigateToAppSettings = rememberNavigateToAppSettings()
        val cameraPermissionContract = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            val shouldRequest = (context as Activity).shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            when {
                granted -> onGranted()
                !granted && !shouldRequest -> navigateToAppSettings()
            }
        }
        return remember {
            {
                when {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED -> {
                        cameraPermissionContract.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    else -> onGranted()
                }
            }
        }
    } else {
        return {}
    }
}
