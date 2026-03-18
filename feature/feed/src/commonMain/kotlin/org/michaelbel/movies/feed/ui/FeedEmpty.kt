@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.compose.RotatingCookie12SidedBox
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun FeedEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RotatingCookie12SidedBox(
            modifier = Modifier.size(164.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = MoviesIcons.LocalMovies,
                contentDescription = MoviesContentDescription.None,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(MoviesStrings.feed_error_empty),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        )
    }
}

@Preview
@Composable
private fun FeedEmptyPreview() {
    MoviesTheme {
        FeedEmpty(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}
