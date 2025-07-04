package org.michaelbel.movies.details

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun DetailsScreen(
    viewModel: DetailsViewModel = koinViewModel()
)