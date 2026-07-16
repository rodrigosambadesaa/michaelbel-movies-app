@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.network.MovieNetworkService
import org.michaelbel.movies.persistence.database.ImagePersistence
import org.michaelbel.movies.persistence.database.entity.pojo.ImageType
import org.michaelbel.movies.persistence.database.ktx.imagePojo
import org.michaelbel.movies.persistence.database.typealiases.MovieId

class ImagesUseCase(
    private val movieNetworkService: MovieNetworkService,
    private val imagePersistence: ImagePersistence,
    dispatchers: SharedDispatchers
): UseCase<MovieId, Unit>(dispatchers.io) {

    override suspend fun execute(movieId: MovieId) {
        val imageResponse = movieNetworkService.images(movieId)
        val posters = imageResponse.posters.mapIndexed { index, image ->
            image.imagePojo(
                movieId = movieId,
                type = ImageType.POSTER,
                position = index
            )
        }
        val backdrops = imageResponse.backdrops.mapIndexed { index, image ->
            image.imagePojo(
                movieId = movieId,
                type = ImageType.BACKDROP,
                position = posters.count().plus(index)
            )
        }
        val logos = imageResponse.logos.mapIndexed { index, image ->
            image.imagePojo(
                movieId = movieId,
                type = ImageType.LOGO,
                position = posters.count().plus(backdrops.count()).plus(index)
            )
        }
        imagePersistence.upsert(posters + backdrops + logos)
    }
}
