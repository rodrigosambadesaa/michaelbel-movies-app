package org.michaelbel.movies.persistence.di

import org.koin.dsl.module
import org.michaelbel.movies.persistence.datastore.di.moviesPreferencesKoinModule

val persistenceWebKoinModule = module {
    includes(moviesPreferencesKoinModule)
}
