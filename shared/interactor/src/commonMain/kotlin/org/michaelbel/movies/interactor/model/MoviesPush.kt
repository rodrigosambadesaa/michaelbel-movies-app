package org.michaelbel.movies.interactor.model

import org.michaelbel.movies.persistence.database.typealiases.MovieId

data class MoviesPush(
    val notificationId: Int,
    val notificationTitle: String,
    val notificationDescription: String,
    val movieId: MovieId
)
