package org.michaelbel.movies.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.GrammaticalInflectionManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import org.michaelbel.movies.common.gender.GrammaticalGender
import org.michaelbel.movies.ui.navigation.INTENT_ACTION_SETTINGS

val Context.currentGrammaticalGender: GrammaticalGender
    get() {
        return when {
            Build.VERSION.SDK_INT >= 34 -> {
                val grammaticalInflectionManager = ContextCompat.getSystemService(this, GrammaticalInflectionManager::class.java) ?: return GrammaticalGender.NotSpecified()
                val grammaticalGender = grammaticalInflectionManager.applicationGrammaticalGender
                GrammaticalGender.transform(grammaticalGender)
            }
            else -> GrammaticalGender.NotSpecified()
        }
    }

fun Context.supportSetRequestedApplicationGrammaticalGender(grammaticalGender: Int) {
    if (Build.VERSION.SDK_INT >= 34) {
        val grammaticalInflectionManager = ContextCompat.getSystemService(this, GrammaticalInflectionManager::class.java) ?: return
        grammaticalInflectionManager.setRequestedApplicationGrammaticalGender(grammaticalGender)
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

@SuppressLint("InlinedApi")
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

fun Window.setScreenshotBlockEnabled(enabled: Boolean) {
    if (enabled) {
        setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

fun Activity.supportRegisterScreenCaptureCallback(screenCaptureCallback: Any) {
    if (Build.VERSION.SDK_INT >= 34) {
        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback as Activity.ScreenCaptureCallback)
    }
}

fun Activity.supportUnregisterScreenCaptureCallback(screenCaptureCallback: Any) {
    if (Build.VERSION.SDK_INT >= 34) {
        try {
            unregisterScreenCaptureCallback(screenCaptureCallback as Activity.ScreenCaptureCallback)
        } catch (_: Exception) {}
    }
}
