package org.michaelbel.movies.ui.compose.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.movie.MovieColumn
import org.michaelbel.movies.ui.compose.movie.MovieRow
import org.michaelbel.movies.ui.ktx.gridColumnsCount
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.placeholder.PlaceholderHighlight
import org.michaelbel.movies.ui.placeholder.material3.fade
import org.michaelbel.movies.ui.placeholder.placeholder
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun PageLoading(
    feedView: FeedView,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    when (feedView) {
        is FeedView.FeedList -> {
            if (isPortrait) {
                PageLoadingColumn(
                    modifier = modifier,
                    paddingValues = paddingValues,
                    cardColor = cardColor
                )
            } else {
                PageLoadingGrid(
                    modifier = modifier,
                    paddingValues = paddingValues,
                    cardColor = cardColor
                )
            }
        }
        is FeedView.FeedGrid -> {
            PageLoadingStaggeredGrid(
                modifier = modifier,
                paddingValues = paddingValues,
                cardColor = cardColor
            )
        }
    }
}

@Composable
private fun PageLoadingColumn(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues,
        userScrollEnabled = false
    ) {
        items(MovieResponse.DEFAULT_PAGE_SIZE.div(2)) {
            MovieRow(
                movie = MoviePojo.Empty,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .placeholder(
                        visible = true,
                        color = cardColor,
                        shape = MaterialTheme.shapes.large,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }
    }
}

@Composable
private fun PageLoadingGrid(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(MovieResponse.DEFAULT_PAGE_SIZE.div(2)) {
            MovieRow(
                movie = MoviePojo.Empty,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(bottom = 8.dp)
                    .placeholder(
                        visible = true,
                        color = cardColor,
                        shape = MaterialTheme.shapes.large,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }
    }
}

@Composable
private fun PageLoadingStaggeredGrid(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(gridColumnsCount),
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
        contentPadding = paddingValues,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(MovieResponse.DEFAULT_PAGE_SIZE.div(2)) {
            MovieColumn(
                movie = MoviePojo.Empty,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = cardColor,
                        shape = MaterialTheme.shapes.large,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }
    }
}

@Preview
@Composable
private fun PageLoadingColumnPreview() {
    MoviesTheme {
        PageLoadingColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}

@Preview
@Composable
private fun PageLoadingGridPreview() {
    MoviesTheme {
        PageLoadingGrid(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}

@Preview
@Composable
private fun PageLoadingStaggeredGridPreview() {
    MoviesTheme {
        PageLoadingStaggeredGrid(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}
