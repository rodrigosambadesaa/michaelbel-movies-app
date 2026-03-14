package org.michaelbel.movies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlinx.coroutines.flow.filterNotNull
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.di.appKoinModule
import org.michaelbel.movies.main.MainScreen
import org.michaelbel.movies.main.MainViewModel
import org.michaelbel.movies.main.intent.MainIntent
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun IosMainContent() {
    KoinApplication(
        application = {
            modules(appKoinModule)
        }
    ) {
        val viewModel = koinInject<MainViewModel>()
        val state by viewModel.stateFlow.collectAsStateCommon()

        LaunchedEffect(viewModel) {
            IosIncomingUrlStore.url
                .filterNotNull()
                .collect { url ->
                    resolveIncomingUrl(url, viewModel::dispatch)
                    IosIncomingUrlStore.clear(url)
                }
        }

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
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

private fun resolveIncomingUrl(
    url: String,
    dispatch: (MainIntent) -> Unit
) {
    when {
        url.startsWith("movies://redirect_url") -> {
            val requestToken = url.queryParameter("request_token")?.takeIf(String::isNotBlank)
            val approved = when (url.queryParameter("approved")?.lowercase()) {
                "true", "1" -> true
                "false", "0" -> false
                else -> null
            }
            if (requestToken != null && approved != null) {
                dispatch(MainIntent.NavigateToMain(requestToken, approved))
            }
        }
    }
}

private fun String.queryParameter(name: String): String? {
    val query = substringAfter('?', "")
    if (query.isEmpty()) return null

    return query
        .split('&')
        .firstNotNullOfOrNull { item ->
            val parts = item.split('=', limit = 2)
            when {
                parts.size != 2 || parts[0] != name -> null
                else -> parts[1]
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
fun withViewModelStoreOwner(content: @Composable () -> Unit) {
    if (LocalViewModelStoreOwner.current != null) {
        content()
    } else {
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides rememberComposeViewModelStoreOwner(),
            content = content
        )
    }
}
