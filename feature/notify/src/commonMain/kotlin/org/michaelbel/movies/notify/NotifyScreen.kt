package org.michaelbel.movies.notify

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun NotifyScreen(
    viewModel: NotifyViewModel = koinViewModel()
)
