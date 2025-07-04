package org.michaelbel.movies.common.mvi

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.common.viewmodel.CoroutineViewModel

abstract class MoviesViewModel<S: Model, I: Intent>(
    initialState: S
): CoroutineViewModel() {

    private val _stateFlow = MutableStateFlow(initialState)
    val stateFlow: StateFlow<S> = _stateFlow

    private val _eventChannel = Channel<Any>()
    val eventFlow: Flow<Any> = _eventChannel.receiveAsFlow()

    protected fun reduce(reducer: (S) -> S) {
        _stateFlow.update(reducer)
    }

    protected suspend fun push(event: Any) {
        _eventChannel.send(event)
    }

    abstract fun dispatch(intent: I)
}