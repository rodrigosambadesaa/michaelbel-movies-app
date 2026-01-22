package org.michaelbel.movies.gallery.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.michaelbel.movies.gallery.GalleryViewModel
import org.michaelbel.movies.ui.navigation.GalleryDestination

@Composable
actual fun GalleryScreen(
    destination: GalleryDestination,
    viewModel: GalleryViewModel
) {
    Text(
        text = "Gallery",
        modifier = Modifier
    )
}
