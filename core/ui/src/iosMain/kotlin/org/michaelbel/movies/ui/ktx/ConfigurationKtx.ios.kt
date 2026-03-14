package org.michaelbel.movies.ui.ktx

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.movie.MovieColumn
import org.michaelbel.movies.ui.compose.movie.MovieRow
import org.michaelbel.movies.ui.placeholder.PlaceholderHighlight
import org.michaelbel.movies.ui.placeholder.material3.fade
import org.michaelbel.movies.ui.placeholder.placeholder
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation

actual val isDebug: Boolean
    get() = true

actual val isPortrait: Boolean
    get() = UIDevice.currentDevice.orientation in listOf(UIDeviceOrientation.UIDeviceOrientationPortrait, UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown)

actual fun statusBarStyle(detectDarkMode: Boolean): Any {
    return Any()
}

@Composable
actual fun navigationBarStyle(detectDarkMode: Boolean): Any {
    return Any()
}

actual val displayCutoutWindowInsets: WindowInsets
    get() = WindowInsets(0, 0, 0, 0)

actual const val USE_PLATFORM_DEFAULT_WIDTH: Boolean = true

actual const val AUTH_DIALOG_USE_PLATFORM_DEFAULT_WIDTH: Boolean = false

actual const val ACCOUNT_DIALOG_USE_PLATFORM_DEFAULT_WIDTH: Boolean = false

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
actual fun pageContentStaggeredGridCells(): StaggeredGridCells = StaggeredGridCells.Fixed(gridColumnsCount)

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
