package org.michaelbel.movies.settings.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object SettingsEventManager {

    data object PinWidget

    private val _eventChannel = Channel<Any>()
    val eventFlow: Flow<Any> = _eventChannel.receiveAsFlow()

    suspend fun push(event: Any) {
        _eventChannel.send(event)
    }
}