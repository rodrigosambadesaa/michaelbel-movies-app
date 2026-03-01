package org.michaelbel.movies.notify.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.michaelbel.movies.interactor.di.interactorKoinModule
import org.michaelbel.movies.notify.NotifyViewModel

val notifyKoinModule = module {
    includes(
        interactorKoinModule
    )
    viewModelOf(::NotifyViewModel)
}
