package org.michaelbel.movies.ui.ktx

import android.content.res.Configuration
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.movie.MovieColumn
import org.michaelbel.movies.ui.compose.movie.MovieRow
import org.michaelbel.movies.ui.placeholder.PlaceholderHighlight
import org.michaelbel.movies.ui.placeholder.material3.fade
import org.michaelbel.movies.ui.placeholder.placeholder

actual val isDebug: Boolean
    get() = runCatching {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val application = activityThreadClass
            .getDeclaredMethod("currentApplication")
            .invoke(null) as? android.app.Application
        application?.applicationInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }.getOrDefault(false)

actual val isPortrait: Boolean
    @Composable get() {
        val configuration = LocalConfiguration.current
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

actual val isWideFoldableMode: Boolean
    @Composable get() {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp >= 600
    }

actual fun statusBarStyle(detectDarkMode: Boolean): Any {
    return SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()) { detectDarkMode }
}

@Composable
actual fun navigationBarStyle(detectDarkMode: Boolean): Any {
    val configuration = LocalConfiguration.current
    val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return when (currentNightMode) {
        Configuration.UI_MODE_NIGHT_NO -> SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
        Configuration.UI_MODE_NIGHT_YES -> SystemBarStyle.dark(Color.Transparent.toArgb())
        else -> SystemBarStyle.auto(Color.Transparent.toArgb(), Color.Transparent.toArgb()) { detectDarkMode }
    }
}

actual val displayCutoutWindowInsets: WindowInsets
    @Composable get() = if (isPortrait) WindowInsets(0, 0, 0, 0) else WindowInsets.displayCutout

actual val dialogUsePlatformDefaultWidth: Boolean
    @Composable get() = isWideFoldableMode

actual val bottomSheetUsePlatformDefaultWidth: Boolean
    get() = false

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
        modifier = modifier
            .height(280.dp)
            .placeholder(
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
