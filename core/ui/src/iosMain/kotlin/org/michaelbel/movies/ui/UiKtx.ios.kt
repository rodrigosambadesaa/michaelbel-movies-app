@file:OptIn(ExperimentalForeignApi::class)

package org.michaelbel.movies.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.movie.MovieColumn
import org.michaelbel.movies.ui.compose.movie.MovieRow
import org.michaelbel.movies.ui.placeholder.PlaceholderHighlight
import org.michaelbel.movies.ui.placeholder.material3.fade
import org.michaelbel.movies.ui.placeholder.placeholder
import org.michaelbel.movies.ui.strings.MoviesStrings
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import kotlin.coroutines.CoroutineContext

actual val isDebug: Boolean
    get() = true

actual val isPortrait: Boolean
    get() = UIDevice.currentDevice.orientation in listOf(UIDeviceOrientation.UIDeviceOrientationPortrait, UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown)

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
    @Composable get() = iosTopAndHorizontalCutoutWindowInsets()

actual val settingsContentWindowInsets: WindowInsets
    @Composable get() = iosHorizontalCutoutWindowInsets()

actual val modifierDetailsTopAppBarWindowInsets: Modifier
    @Composable get() = Modifier.windowInsetsPadding(iosTopAndHorizontalCutoutWindowInsets())

actual val modifierDetailsContentWindowInsets: Modifier
    @Composable get() = Modifier.windowInsetsPadding(iosHorizontalCutoutWindowInsets())

actual val dialogUsePlatformDefaultWidth: Boolean
    get() = false

actual val bottomSheetUsePlatformDefaultWidth: Boolean
    get() = true

actual val movieColumnPosterModifier: Modifier = Modifier
    .fillMaxWidth()
    .height(220.dp)

@Composable
actual fun pageLoadingGridCells(): GridCells {
    return GridCells.Fixed(2)
}

@Composable
actual fun pageLoadingStaggeredGridCells(): StaggeredGridCells {
    return StaggeredGridCells.Fixed(gridColumnsCount)
}

@Composable
actual fun PageLoadingRowItem(
    modifier: Modifier,
    cardColor: Color
) {
    MovieRow(
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

actual val pageContentTopPadding: Dp = 0.dp

@Composable
actual fun pageContentGridCells(): GridCells = GridCells.Fixed(2)

@Composable
actual fun pageContentStaggeredGridCells(): StaggeredGridCells = StaggeredGridCells.Fixed(
    gridColumnsCount
)

@Composable
actual fun PageContentColumnMovieItem(
    movie: MoviePojo,
    onMovieClick: (String, Int) -> Unit,
    cardColor: Color
) {
    MovieRow(
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
    MovieRow(
        movie = movie,
        maxLines = 1,
        modifier = pageContentGridModifier(cardColor)
            .clickable { onMovieClick(movie.movieList, movie.movieId) }
    )
}

actual val modifierDisplayCutoutWindowInsets: Modifier
    get() = Modifier.windowInsetsPadding(displayCutoutWindowInsets)

actual val SettingsGenderText: String
    @Composable get() = stringResource(MoviesStrings.settings_gender)

@Composable
actual fun rememberSpeechRecognitionLauncher(onInputText: (String) -> Unit): () -> Unit = {}

@Composable
actual fun shareText(text: String, title: String): () -> Unit = {
    val currentViewController = UIApplication.sharedApplication().keyWindow?.rootViewController
    val activityViewController = UIActivityViewController(listOf(text), null)
    currentViewController?.presentViewController(
        viewControllerToPresent = activityViewController,
        animated = true,
        completion = null
    )
}

@Composable
actual fun navigateToImageUri(): (uri: String) -> Unit = { uri ->
    val nsUrl = NSURL.URLWithString(uri)
    if (nsUrl != null) {
        UIApplication.sharedApplication().openURL(
            nsUrl,
            options = emptyMap<Any?, Any?>(),
            completionHandler = null
        )
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
    LaunchedEffect(flow, key1, key2) {
        flow.collect(onEvent)
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
): Modifier = this
