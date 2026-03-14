package org.michaelbel.movies.ui.ktx

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo

val gridColumnsCount: Int
    @Composable get() = if (isPortrait) 2 else 4

expect val isDebug: Boolean

expect val isPortrait: Boolean

expect fun statusBarStyle(detectDarkMode: Boolean): Any

@Composable
expect fun navigationBarStyle(detectDarkMode: Boolean): Any

expect val displayCutoutWindowInsets: WindowInsets

expect val USE_PLATFORM_DEFAULT_WIDTH: Boolean

expect val AUTH_DIALOG_USE_PLATFORM_DEFAULT_WIDTH: Boolean

expect val ACCOUNT_DIALOG_USE_PLATFORM_DEFAULT_WIDTH: Boolean

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
