package org.michaelbel.movies.feed.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.michaelbel.movies.common.mvi.Event

object FeedEventManager {

    private val _eventChannel = Channel<Event>()
    val eventFlow: Flow<Event> = _eventChannel.receiveAsFlow()

    suspend fun push(event: Event) {
        _eventChannel.send(event)
    }
}
