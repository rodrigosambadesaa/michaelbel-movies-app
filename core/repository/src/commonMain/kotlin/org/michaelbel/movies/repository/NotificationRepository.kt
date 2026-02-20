package org.michaelbel.movies.repository

interface NotificationRepository {

    suspend fun notificationExpireTime(): Long

    suspend fun updateNotificationExpireTime()

    suspend fun resetNotificationExpireTime()
}
