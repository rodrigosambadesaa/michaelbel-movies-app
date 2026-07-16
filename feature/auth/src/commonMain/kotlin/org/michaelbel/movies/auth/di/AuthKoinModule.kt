package org.michaelbel.movies.auth.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.michaelbel.movies.auth.AuthViewModel
import org.michaelbel.movies.domain.usecase.di.useCaseKoinModule
import org.michaelbel.movies.interactor.di.interactorKoinModule

val authKoinModule = module {
    includes(
        useCaseKoinModule,
        interactorKoinModule
    )
    viewModelOf(::AuthViewModel)
}
