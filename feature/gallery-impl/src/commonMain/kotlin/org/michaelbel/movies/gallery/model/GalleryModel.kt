package org.michaelbel.movies.gallery.model

import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.persistence.database.entity.pojo.ImagePojo
import org.michaelbel.movies.work.WorkInfoState

data class GalleryModel(
    val movieImages: List<ImagePojo> = emptyList(),
    val workInfoState: WorkInfoState = WorkInfoState.None
): Model