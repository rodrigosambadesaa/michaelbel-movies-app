package org.michaelbel.movies.gallery.intent

import org.michaelbel.movies.common.mvi.Intent
import org.michaelbel.movies.persistence.database.entity.pojo.ImagePojo

sealed interface GalleryIntent: Intent {
    data object CollectMovieImages: GalleryIntent
    data object BackClick: GalleryIntent
    data object LoadMovieImages: GalleryIntent
    data class DownloadClick(val image: ImagePojo): GalleryIntent
}