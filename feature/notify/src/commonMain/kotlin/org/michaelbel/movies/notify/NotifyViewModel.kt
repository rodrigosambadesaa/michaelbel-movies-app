package org.michaelbel.movies.notify

import kotlinx.coroutines.launch
import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.common.mvi.model.EmptyModel
import org.michaelbel.movies.notifications.NotificationClient
import org.michaelbel.movies.notify.intent.NotifyIntent
import org.michaelbel.movies.ui.navigation.MainNavigator

class NotifyViewModel(
    private val notificationClient: NotificationClient
): MoviesViewModel<EmptyModel, NotifyIntent, Event>(EmptyModel) {

    override fun dispatch(intent: NotifyIntent) {
        when (intent) {
            is NotifyIntent.DismissRequest -> {
                launch {
                    notificationClient.updateNotificationExpireTime()
                    MainNavigator.back()
                }
            }
        }
    }
}
