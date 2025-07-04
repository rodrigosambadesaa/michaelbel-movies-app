package org.michaelbel.movies.gallery

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.michaelbel.movies.ui.navigation.GalleryDestination
import org.michaelbel.movies.gallery.ui.GalleryScreen

fun NavGraphBuilder.galleryGraph() {
    composable<GalleryDestination> {
        GalleryScreen()
    }
}