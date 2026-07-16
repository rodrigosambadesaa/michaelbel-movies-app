package org.michaelbel.movies.gallery

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.domain.usecase.ImagesFlowUseCase
import org.michaelbel.movies.domain.usecase.ImagesUseCase
import org.michaelbel.movies.gallery.event.GalleryEvent
import org.michaelbel.movies.gallery.intent.GalleryIntent
import org.michaelbel.movies.gallery.model.GalleryModel
import org.michaelbel.movies.ui.navigation.GalleryDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.work.WorkInfoState
import org.michaelbel.movies.work.WorkManagerInteractor

class GalleryViewModel(
    private val destination: GalleryDestination,
    private val workManagerInteractor: WorkManagerInteractor,
    private val imagesFlowUseCase: ImagesFlowUseCase,
    private val imagesUseCase: ImagesUseCase
): MoviesViewModel<GalleryModel, GalleryIntent, GalleryEvent>(GalleryModel()) {

    init {
        dispatch(GalleryIntent.CollectMovieImages)
        dispatch(GalleryIntent.LoadMovieImages)
    }

    override fun dispatch(intent: GalleryIntent) {
        when (intent) {
            is GalleryIntent.CollectMovieImages -> {
                launch {
                    imagesFlowUseCase(destination.movieId).collectLatest { pojos ->
                        reduce { it.copy(imagePojos = pojos) }
                    }
                }
            }
            is GalleryIntent.BackClick -> launch { MainNavigator.back() }
            is GalleryIntent.LoadMovieImages -> {
                launch { imagesUseCase(destination.movieId).getOrThrow() }
            }
            is GalleryIntent.DownloadClick -> {
                launch {
                    workManagerInteractor.downloadImage(intent.image).collectLatest { workInfoState ->
                        when (workInfoState) {
                            is WorkInfoState.Success, is WorkInfoState.Failure -> {
                                push(GalleryEvent.DownloadResult(workInfoState))
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}
