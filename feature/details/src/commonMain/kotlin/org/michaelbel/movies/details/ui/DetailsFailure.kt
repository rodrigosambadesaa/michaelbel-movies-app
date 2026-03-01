@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun DetailsFailure(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialShapes.Pill.toShape()
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = MoviesIcons.Info,
                contentDescription = MoviesContentDescriptionCommon.None,
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(MoviesStrings.details_error_loading),
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
private fun DetailsFailurePreview() {
    MoviesTheme {
        DetailsFailure(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}
