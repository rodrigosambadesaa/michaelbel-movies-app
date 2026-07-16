package org.michaelbel.movies.common.dispatchers.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.dispatchers.ioDispatcher
import org.michaelbel.movies.common.dispatchers.uiDispatcher

class SharedDispatchersImpl: SharedDispatchers {

    override val default: CoroutineDispatcher
        get() = Dispatchers.Default

    override val io: CoroutineDispatcher
        get() = ioDispatcher

    override val main: CoroutineDispatcher
        get() = Dispatchers.Main

    override val immediate: CoroutineDispatcher
        get() = Dispatchers.Main.immediate

    override val ui: CoroutineDispatcher
        get() = uiDispatcher
}
