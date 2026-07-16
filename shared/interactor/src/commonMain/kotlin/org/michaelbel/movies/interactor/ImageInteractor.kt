package org.michaelbel.movies.interactor

import org.michaelbel.movies.persistence.database.typealiases.MovieId

interface ImageInteractor {

    suspend fun images(movieId: MovieId)
}
