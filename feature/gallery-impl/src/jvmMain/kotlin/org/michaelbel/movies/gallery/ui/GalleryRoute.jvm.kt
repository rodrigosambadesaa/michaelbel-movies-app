package org.michaelbel.movies.gallery.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.michaelbel.movies.gallery.GalleryViewModel

@Composable
actual fun GalleryRoute(
    modifier: Modifier,
    viewModel: GalleryViewModel
) {
    Text(
        text = "Gallery",
        modifier = Modifier
    )
}