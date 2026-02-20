package org.michaelbel.movies.main.mainnav.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object MainNavAppEvent {

    sealed interface Event {
        data object OpenFeed: Event
        data object OpenSettings: Event
    }

    private val _eventChannel = Channel<Event>()
    val eventFlow: Flow<Event> = _eventChannel.receiveAsFlow()

    suspend fun push(event: Event) {
        _eventChannel.send(event)
    }
}
