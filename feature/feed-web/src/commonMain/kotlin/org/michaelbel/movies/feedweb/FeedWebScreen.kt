package org.michaelbel.movies.feedweb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.koin.compose.koinInject
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.network.config.formatBackdropImage
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

@Composable
fun FeedWebScreen(
    onFeedClick: () -> Unit = {},
    onFaveClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onMovieClick: (PagingKey, MovieId) -> Unit = { _, _ -> },
    interactor: Interactor = koinInject()
) {
    var reloadToken by remember { mutableIntStateOf(0) }
    val state by produceState<FeedWebState>(
        initialValue = FeedWebState.Loading,
        key1 = interactor,
        key2 = reloadToken
    ) {
        value = try {
            FeedWebState.Ready(
                movies = interactor.moviesResult(MovieList.name(MovieList.NowPlaying()))
            )
        } catch (exception: Exception) {
            FeedWebState.Error(exception.toUserMessage())
        }
    }

    BoxWithConstraints {
        val useRailNavigation = maxWidth >= 1100.dp
        val useAdaptiveGrid = maxWidth >= 700.dp

        MaterialTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (!useRailNavigation) {
                        FeedBottomBar(
                            onFeedClick = onFeedClick,
                            onFaveClick = onFaveClick,
                            onSettingsClick = onSettingsClick
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) { innerPadding ->
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (useRailNavigation) {
                        FeedNavigationRail(
                            onFeedClick = onFeedClick,
                            onFaveClick = onFaveClick,
                            onSettingsClick = onSettingsClick
                        )
                    }

                    when (val currentState = state) {
                        FeedWebState.Loading -> LoadingContent(
                            modifier = Modifier
                                .weight(1F)
                                .padding(
                                    start = if (useRailNavigation) 0.dp else 8.dp,
                                    top = 8.dp,
                                    end = 8.dp,
                                    bottom = innerPadding.calculateBottomPadding().plus(8.dp)
                                )
                        )
                        is FeedWebState.Ready -> MoviesContent(
                            movies = currentState.movies,
                            useAdaptiveGrid = useAdaptiveGrid,
                            onMovieClick = onMovieClick,
                            contentPadding = PaddingValues(
                                start = 8.dp,
                                top = 8.dp,
                                end = 8.dp,
                                bottom = innerPadding.calculateBottomPadding() + if (useRailNavigation) 8.dp else 88.dp
                            ),
                            modifier = Modifier.weight(1F)
                        )
                        is FeedWebState.Error -> ErrorContent(
                            message = currentState.message,
                            modifier = Modifier
                                .weight(1F)
                                .padding(
                                    start = if (useRailNavigation) 0.dp else 16.dp,
                                    top = 16.dp,
                                    end = 16.dp,
                                    bottom = innerPadding.calculateBottomPadding().plus(16.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedNavigationRail(
    onFeedClick: () -> Unit,
    onFaveClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val railItems = feedRailItems(
        onFeedClick = onFeedClick,
        onFaveClick = onFaveClick,
        onSettingsClick = onSettingsClick
    )

    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Spacer(
            modifier = Modifier.weight(1F)
        )

        railItems.forEach { item ->
            NavigationRailItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.inversePrimary)
            )
        }

        Spacer(
            modifier = Modifier.weight(1F)
        )
    }
}

@Composable
private fun FeedBottomBar(
    onFeedClick: () -> Unit,
    onFaveClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val railItems = feedRailItems(
        onFeedClick = onFeedClick,
        onFaveClick = onFaveClick,
        onSettingsClick = onSettingsClick
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                railItems.forEach { item ->
                    BottomBarPill(item = item)
                }
            }
        }
    }
}

@Composable
private fun BottomBarPill(
    item: RailItem
) {
    Surface(
        color = when {
            item.selected -> MaterialTheme.colorScheme.inversePrimary
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(22.dp),
        onClick = item.onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MoviesContent(
    movies: List<MoviePojo>,
    useAdaptiveGrid: Boolean,
    onMovieClick: (PagingKey, MovieId) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    when {
        useAdaptiveGrid -> MoviesGrid(
            movies = movies,
            onMovieClick = onMovieClick,
            contentPadding = contentPadding,
            modifier = modifier
        )
        else -> MoviesColumn(
            movies = movies,
            onMovieClick = onMovieClick,
            contentPadding = contentPadding,
            modifier = modifier
        )
    }
}

@Composable
private fun MoviesColumn(
    movies: List<MoviePojo>,
    onMovieClick: (PagingKey, MovieId) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieDesktopCard(
                movie = movie,
                maxLines = 10,
                onClick = {
                    onMovieClick(movie.movieList, movie.movieId)
                }
            )
        }
    }
}

@Composable
private fun MoviesGrid(
    movies: List<MoviePojo>,
    onMovieClick: (PagingKey, MovieId) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier.fillMaxSize(),
        state = gridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieDesktopCard(
                movie = movie,
                maxLines = 1,
                onClick = {
                    onMovieClick(movie.movieList, movie.movieId)
                }
            )
        }
    }
}

@Composable
private fun MovieDesktopCard(
    movie: MoviePojo,
    maxLines: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.inversePrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minWidth = 280.dp)
                .widthIn(max = 586.dp)
                .aspectRatio(1.5F)
        ) {
            when {
                movie.backdropPath.isBlank() -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                else -> {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(movie.backdropPath.formatBackdropImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Text(
            text = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.inversePrimary
            )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(24.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

private fun Exception.toUserMessage(): String {
    return when (this) {
        is ApiKeyNotNullException -> "TMDB API key is missing. Set TMDB_API_KEY before starting the web app."
        else -> message ?: this::class.simpleName.orEmpty()
    }
}

private data class RailItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean = false,
    val onClick: () -> Unit = {}
)

private fun feedRailItems(
    onFeedClick: () -> Unit,
    onFaveClick: () -> Unit,
    onSettingsClick: () -> Unit
): List<RailItem> {
    return listOf(
        RailItem(
            label = "Feed",
            icon = Icons.Outlined.GridView,
            selected = true,
            onClick = onFeedClick
        ),
        RailItem(
            label = "Fave",
            icon = Icons.Outlined.Favorite,
            onClick = onFaveClick
        ),
        RailItem(
            label = "Settings",
            icon = Icons.Outlined.Settings,
            onClick = onSettingsClick
        )
    )
}

private sealed interface FeedWebState {
    data object Loading: FeedWebState
    data class Ready(
        val movies: List<MoviePojo>
    ): FeedWebState
    data class Error(val message: String): FeedWebState
}
