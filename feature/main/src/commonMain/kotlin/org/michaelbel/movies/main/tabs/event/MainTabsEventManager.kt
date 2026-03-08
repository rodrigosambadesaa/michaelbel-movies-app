package org.michaelbel.movies.main.tabs.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.michaelbel.movies.common.mvi.Event

object MainTabsEventManager {

    private val _eventChannel = Channel<Event>()
    val eventFlow: Flow<Event> = _eventChannel.receiveAsFlow()

    suspend fun push(event: Event) {
        _eventChannel.send(event)
    }
}
