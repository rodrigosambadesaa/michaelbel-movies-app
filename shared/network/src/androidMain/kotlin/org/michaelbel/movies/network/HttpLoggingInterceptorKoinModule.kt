package org.michaelbel.movies.network

import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

val httpLoggingInterceptorKoinModule = module {
    single<HttpLoggingInterceptor> { HttpLoggingInterceptor() }
}
