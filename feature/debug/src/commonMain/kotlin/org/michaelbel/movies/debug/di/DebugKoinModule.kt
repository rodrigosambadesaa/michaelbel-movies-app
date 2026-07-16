package org.michaelbel.movies.debug.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.michaelbel.movies.debug.DebugViewModel
import org.michaelbel.movies.domain.usecase.di.useCaseKoinModule
import org.michaelbel.movies.interactor.di.interactorKoinModule

val debugKoinModule = module {
    includes(
        useCaseKoinModule,
        interactorKoinModule
    )
    viewModelOf(::DebugViewModel)
}
