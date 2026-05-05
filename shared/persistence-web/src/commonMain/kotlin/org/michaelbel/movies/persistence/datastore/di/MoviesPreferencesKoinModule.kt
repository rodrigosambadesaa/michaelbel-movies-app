package org.michaelbel.movies.persistence.datastore.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

val moviesPreferencesKoinModule = module {
    singleOf(::MoviesPreferences)
}
