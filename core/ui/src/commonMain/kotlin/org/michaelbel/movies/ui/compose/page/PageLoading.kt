package org.michaelbel.movies.ui.compose.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.ui.ktx.PageLoadingColumnItem
import org.michaelbel.movies.ui.ktx.PageLoadingRowItem
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.ktx.isWideFoldableMode
import org.michaelbel.movies.ui.ktx.pageLoadingGridCells
import org.michaelbel.movies.ui.ktx.pageLoadingStaggeredGridCells

@Composable
fun PageLoading(
    feedView: FeedView,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    when (feedView) {
        is FeedView.FeedList -> {
            when {
                isPortrait && !isWideFoldableMode -> {
                    PageLoadingColumn(
                        modifier = modifier,
                        paddingValues = paddingValues,
                        cardColor = cardColor
                    )
                }
                else -> {
                    PageLoadingGrid(
                        modifier = modifier,
                        paddingValues = paddingValues,
                        cardColor = cardColor
                    )
                }
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
            PageLoadingRowItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                cardColor = cardColor
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
        columns = pageLoadingGridCells(),
        modifier = modifier.padding(horizontal = 8.dp),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(MovieResponse.DEFAULT_PAGE_SIZE.div(2)) {
            PageLoadingRowItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                cardColor = cardColor
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
        columns = pageLoadingStaggeredGridCells(),
        modifier = modifier.padding(horizontal = 8.dp),
        contentPadding = paddingValues,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(MovieResponse.DEFAULT_PAGE_SIZE.div(2)) {
            PageLoadingColumnItem(
                modifier = Modifier.fillMaxWidth(),
                cardColor = cardColor
            )
        }
    }
}
