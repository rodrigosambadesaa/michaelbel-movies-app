package org.michaelbel.movies.gallery.ui

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.gallery.GalleryViewModel

@Composable
expect fun GalleryScreen(
    viewModel: GalleryViewModel = koinViewModel()
)