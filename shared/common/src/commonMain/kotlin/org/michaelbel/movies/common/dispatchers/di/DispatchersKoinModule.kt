package org.michaelbel.movies.common.dispatchers.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.dispatchers.impl.SharedDispatchersImpl

val dispatchersKoinModule = module {
    singleOf(::SharedDispatchersImpl) { bind<SharedDispatchers>() }
}
