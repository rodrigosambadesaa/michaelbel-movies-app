package org.michaelbel.movies.repository.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.michaelbel.movies.network.di.networkKoinModule
import org.michaelbel.movies.persistence.database.di.persistenceKoinModule
import org.michaelbel.movies.persistence.datastore.di.moviesPreferencesKoinModule
import org.michaelbel.movies.repository.PagingKeyRepository
import org.michaelbel.movies.repository.impl.PagingKeyRepositoryImpl

val repositoryKoinModule = module {
    includes(
        networkKoinModule,
        persistenceKoinModule,
        moviesPreferencesKoinModule
    )
    singleOf(::PagingKeyRepositoryImpl) { bind<PagingKeyRepository>() }
}
