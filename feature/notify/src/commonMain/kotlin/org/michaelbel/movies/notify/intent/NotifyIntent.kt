package org.michaelbel.movies.notify.intent

import org.michaelbel.movies.common.mvi.Intent

sealed interface NotifyIntent: Intent {
    data object DismissRequest: NotifyIntent
}
