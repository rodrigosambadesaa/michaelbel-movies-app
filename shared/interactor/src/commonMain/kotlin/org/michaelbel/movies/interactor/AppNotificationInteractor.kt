package org.michaelbel.movies.interactor

import org.michaelbel.movies.interactor.model.MoviesPush

interface AppNotificationInteractor {

    suspend fun notificationsPermissionRequired(): Boolean

    suspend fun updateNotificationExpireTime()

    fun send(push: MoviesPush)

    fun sendDownloadImageNotification(
        notificationId: Int,
        contentTitleRes: Int,
        contentTextRes: Int
    )

    fun cancelDownloadImageNotification(notificationId: Int)
}
