package org.michaelbel.movies.feed.ui

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.feed.FeedViewModel

@Composable
expect fun FeedScreen(
    viewModel: FeedViewModel = koinViewModel()
)
