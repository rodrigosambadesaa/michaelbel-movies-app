package org.michaelbel.movies.main.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.michaelbel.movies.analytics.di.moviesAnalyticsKoinModule
import org.michaelbel.movies.domain.usecase.di.useCaseKoinModule
import org.michaelbel.movies.interactor.di.interactorKoinModule
import org.michaelbel.movies.main.MainViewModel
import org.michaelbel.movies.work.di.workManagerInteractorKoinModule

val mainKoinModule = module {
    includes(
        interactorKoinModule,
        moviesAnalyticsKoinModule,
        useCaseKoinModule,
        workManagerInteractorKoinModule
    )
    viewModelOf(::MainViewModel)
}
