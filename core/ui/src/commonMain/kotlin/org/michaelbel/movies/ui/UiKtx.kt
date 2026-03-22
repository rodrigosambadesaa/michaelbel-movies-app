package org.michaelbel.movies.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

expect val isDebug: Boolean

expect val isPortrait: Boolean

expect val isWideFoldableMode: Boolean

expect fun statusBarStyle(detectDarkMode: Boolean): Any

@Composable
expect fun navigationBarStyle(detectDarkMode: Boolean): Any

expect val displayCutoutWindowInsets: WindowInsets

expect val settingsTopAppBarWindowInsets: WindowInsets

expect val settingsContentWindowInsets: WindowInsets

expect val modifierDetailsTopAppBarWindowInsets: Modifier

expect val modifierDetailsContentWindowInsets: Modifier

expect val dialogUsePlatformDefaultWidth: Boolean

expect val bottomSheetUsePlatformDefaultWidth: Boolean

expect val movieColumnPosterModifier: Modifier

@Composable
expect fun pageLoadingGridCells(): GridCells

@Composable
expect fun pageLoadingStaggeredGridCells(): StaggeredGridCells

@Composable
expect fun PageLoadingRowItem(
    modifier: Modifier,
    cardColor: Color
)

@Composable
expect fun PageLoadingColumnItem(
    modifier: Modifier,
    cardColor: Color
)

expect val pageContentTopPadding: Dp

@Composable
expect fun pageContentGridCells(): GridCells

@Composable
expect fun pageContentStaggeredGridCells(): StaggeredGridCells

@Composable
expect fun PageContentColumnMovieItem(
    movie: MoviePojo,
    onMovieClick: (String, Int) -> Unit,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
)

@Composable
expect fun PageContentGridMovieItem(
    movie: MoviePojo,
    onMovieClick: (String, Int) -> Unit,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
)

expect val modifierDisplayCutoutWindowInsets: Modifier

expect val SettingsGenderText: String

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
