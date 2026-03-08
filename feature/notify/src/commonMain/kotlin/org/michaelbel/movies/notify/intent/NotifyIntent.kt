package org.michaelbel.movies.notify.intent

import org.michaelbel.movies.common.mvi.Intent

sealed interface NotifyIntent: Intent {
    data object CollectNotificationsFeatureEnabled: NotifyIntent
    data object ActionClick: NotifyIntent
    data object DismissRequest: NotifyIntent
}
