package org.michaelbel.movies.ui.compose.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.compose.movie.MovieColumnDesktop
import org.michaelbel.movies.ui.compose.movie.MovieRowDesktop
import org.michaelbel.movies.ui.ktx.isPagingFailure
import org.michaelbel.movies.ui.ktx.isPagingLoading
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.ktx.pageContentColumnModifier
import org.michaelbel.movies.ui.ktx.pageContentGridModifier
import org.michaelbel.movies.ui.ktx.pageContentStaggeredGridModifier

@Composable
fun PageContent(
    feedView: FeedView,
    lazyListState: LazyListState,
    lazyGridState: LazyGridState,
    lazyStaggeredGridState: LazyStaggeredGridState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    modifier: Modifier,
    contentPadding: PaddingValues,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    when (feedView) {
        is FeedView.FeedList -> {
            if (isPortrait) {
                PageContentColumn(
                    lazyListState = lazyListState,
                    pagingItems = pagingItems,
                    onMovieClick = onMovieClick,
                    contentPadding = contentPadding,
                    modifier = modifier,
                    cardColor = cardColor
                )
            } else {
                PageContentGrid(
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
            PageContentStaggeredGrid(
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
private fun PageContentColumn(
    lazyListState: LazyListState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyColumn(
        modifier = modifier.padding(top = 8.dp),
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
                MovieRowDesktop(
                    movie = movieDb,
                    modifier = pageContentColumnModifier(cardColor)
                        .then(Modifier.clickable { onMovieClick(movieDb.movieList, movieDb.movieId) })
                )
            }
        }
        pagingItems.apply {
            when {
                isPagingLoading -> {
                    item {
                        PagingLoadingBox(
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        )
                    }
                }
                isPagingFailure -> {
                    item {
                        PagingFailureBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(start = 8.dp, top = 4.dp, end = 8.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable { retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageContentGrid(
    lazyGridState: LazyGridState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
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
                MovieRowDesktop(
                    movie = movieDb,
                    maxLines = 1,
                    modifier = pageContentGridModifier(cardColor)
                        .then(Modifier.clickable { onMovieClick(movieDb.movieList, movieDb.movieId) })
                )
            }
        }
        pagingItems.apply {
            when {
                isPagingLoading -> {
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
                isPagingFailure -> {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        PagingFailureBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable { retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageContentStaggeredGrid(
    lazyStaggeredGridState: LazyStaggeredGridState,
    pagingItems: LazyPagingItems<MoviePojo>,
    onMovieClick: (String, Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 220.dp),
        modifier = modifier.padding(start = 8.dp, end = 8.dp),
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
                MovieColumnDesktop(
                    movie = movieDb,
                    modifier = pageContentStaggeredGridModifier(cardColor)
                        .then(Modifier.clickable { onMovieClick(movieDb.movieList, movieDb.movieId) })
                )
            }
        }
        pagingItems.apply {
            when {
                isPagingLoading -> {
                    item(
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        PagingLoadingBox(
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        )
                    }
                }
                isPagingFailure -> {
                    item(
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        PagingFailureBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable { retry() }
                        )
                    }
                }
            }
        }
    }
}
