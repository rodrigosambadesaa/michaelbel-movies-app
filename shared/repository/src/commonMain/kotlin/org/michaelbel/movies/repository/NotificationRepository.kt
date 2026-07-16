package org.michaelbel.movies.repository

interface NotificationRepository {

    suspend fun updateNotificationExpireTime()
}
