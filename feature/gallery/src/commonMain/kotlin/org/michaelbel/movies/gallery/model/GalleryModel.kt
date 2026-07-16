package org.michaelbel.movies.gallery.model

import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.persistence.database.entity.pojo.ImagePojo

data class GalleryModel(
    val imagePojos: List<ImagePojo> = emptyList()
): Model
