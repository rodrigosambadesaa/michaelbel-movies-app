@file:OptIn(ExperimentalComposeUiApi::class)

package org.michaelbel.movies.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.movie.MovieColumn
import org.michaelbel.movies.ui.compose.movie.MovieRowDesktop
import org.michaelbel.movies.ui.placeholder.PlaceholderHighlight
import org.michaelbel.movies.ui.placeholder.material3.fade
import org.michaelbel.movies.ui.placeholder.placeholder
import org.michaelbel.movies.ui.strings.MoviesStrings
import java.awt.Desktop
import java.io.File
import java.net.URI
import kotlin.coroutines.CoroutineContext

actual val movieColumnPosterModifier: Modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(1.5F)
    .defaultMinSize(minWidth = 400.dp, minHeight = 400.dp)

actual val isDebug: Boolean
    get() = true

actual val isPortrait: Boolean
    get() = false

actual val isWideFoldableMode: Boolean
    get() = false

actual fun statusBarStyle(detectDarkMode: Boolean): Any {
    return Any()
}

@Composable
actual fun navigationBarStyle(detectDarkMode: Boolean): Any {
    return Any()
}

actual val displayCutoutWindowInsets: WindowInsets
    get() = WindowInsets(0, 0, 0, 0)

actual val settingsTopAppBarWindowInsets: WindowInsets
    get() = WindowInsets(0, 0, 0, 0)

actual val settingsContentWindowInsets: WindowInsets
    get() = WindowInsets(0, 0, 0, 0)

actual val modifierDetailsTopAppBarWindowInsets: Modifier
    get() = Modifier

actual val modifierDetailsContentWindowInsets: Modifier
    get() = Modifier

actual val dialogUsePlatformDefaultWidth: Boolean
    get() = true

actual val bottomSheetUsePlatformDefaultWidth: Boolean
    get() = true

@Composable
actual fun pageLoadingGridCells(): GridCells {
    return GridCells.Adaptive(minSize = 280.dp)
}

@Composable
actual fun pageLoadingStaggeredGridCells(): StaggeredGridCells {
    return StaggeredGridCells.Adaptive(minSize = 220.dp)
}

@Composable
actual fun PageLoadingRowItem(
    modifier: Modifier,
    cardColor: Color
) {
    MovieRowDesktop(
        movie = MoviePojo.Empty,
        modifier = modifier.placeholder(
            visible = true,
            color = cardColor,
            shape = MaterialTheme.shapes.large,
            highlight = PlaceholderHighlight.fade()
        )
    )
}

@Composable
actual fun PageLoadingColumnItem(
    modifier: Modifier,
    cardColor: Color
) {
    MovieColumn(
        movie = MoviePojo.Empty,
        modifier = modifier.placeholder(
            visible = true,
            color = cardColor,
            shape = MaterialTheme.shapes.large,
            highlight = PlaceholderHighlight.fade()
        )
    )
}

actual val pageContentTopPadding: Dp = 8.dp

@Composable
actual fun pageContentGridCells(): GridCells = GridCells.Adaptive(minSize = 280.dp)

@Composable
actual fun pageContentStaggeredGridCells(): StaggeredGridCells = StaggeredGridCells.Adaptive(minSize = 220.dp)

@Composable
actual fun PageContentColumnMovieItem(
    movie: MoviePojo,
    onMovieClick: (String, Int) -> Unit,
    cardColor: Color
) {
    MovieRowDesktop(
        movie = movie,
        modifier = pageContentColumnModifier(cardColor)
            .clickable { onMovieClick(movie.movieList, movie.movieId) }
    )
}

@Composable
actual fun PageContentGridMovieItem(
    movie: MoviePojo,
    onMovieClick: (String, Int) -> Unit,
    cardColor: Color
) {
    MovieRowDesktop(
        movie = movie,
        maxLines = 1,
        modifier = pageContentGridModifier(cardColor)
            .clickable { onMovieClick(movie.movieList, movie.movieId) }
    )
}

actual val modifierDisplayCutoutWindowInsets: Modifier
    get() = Modifier

actual val SettingsGenderText: String
    @Composable get() = stringResource(MoviesStrings.settings_gender)

@Composable
actual fun rememberSpeechRecognitionLauncher(onInputText: (String) -> Unit): () -> Unit = {}

@Composable
actual fun shareText(text: String, title: String): () -> Unit = {}

@Composable
actual fun navigateToImageUri(): (uri: String) -> Unit {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    return { uri ->
        val imageUri = runCatching { URI.create(uri) }.getOrNull()
        if (desktop != null && imageUri != null) {
            if (imageUri.scheme == "file" && desktop.isSupported(Desktop.Action.OPEN)) {
                desktop.open(File(imageUri))
            } else if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(imageUri)
            }
        }
    }
}

@Composable
actual fun requestTileService(onSuccess: (String) -> Unit): () -> Unit {
    return {}
}

@Composable
actual fun rememberConnectivityClickHandler(): () -> Unit = {}

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
): State<T> = collectAsState()

actual fun Modifier.onSecondaryClick(
    onClick: () -> Unit
): Modifier = onPointerEvent(PointerEventType.Press) { event ->
    if (event.buttons.isSecondaryPressed) {
        onClick()
    }
}
