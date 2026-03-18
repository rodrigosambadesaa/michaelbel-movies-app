@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.ui.compose.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun PageFailure(
    modifier: Modifier,
    isButtonVisible: Boolean = false,
    onButtonClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RotatingCookie12SidedBox(
            modifier = Modifier.size(164.dp),
            color = MaterialTheme.colorScheme.error
        ) {
            Icon(
                imageVector = MoviesIcons.Info,
                contentDescription = MoviesContentDescription.None,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(MoviesStrings.error_loading),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
        )

        if (isButtonVisible) {
            Button(
                onClick = onButtonClick,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceTint
                ),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text(
                    text = stringResource(MoviesStrings.error_check_internet_connectivity),
                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PageFailurePreview() {
    MoviesTheme {
        PageFailure(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            isButtonVisible = true,
            onButtonClick = {}
        )
    }
}
