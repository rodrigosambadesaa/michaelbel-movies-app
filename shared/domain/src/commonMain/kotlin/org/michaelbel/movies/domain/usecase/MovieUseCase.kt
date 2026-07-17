package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.domain.usecase.MovieUseCase.Params

class MovieUseCase(
    private val moviePersistence: MoviePersistence,
    dispatchers: SharedDispatchers
): UseCase<Params, MoviePojo>(dispatchers.io) {

    override suspend fun execute(params: Params): MoviePojo {
        return moviePersistence.movieById(params.pagingKey, params.movieId).orEmpty
    }

    data class Params(
        val pagingKey: PagingKey,
        val movieId: MovieId
    )
}
