package org.michaelbel.movies.ui.compose.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.PageContentColumnMovieItem
import org.michaelbel.movies.ui.PageContentGridMovieItem
import org.michaelbel.movies.ui.compose.movie.MovieColumn
import org.michaelbel.movies.ui.isPagingFailure
import org.michaelbel.movies.ui.isPagingLoading
import org.michaelbel.movies.ui.isPortrait
import org.michaelbel.movies.ui.isWideFoldableMode
import org.michaelbel.movies.ui.pageContentGridCells
import org.michaelbel.movies.ui.pageContentStaggeredGridCells
import org.michaelbel.movies.ui.pageContentStaggeredGridModifier

@Composable
fun PageContent(
    feedView: FeedView,
    lazyListState: LazyListState,
    lazyGridState: LazyGridState,
    lazyStaggeredGridState: LazyStaggeredGridState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    when (feedView) {
        is FeedView.FeedList -> when (isPortrait && !isWideFoldableMode) {
            true -> {
                PagingContentColumn(
                    lazyListState = lazyListState,
                    pagingItems = pagingItems,
                    onMovieClick = onMovieClick,
                    contentPadding = contentPadding,
                    modifier = modifier,
                    cardColor = cardColor
                )
            }
            false -> {
                PagingContentGrid(
                    lazyGridState = lazyGridState,
                    pagingItems = pagingItems,
                    onMovieClick = onMovieClick,
                    contentPadding = contentPadding,
                    modifier = modifier,
                    cardColor = cardColor
                )
            }
        }
        is FeedView.FeedGrid -> {
            PagingContentStaggeredGrid(
                lazyStaggeredGridState = lazyStaggeredGridState,
                pagingItems = pagingItems,
                onMovieClick = onMovieClick,
                contentPadding = contentPadding,
                modifier = modifier,
                cardColor = cardColor
            )
        }
    }
}

@Composable
private fun PagingContentColumn(
    lazyListState: LazyListState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey(),
            contentType = pagingItems.itemContentType()
        ) { index ->
            val movieDb = pagingItems[index]
            if (movieDb != null) {
                PageContentColumnMovieItem(
                    movie = movieDb,
                    onMovieClick = onMovieClick,
                    cardColor = cardColor
                )
            }
        }
        when {
            pagingItems.isPagingLoading -> {
                item {
                    PagingLoadingBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }
            pagingItems.isPagingFailure -> {
                item {
                    PagingFailureBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(start = 8.dp, top = 4.dp, end = 8.dp)
                            .clip(MaterialTheme.shapes.large)
                            .clickable { pagingItems.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListContentColumn(
    lazyListState: LazyListState,
    pagingItems: List<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    listTopPadding: Dp,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyColumn(
        modifier = modifier.padding(top = listTopPadding),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        items(pagingItems) { movieDb ->
            PageContentColumnMovieItem(
                movie = movieDb,
                onMovieClick = onMovieClick,
                cardColor = cardColor
            )
        }
    }
}

@Composable
private fun PagingContentGrid(
    lazyGridState: LazyGridState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalGrid(
        columns = pageContentGridCells(),
        modifier = modifier.padding(horizontal = 8.dp),
        state = lazyGridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey(),
            contentType = pagingItems.itemContentType()
        ) { index ->
            val movieDb = pagingItems[index]
            if (movieDb != null) {
                PageContentGridMovieItem(
                    movie = movieDb,
                    onMovieClick = onMovieClick,
                    cardColor = cardColor
                )
            }
        }
        when {
            pagingItems.isPagingLoading -> {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    PagingLoadingBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }
            pagingItems.isPagingFailure -> {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    PagingFailureBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(MaterialTheme.shapes.large)
                            .clickable { pagingItems.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListContentGrid(
    lazyGridState: LazyGridState,
    pagingItems: List<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalGrid(
        columns = pageContentGridCells(),
        modifier = modifier.padding(horizontal = 8.dp),
        state = lazyGridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pagingItems) { movieDb ->
            PageContentGridMovieItem(
                movie = movieDb,
                onMovieClick = onMovieClick,
                cardColor = cardColor
            )
        }
    }
}

@Composable
private fun PagingContentStaggeredGrid(
    lazyStaggeredGridState: LazyStaggeredGridState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalStaggeredGrid(
        columns = pageContentStaggeredGridCells(),
        modifier = modifier.padding(horizontal = 8.dp),
        state = lazyStaggeredGridState,
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey(),
            contentType = pagingItems.itemContentType()
        ) { index ->
            val movieDb = pagingItems[index]
            if (movieDb != null) {
                MovieColumn(
                    movie = movieDb,
                    modifier = pageContentStaggeredGridModifier(cardColor)
                        .clickable { onMovieClick(movieDb.movieList, movieDb.movieId) }
                )
            }
        }
        when {
            pagingItems.isPagingLoading -> {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    PagingLoadingBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }
            pagingItems.isPagingFailure -> {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    PagingFailureBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(MaterialTheme.shapes.large)
                            .clickable { pagingItems.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListContentStaggeredGrid(
    lazyStaggeredGridState: LazyStaggeredGridState,
    pagingItems: List<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalStaggeredGrid(
        columns = pageContentStaggeredGridCells(),
        modifier = modifier.padding(horizontal = 8.dp),
        state = lazyStaggeredGridState,
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pagingItems) { movieDb ->
            MovieColumn(
                movie = movieDb,
                modifier = pageContentStaggeredGridModifier(cardColor)
                    .clickable { onMovieClick(movieDb.movieList, movieDb.movieId) }
            )
        }
    }
}
