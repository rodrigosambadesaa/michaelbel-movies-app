package org.michaelbel.movies.domain.usecase.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.michaelbel.movies.common.dispatchers.di.dispatchersKoinModule
import org.michaelbel.movies.domain.usecase.AccountExpireTimeUseCase
import org.michaelbel.movies.domain.usecase.AccountIdUseCase
import org.michaelbel.movies.domain.usecase.AccountPojoFlowUseCase
import org.michaelbel.movies.domain.usecase.ImagesFlowUseCase
import org.michaelbel.movies.domain.usecase.MovieFlowUseCase
import org.michaelbel.movies.domain.usecase.MoviesFlowUseCase
import org.michaelbel.movies.domain.usecase.SuggestionPojosFlowUseCase
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.dao.AccountDao
import org.michaelbel.movies.persistence.database.dao.ImageDao
import org.michaelbel.movies.persistence.database.dao.MovieDao
import org.michaelbel.movies.persistence.database.dao.SuggestionDao
import org.michaelbel.movies.persistence.database.di.moviesDatabaseKoinModule
import org.michaelbel.movies.persistence.datastore.di.moviesPreferencesKoinModule

val useCaseKoinModule = module {
    includes(
        dispatchersKoinModule,
        moviesDatabaseKoinModule,
        moviesPreferencesKoinModule
    )
    single<SuggestionDao> { get<MoviesDatabase>().suggestionDao }
    single<ImageDao> { get<MoviesDatabase>().imageDao }
    single<MovieDao> { get<MoviesDatabase>().movieDao }
    single<AccountDao> { get<MoviesDatabase>().accountDao }
    singleOf(::SuggestionPojosFlowUseCase)
    singleOf(::ImagesFlowUseCase)
    singleOf(::MovieFlowUseCase)
    singleOf(::MoviesFlowUseCase)
    singleOf(::AccountPojoFlowUseCase)
    singleOf(::AccountIdUseCase)
    singleOf(::AccountExpireTimeUseCase)
}
