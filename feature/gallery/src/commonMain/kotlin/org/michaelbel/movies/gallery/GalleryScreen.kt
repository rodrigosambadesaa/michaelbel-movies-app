package org.michaelbel.movies.gallery

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.michaelbel.movies.ui.navigation.GalleryDestination

@Composable
expect fun GalleryScreen(
    destination: GalleryDestination,
    viewModel: GalleryViewModel = koinViewModel { parametersOf(destination) }
)
