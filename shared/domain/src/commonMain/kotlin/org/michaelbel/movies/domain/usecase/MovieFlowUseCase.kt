package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.dao.MovieDao
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

class MovieFlowUseCase(
    private val movieDao: MovieDao,
    dispatchers: SharedDispatchers
): FlowUseCase<MovieFlowUseCase.Params, MoviePojo?>(dispatchers.io) {

    override fun execute(params: Params): Flow<MoviePojo?> {
        return movieDao.movieFlow(params.pagingKey, params.movieId)
    }

    data class Params(
        val pagingKey: PagingKey,
        val movieId: MovieId
    )
}
