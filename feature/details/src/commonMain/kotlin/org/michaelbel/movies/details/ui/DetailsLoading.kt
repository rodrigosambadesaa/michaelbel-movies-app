package org.michaelbel.movies.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun DetailsLoading(
    modifier: Modifier = Modifier,
    additionalBottomContentPadding: Dp = 0.dp
) {
    DetailsContent(
        modifier = modifier,
        movie = MoviePojo.Empty,
        onNavigateToGallery = {},
        placeholder = true,
        additionalBottomContentPadding = additionalBottomContentPadding
    )
}

@Preview
@Composable
private fun DetailsLoadingPreview() {
    MoviesTheme {
        DetailsLoading(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}
