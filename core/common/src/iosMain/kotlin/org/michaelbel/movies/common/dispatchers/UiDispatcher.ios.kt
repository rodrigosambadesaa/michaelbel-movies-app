package org.michaelbel.movies.common.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val uiDispatcher: CoroutineDispatcher = Dispatchers.Default
