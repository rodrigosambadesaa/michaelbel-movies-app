package org.michaelbel.movies.gallery.event

import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.work.WorkInfoState

sealed interface GalleryEvent: Event {
    data class DownloadResult(val workInfoState: WorkInfoState): GalleryEvent
}
