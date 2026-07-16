package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.dao.ImageDao
import org.michaelbel.movies.persistence.database.entity.pojo.ImagePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId

class ImagesFlowUseCase(
    private val imageDao: ImageDao,
    dispatchers: SharedDispatchers
): FlowUseCase<MovieId, List<ImagePojo>>(dispatchers.io) {

    override fun execute(params: MovieId): Flow<List<ImagePojo>> {
        return imageDao.selectFlow(params)
    }
}
