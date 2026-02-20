package org.michaelbel.movies

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.michaelbel.movies.feed.event.FeedAppEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.michaelbel.movies.common.ktx.launchAndCollectIn
import org.michaelbel.movies.main.MainContent
import org.michaelbel.movies.main.MainViewModel
import org.michaelbel.movies.main.mainnav.event.MainNavAppEvent
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.resolveNotificationPreferencesIntent
import org.michaelbel.movies.ui.ktx.setScreenshotBlockEnabled
import org.michaelbel.movies.ui.ktx.supportRegisterScreenCaptureCallback
import org.michaelbel.movies.ui.ktx.supportUnregisterScreenCaptureCallback
import org.michaelbel.movies.ui.navigation.MainDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.shortcuts.INTENT_ACTION_SEARCH
import org.michaelbel.movies.ui.shortcuts.INTENT_ACTION_SETTINGS
import org.michaelbel.movies.ui.shortcuts.installShortcuts
import org.michaelbel.movies.ui.theme.MoviesTheme

/**
 * BiometricPrompt needs FragmentActivity.
 */
internal class MainActivity: FragmentActivity() {

    private val viewModel: MainViewModel by viewModel()

    private val screenCaptureCallback: Any
        get() {
            return if (Build.VERSION.SDK_INT >= 34) {
                ScreenCaptureCallback {}
            } else {
                Unit
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply { setKeepOnScreenCondition { viewModel.splashLoading.value } }
        super.onCreate(savedInstanceState)
        installShortcuts()
        setContent {
            val themeData by viewModel.themeData.collectAsStateCommon()

            MoviesTheme(
                themeData = themeData,
                enableEdgeToEdge = { statusBarStyle, navigationBarStyle ->
                    enableEdgeToEdge(statusBarStyle as SystemBarStyle, navigationBarStyle as SystemBarStyle)
                }
            ) {
                MainContent(
                    onRequestReview = { viewModel.requestReview(this) },
                    onRequestUpdate = { viewModel.requestUpdate(this) }
                )
            }
        }
        resolveNotificationPreferencesIntent()
        resolveShortcutIntent(intent)
        viewModel.run {
            isScreenshotBlockEnabled.launchAndCollectIn(this@MainActivity) { enabled ->
                window.setScreenshotBlockEnabled(enabled)
            }
            authenticateFlow.launchAndCollectIn(this@MainActivity) { authenticate(this@MainActivity) }
            cancelFlow.launchAndCollectIn(this@MainActivity) { finish() }
        }
    }

    override fun onStart() {
        super.onStart()
        supportRegisterScreenCaptureCallback(screenCaptureCallback)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        resolveShortcutIntent(intent)
    }

    override fun onStop() {
        super.onStop()
        supportUnregisterScreenCaptureCallback(screenCaptureCallback)
    }

    private fun resolveShortcutIntent(intent: Intent?) {
        when (intent?.dataString) {
            INTENT_ACTION_SEARCH -> {
                lifecycleScope.launch {
                    MainNavigator.forward(MainDestination())
                    MainNavAppEvent.push(MainNavAppEvent.Event.OpenFeed)
                    FeedAppEvent.push(FeedAppEvent.Event.OpenSearch)
                }
            }
            INTENT_ACTION_SETTINGS -> {
                lifecycleScope.launch {
                    MainNavigator.forward(MainDestination())
                    MainNavAppEvent.push(MainNavAppEvent.Event.OpenSettings)
                }
            }
        }
    }
}
