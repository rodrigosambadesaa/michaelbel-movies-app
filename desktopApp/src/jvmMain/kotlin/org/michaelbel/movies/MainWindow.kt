package org.michaelbel.movies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.di.appKoinModule
import org.michaelbel.movies.main.MainScreen
import org.michaelbel.movies.main.MainViewModel
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.theme.MoviesTheme
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(600.dp, 500.dp)
        ),
        title = "Movies",
        icon = painterResource(MoviesIcons.LauncherRed),
        alwaysOnTop = true,
        onKeyEvent = { false }
    ) {
        window.minimumSize = Dimension(600, 500)

        App()
    }
}

@Composable
private fun App() {
    KoinApplication(
        application = {
            modules(appKoinModule)
        }
    ) {
        val viewModel = koinInject<MainViewModel>()
        val state by viewModel.stateFlow.collectAsStateCommon()

        withViewModelStoreOwner {
            MoviesTheme(
                themeData = ThemeData(
                    appTheme = state.themeData.appTheme,
                    dynamicColors = false,
                    paletteColors = state.themeData.paletteColors,
                    paletteKey = state.themeData.paletteKey,
                    seedColor = state.themeData.seedColor
                ),
                theme = state.themeData.appTheme,
                enableEdgeToEdge = { _,_ -> }
            ) {
                MainScreen()
            }
        }
    }
}

private class ComposeViewModelStoreOwner: ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
    fun dispose() { viewModelStore.clear() }
}

@Composable
private fun rememberComposeViewModelStoreOwner(): ViewModelStoreOwner {
    val viewModelStoreOwner = remember { ComposeViewModelStoreOwner() }
    DisposableEffect(viewModelStoreOwner) {
        onDispose { viewModelStoreOwner.dispose() }
    }
    return viewModelStoreOwner
}

@Composable
internal fun withViewModelStoreOwner(content: @Composable () -> Unit) {
    if (LocalViewModelStoreOwner.current != null) {
        content()
    } else {
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides rememberComposeViewModelStoreOwner(),
            content = content
        )
    }
}
