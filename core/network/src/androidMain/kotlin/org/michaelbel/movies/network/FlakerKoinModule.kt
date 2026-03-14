package org.michaelbel.movies.network

import io.github.rotbolt.flakerokhttpcore.FlakerInterceptor
import org.koin.dsl.module

val flakerKoinModule = module {
    single<FlakerInterceptor> { FlakerInterceptor.Builder().build() }
}
