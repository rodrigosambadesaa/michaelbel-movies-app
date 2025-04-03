package org.michaelbel.movies.ui.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object MainNavigator {

    private val _destChannel = Channel<Any>()
    val destFlow: Flow<Any> = _destChannel.receiveAsFlow()

    suspend fun forward(element: Any) {
        _destChannel.send(element)
    }

    suspend fun back() {
        _destChannel.send(BackDestination)
    }
}