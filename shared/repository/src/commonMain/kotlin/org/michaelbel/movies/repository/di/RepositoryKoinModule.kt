package org.michaelbel.movies.repository.di

import org.koin.dsl.module
import org.michaelbel.movies.network.di.networkKoinModule
import org.michaelbel.movies.persistence.database.di.persistenceKoinModule
import org.michaelbel.movies.persistence.datastore.di.moviesPreferencesKoinModule

val repositoryKoinModule = module {
    includes(
        networkKoinModule,
        persistenceKoinModule,
        moviesPreferencesKoinModule
    )
}
