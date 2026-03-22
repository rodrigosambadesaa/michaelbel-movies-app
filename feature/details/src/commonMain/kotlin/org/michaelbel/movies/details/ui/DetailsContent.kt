@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.network.config.formatBackdropImage
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.isNotEmpty
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.placeholder.PlaceholderHighlight
import org.michaelbel.movies.ui.placeholder.material3.fade
import org.michaelbel.movies.ui.placeholder.placeholder
import org.michaelbel.movies.ui.preview.MovieDbPreviewParameterProvider
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun DetailsContent(
    movie: MoviePojo,
    onNavigateToGallery: () -> Unit,
    isDetailsGalleryFeatureEnabled: Boolean = false,
    modifier: Modifier = Modifier,
    additionalBottomContentPadding: Dp = 0.dp,
    onContainerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    placeholder: Boolean = false,
    shouldGenerateColors: Boolean = true,
    onGenerateColors: (Int, Int?, Int?) -> Unit = { _, _, _ -> },
    detailsPaletteEffect: @Composable (MoviePojo, Boolean, Boolean, (Int, Int?, Int?) -> Unit) -> Unit = { _, _, _, _ -> }
) {
    detailsPaletteEffect(movie, placeholder, shouldGenerateColors, onGenerateColors)

    val platformContext = LocalPlatformContext.current
    var isNoImageVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp + additionalBottomContentPadding
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val imageRequest: ImageRequest? = if (placeholder) {
            null
        } else {
            ImageRequest.Builder(platformContext)
                .data(movie.backdropPath.formatBackdropImage)
                .crossfade(true)
                .build()
        }

        item {
            ElevatedCard(
                shape = MaterialTheme.shapes.largeIncreased,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.largeIncreased)
                    .clickable(
                        enabled = isDetailsGalleryFeatureEnabled && !placeholder && !isNoImageVisible,
                        onClick = onNavigateToGallery
                    )
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = stringResource(MoviesContentDescription.MovieDetailsImage),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(MaterialTheme.shapes.largeIncreased)
                        .background(MaterialTheme.colorScheme.inversePrimary)
                        .placeholder(
                            visible = placeholder,
                            color = MaterialTheme.colorScheme.inversePrimary,
                            shape = MaterialTheme.shapes.largeIncreased,
                            highlight = PlaceholderHighlight.fade()
                        ),
                    onState = { state ->
                        isNoImageVisible = movie.isNotEmpty && (state is AsyncImagePainter.State.Error || state is AsyncImagePainter.State.Empty)
                    },
                    contentScale = ContentScale.Crop
                )
            }
        }
        item {
            SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = placeholder,
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = MaterialTheme.shapes.large,
                        highlight = PlaceholderHighlight.fade()
                    )
            ) {
                Text(
                    text = movie.overview,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = onContainerColor,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.15F
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun DetailsContentPreview(
    @PreviewParameter(MovieDbPreviewParameterProvider::class) movie: MoviePojo
) {
    MoviesTheme {
        DetailsContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            movie = movie,
            onNavigateToGallery = {}
        )
    }
}
