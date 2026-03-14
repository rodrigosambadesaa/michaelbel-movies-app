package org.michaelbel.movies.fave.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.michaelbel.movies.fave.FaveViewModel
import org.michaelbel.movies.interactor.di.interactorKoinModule

val faveKoinModule = module {
    includes(
        interactorKoinModule
    )
    viewModelOf(::FaveViewModel)
}
