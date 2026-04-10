package org.michaelbel.movies.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.michaelbel.movies.ui.entity.MovieCardStyle
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

expect val isDebug: Boolean

expect val displayCutoutWindowInsets: WindowInsets

expect val dialogUsePlatformDefaultWidth: Boolean

expect val bottomSheetUsePlatformDefaultWidth: Boolean

expect val modifierDisplayCutoutWindowInsets: Modifier

expect val SettingsGenderText: String

expect fun statusBarStyle(detectDarkMode: Boolean): Any

@Composable
expect fun navigationBarStyle(detectDarkMode: Boolean): Any

@Composable
expect fun pageStaggeredGridCells(style: MovieCardStyle): StaggeredGridCells

@Composable
expect fun rememberSpeechRecognitionLauncher(onInputText: (String) -> Unit): () -> Unit

@Composable
expect fun shareText(text: String, title: String): () -> Unit

@Composable
expect fun navigateToImageUri(): (uri: String) -> Unit

@Composable
expect fun requestTileService(onSuccess: (String) -> Unit): () -> Unit

@Composable
expect fun rememberConnectivityClickHandler(): () -> Unit

@Composable
expect fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
)

@Composable
expect fun <T> StateFlow<T>.collectAsStateCommon(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<T>

expect fun Modifier.onSecondaryClick(onClick: () -> Unit): Modifier
