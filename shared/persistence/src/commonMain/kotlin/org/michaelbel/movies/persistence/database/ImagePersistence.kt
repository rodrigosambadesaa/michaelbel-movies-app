package org.michaelbel.movies.persistence.database

import org.michaelbel.movies.persistence.database.entity.pojo.ImagePojo
import org.michaelbel.movies.persistence.database.ktx.imageDb

class ImagePersistence(
    private val moviesDatabase: MoviesDatabase
) {

    suspend fun upsert(images: List<ImagePojo>) {
        moviesDatabase.imageDao.upsert(images.map(ImagePojo::imageDb))
    }
}
