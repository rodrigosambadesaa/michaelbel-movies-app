package org.michaelbel.movies.ui.navigation

import kotlinx.serialization.Serializable
import org.michaelbel.movies.persistence.database.typealiases.MovieId

@Serializable
data class GalleryDestination(
    val movieId: MovieId
)