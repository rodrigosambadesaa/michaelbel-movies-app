package org.michaelbel.movies.fave.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.michaelbel.movies.fave.FaveViewModel
import org.michaelbel.movies.interactor.di.interactorKoinModule
import org.michaelbel.movies.network.connectivity.di.connectivityKoinModule

val faveKoinModule = module {
    includes(
        interactorKoinModule,
        connectivityKoinModule
    )
    viewModelOf(::FaveViewModel)
}
