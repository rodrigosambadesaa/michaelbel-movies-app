package org.michaelbel.movies.interactor.impl

import org.michaelbel.movies.interactor.AppNotificationInteractor
import org.michaelbel.movies.interactor.NotificationInteractor
import org.michaelbel.movies.interactor.model.MoviesPush

class AppNotificationInteractorImpl(
    private val notificationInteractor: NotificationInteractor
): AppNotificationInteractor {

    override suspend fun notificationsPermissionRequired(): Boolean {
        return false
    }

    override suspend fun updateNotificationExpireTime() {
        notificationInteractor.updateNotificationExpireTime()
    }

    override fun send(push: MoviesPush) {}

    override fun sendDownloadImageNotification(
        notificationId: Int,
        contentTitleRes: Int,
        contentTextRes: Int
    ) {}

    override fun cancelDownloadImageNotification(notificationId: Int) {}
}
