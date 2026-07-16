package org.michaelbel.movies.interactor

interface NotificationInteractor {

    suspend fun updateNotificationExpireTime()

    suspend fun resetNotificationExpireTime()
}
