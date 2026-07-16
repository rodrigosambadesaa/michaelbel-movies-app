package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.dao.MovieDao
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.typealiases.Limit
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

class MoviesFlowUseCase(
    private val movieDao: MovieDao,
    dispatchers: SharedDispatchers
): FlowUseCase<MoviesFlowUseCase.Params, List<MoviePojo>>(dispatchers.io) {

    override fun execute(params: Params): Flow<List<MoviePojo>> {
        return movieDao.moviesFlow(params.pagingKey, params.limit)
    }

    data class Params(
        val pagingKey: PagingKey,
        val limit: Limit
    )
}
