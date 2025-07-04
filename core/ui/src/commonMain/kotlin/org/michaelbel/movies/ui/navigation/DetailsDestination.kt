package org.michaelbel.movies.ui.navigation

import kotlinx.serialization.Serializable
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

@Serializable
data class DetailsDestination(
    val movieList: PagingKey?,
    val movieId: MovieId
)