package org.michaelbel.movies.notify

import kotlinx.coroutines.launch
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.interactor.AppNotificationInteractor
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.notify.event.NotifyEvent
import org.michaelbel.movies.notify.intent.NotifyIntent
import org.michaelbel.movies.notify.model.NotifyModel
import org.michaelbel.movies.ui.navigation.MainNavigator

class NotifyViewModel(
    private val uiInteractor: UiInteractor,
    private val appNotificationInteractor: AppNotificationInteractor
): MoviesViewModel<NotifyModel, NotifyIntent, NotifyEvent>(NotifyModel()) {

    init {
        dispatch(NotifyIntent.CollectNotificationsFeatureEnabled)
    }

    override fun dispatch(intent: NotifyIntent) {
        when (intent) {
            is NotifyIntent.CollectNotificationsFeatureEnabled -> reduce { it.copy(isNotificationsFeatureEnabled = uiInteractor.isNotificationsFeatureEnabled) }
            is NotifyIntent.ActionClick -> {
                launch {
                    when {
                        stateFlow.value.isNotificationsFeatureEnabled -> push(NotifyEvent.RequestPostNotificationsPermission)
                        else -> push(NotifyEvent.OpenAppNotificationSettings)
                    }
                    appNotificationInteractor.updateNotificationExpireTime()
                    MainNavigator.back()
                }
            }
            is NotifyIntent.DismissRequest -> {
                launch {
                    appNotificationInteractor.updateNotificationExpireTime()
                    MainNavigator.back()
                }
            }
        }
    }
}
