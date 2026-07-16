package org.michaelbel.movies.main.tabs.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.michaelbel.movies.domain.usecase.di.useCaseKoinModule
import org.michaelbel.movies.interactor.di.interactorKoinModule
import org.michaelbel.movies.main.tabs.MainTabsViewModel

val mainTabsKoinModule = module {
    includes(
        useCaseKoinModule,
        interactorKoinModule
    )
    viewModelOf(::MainTabsViewModel)
}
