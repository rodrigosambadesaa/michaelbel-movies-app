package org.michaelbel.movies.fave

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun FaveScreen(
    viewModel: FaveViewModel = koinViewModel()
)
