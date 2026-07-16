package org.michaelbel.movies.domain.usecase.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.michaelbel.movies.common.dispatchers.di.dispatchersKoinModule
import org.michaelbel.movies.domain.usecase.AccountDetailsUseCase
import org.michaelbel.movies.domain.usecase.AccountExpireTimeUseCase
import org.michaelbel.movies.domain.usecase.AccountIdUseCase
import org.michaelbel.movies.domain.usecase.AccountPojoFlowUseCase
import org.michaelbel.movies.domain.usecase.ImagesFlowUseCase
import org.michaelbel.movies.domain.usecase.ImagesUseCase
import org.michaelbel.movies.domain.usecase.MovieFlowUseCase
import org.michaelbel.movies.domain.usecase.MoviesFlowUseCase
import org.michaelbel.movies.domain.usecase.NotificationExpireTimeUseCase
import org.michaelbel.movies.domain.usecase.ResetNotificationExpireTimeUseCase
import org.michaelbel.movies.domain.usecase.SuggestionPojosFlowUseCase
import org.michaelbel.movies.domain.usecase.UpdateNotificationExpireTimeUseCase
import org.michaelbel.movies.domain.usecase.UpdateSuggestionsUseCase
import org.michaelbel.movies.network.di.networkKoinModule
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.dao.AccountDao
import org.michaelbel.movies.persistence.database.dao.ImageDao
import org.michaelbel.movies.persistence.database.dao.MovieDao
import org.michaelbel.movies.persistence.database.dao.SuggestionDao
import org.michaelbel.movies.persistence.database.di.moviesDatabaseKoinModule
import org.michaelbel.movies.persistence.database.di.persistenceKoinModule
import org.michaelbel.movies.persistence.datastore.di.moviesPreferencesKoinModule

val useCaseKoinModule = module {
    includes(
        dispatchersKoinModule,
        moviesDatabaseKoinModule,
        moviesPreferencesKoinModule,
        persistenceKoinModule,
        networkKoinModule
    )
    single<SuggestionDao> { get<MoviesDatabase>().suggestionDao }
    single<ImageDao> { get<MoviesDatabase>().imageDao }
    single<MovieDao> { get<MoviesDatabase>().movieDao }
    single<AccountDao> { get<MoviesDatabase>().accountDao }
    singleOf(::SuggestionPojosFlowUseCase)
    singleOf(::ImagesFlowUseCase)
    singleOf(::ImagesUseCase)
    singleOf(::MovieFlowUseCase)
    singleOf(::MoviesFlowUseCase)
    singleOf(::AccountPojoFlowUseCase)
    singleOf(::AccountIdUseCase)
    singleOf(::AccountExpireTimeUseCase)
    singleOf(::AccountDetailsUseCase)
    singleOf(::NotificationExpireTimeUseCase)
    singleOf(::ResetNotificationExpireTimeUseCase)
    singleOf(::UpdateNotificationExpireTimeUseCase)
    singleOf(::UpdateSuggestionsUseCase)
}
