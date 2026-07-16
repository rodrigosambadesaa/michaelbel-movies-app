package org.michaelbel.movies.gallery.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.michaelbel.movies.domain.usecase.di.useCaseKoinModule
import org.michaelbel.movies.gallery.GalleryViewModel
import org.michaelbel.movies.interactor.di.interactorKoinModule
import org.michaelbel.movies.work.di.workManagerInteractorKoinModule
import org.michaelbel.movies.ui.navigation.GalleryDestination

val galleryKoinModule = module {
    includes(
        useCaseKoinModule,
        interactorKoinModule,
        workManagerInteractorKoinModule
    )
    viewModel { (destination: GalleryDestination) ->
        GalleryViewModel(destination, get(), get(), get())
    }
}
