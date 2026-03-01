package org.michaelbel.movies.notify.event

import org.michaelbel.movies.common.mvi.Event

sealed interface NotifyEvent: Event {
    data object RequestPostNotificationsPermission: NotifyEvent
    data object OpenAppNotificationSettings: NotifyEvent
}
