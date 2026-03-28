@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.detailsweb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.koin.compose.koinInject
import org.michaelbel.movies.common.exceptions.ApiKeyNotNullException
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.network.config.formatBackdropImage
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsWebScreen(
    movieId: MovieId,
    pagingKey: PagingKey,
    onBackClick: () -> Unit,
    interactor: Interactor = koinInject()
) {
    val state by produceState<DetailsWebState>(
        initialValue = DetailsWebState.Loading,
        key1 = interactor,
        key2 = movieId,
        key3 = pagingKey
    ) {
        value = try {
            DetailsWebState.Ready(
                movie = interactor.movieDetails(
                    pagingKey = pagingKey,
                    movieId = movieId
                )
            )
        } catch (exception: Exception) {
            DetailsWebState.Error(exception.toUserMessage())
        }
    }

    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = state.toolbarTitle
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            shape = IconButtonDefaults.extraSmallSquareShape
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = detailsBackText,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.inversePrimary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) { innerPadding ->
            when (val currentState = state) {
                DetailsWebState.Loading -> LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
                is DetailsWebState.Ready -> DetailsContent(
                    movie = currentState.movie,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
                is DetailsWebState.Error -> ErrorContent(
                    message = currentState.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DetailsContent(
    movie: MoviePojo,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                when {
                    movie.backdropPath.isBlank() -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    else -> AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(movie.backdropPath.formatBackdropImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.6F),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetadataRow(
                            icon = Icons.Outlined.CalendarToday,
                            text = movie.releaseDate.ifBlank { detailsUnknownReleaseDateText }
                        )
                        MetadataRow(
                            icon = Icons.Outlined.StarOutline,
                            text = movie.voteAverage.toString()
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = detailsOverviewText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = movie.overview.ifBlank { detailsOverviewEmptyText },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
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
        is ApiKeyNotNullException -> detailsApiKeyErrorText
        else -> message ?: this::class.simpleName.orEmpty()
    }
}

private val DetailsWebState.toolbarTitle: String
    get() = when (this) {
        DetailsWebState.Loading -> detailsTitleText
        is DetailsWebState.Ready -> movie.title
        is DetailsWebState.Error -> detailsTitleText
    }

private sealed interface DetailsWebState {
    data object Loading: DetailsWebState
    data class Ready(
        val movie: MoviePojo
    ): DetailsWebState
    data class Error(
        val message: String
    ): DetailsWebState
}

private const val detailsTitleText = "Details"
private const val detailsBackText = "Back"
private const val detailsOverviewText = "Overview"
private const val detailsOverviewEmptyText = "Overview is not available."
private const val detailsUnknownReleaseDateText = "Release date is unknown."
private const val detailsApiKeyErrorText = "TMDB API key is missing. Set TMDB_API_KEY before starting the web app."
