package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any?,
    key2: Any?,
    onEvent: (T) -> Unit
) {
    LaunchedEffect(flow, key1, key2) {
        flow.collect(onEvent)
    }
}

@Composable
actual fun <T> StateFlow<T>.collectAsStateCommon(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State,
    context: CoroutineContext
): State<T> = collectAsState()
