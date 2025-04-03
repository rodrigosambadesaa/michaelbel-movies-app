package org.michaelbel.movies.search.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.search.SearchViewModel2

@Composable
actual fun SearchRoute(
    onNavigateToDetails: (PagingKey, MovieId) -> Unit,
    modifier: Modifier,
    viewModel: SearchViewModel2
) {
    Text(
        text = "Feed",
        modifier = Modifier
    )
}