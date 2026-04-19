@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package org.michaelbel.movies.detailsweb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import org.michaelbel.movies.ui.placeholder.PlaceholderHighlight
import org.michaelbel.movies.ui.placeholder.material3.fade
import org.michaelbel.movies.ui.placeholder.placeholder

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
                        onClick = onBackClick
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
            DetailsWebState.Loading -> {
                DetailsContent(
                    movie = MoviePojo.Empty,
                    placeholder = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is DetailsWebState.Ready -> {
                DetailsContent(
                    movie = currentState.movie,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            is DetailsWebState.Error -> {
                ErrorContent(
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
private fun DetailsContent(
    movie: MoviePojo,
    modifier: Modifier = Modifier,
    placeholder: Boolean = false
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                val imageRequest: ImageRequest? = if (placeholder) {
                    null
                } else {
                    ImageRequest.Builder(LocalPlatformContext.current)
                        .data(movie.backdropPath.formatBackdropImage)
                        .crossfade(true)
                        .build()
                }
                AsyncImage(
                    model = imageRequest,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.inversePrimary)
                        .placeholder(
                            visible = placeholder,
                            color = MaterialTheme.colorScheme.inversePrimary,
                            shape = MaterialTheme.shapes.medium,
                            highlight = PlaceholderHighlight.fade()
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .placeholder(
                        visible = placeholder,
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = MaterialTheme.shapes.large,
                        highlight = PlaceholderHighlight.fade()
                    )
            ) {
                Text(
                    text = movie.overview.ifBlank { detailsOverviewEmptyText },
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.15F
                    )
                )
            }
        }
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
private const val detailsOverviewEmptyText = "Overview is not available."
private const val detailsApiKeyErrorText = "TMDB API key is missing. Set TMDB_API_KEY before starting the web app."
