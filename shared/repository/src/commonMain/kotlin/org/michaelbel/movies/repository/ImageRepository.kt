package org.michaelbel.movies.repository

import org.michaelbel.movies.persistence.database.typealiases.MovieId

interface ImageRepository {

    suspend fun images(movieId: MovieId)
}
