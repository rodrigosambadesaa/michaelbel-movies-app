package org.michaelbel.movies.common.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface SharedDispatchers {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val immediate: CoroutineDispatcher
    val ui: CoroutineDispatcher
}
