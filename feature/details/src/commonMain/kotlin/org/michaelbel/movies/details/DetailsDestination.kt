package org.michaelbel.movies.details

import kotlinx.serialization.Serializable
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

@Serializable
class DetailsDestination(
    val movieList: PagingKey?,
    val movieId: MovieId
)