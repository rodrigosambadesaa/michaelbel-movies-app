package org.michaelbel.movies.feed

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun FeedScreen(
    viewModel: FeedViewModel = koinViewModel(),
    initialSearchActive: Boolean = false,
    onSearchActiveChange: (Boolean) -> Unit = {}
)
