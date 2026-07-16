package org.michaelbel.movies.domain.usecase.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.michaelbel.movies.common.dispatchers.di.dispatchersKoinModule
import org.michaelbel.movies.domain.usecase.ImagesFlowUseCase
import org.michaelbel.movies.domain.usecase.MovieFlowUseCase
import org.michaelbel.movies.domain.usecase.MoviesFlowUseCase
import org.michaelbel.movies.domain.usecase.SuggestionPojosFlowUseCase
import org.michaelbel.movies.persistence.database.MoviesDatabase
import org.michaelbel.movies.persistence.database.dao.ImageDao
import org.michaelbel.movies.persistence.database.dao.MovieDao
import org.michaelbel.movies.persistence.database.dao.SuggestionDao
import org.michaelbel.movies.persistence.database.di.moviesDatabaseKoinModule

val useCaseKoinModule = module {
    includes(
        dispatchersKoinModule,
        moviesDatabaseKoinModule
    )
    single<SuggestionDao> { get<MoviesDatabase>().suggestionDao }
    single<ImageDao> { get<MoviesDatabase>().imageDao }
    single<MovieDao> { get<MoviesDatabase>().movieDao }
    singleOf(::SuggestionPojosFlowUseCase)
    singleOf(::ImagesFlowUseCase)
    singleOf(::MovieFlowUseCase)
    singleOf(::MoviesFlowUseCase)
}
