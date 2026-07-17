package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.network.MovieNetworkService
import org.michaelbel.movies.persistence.database.MoviePersistence
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.moviePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.domain.usecase.MovieDetailsUseCase.Params

class MovieDetailsUseCase(
    private val movieNetworkService: MovieNetworkService,
    private val moviePersistence: MoviePersistence,
    dispatchers: SharedDispatchers
): UseCase<Params, MoviePojo>(dispatchers.io) {

    override suspend fun execute(params: Params): MoviePojo {
        return try {
            moviePersistence.movieById(params.pagingKey, params.movieId)
                ?: movieNetworkService.movie(params.movieId, params.language).moviePojo
        } catch (exception: Exception) {
            throw MovieDetailsException(exception.message.orEmpty())
        }
    }

    data class MovieDetailsException(
        override val message: String
    ): Exception(message)

    data class Params(
        val pagingKey: PagingKey,
        val language: String,
        val movieId: MovieId
    )
}
